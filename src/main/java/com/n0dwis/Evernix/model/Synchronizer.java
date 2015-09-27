package com.n0dwis.Evernix.model;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.n0dwis.Evernix.service.StoreConfig;
import com.n0dwis.Evernix.utils.NoteFormatConverter;
import com.n0dwis.Evernix.utils.SystemException;
import com.n0dwis.Evernix.utils.UserErrorException;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Date;
import java.util.List;

public class Synchronizer {

    LocalStorage localStorage;
    private NoteStoreClient noteStore;

    public Synchronizer(LocalStorage localStorage, String token) {
        this.localStorage = localStorage;
        noteStore = createNoteStoreClient(token);
    }

    private NoteStoreClient createNoteStoreClient(String token) {
        EvernoteAuth auth = new EvernoteAuth(EvernoteService.SANDBOX, token);
        ClientFactory factory = new ClientFactory(auth);
        try {
            return factory.createNoteStoreClient();
        } catch (Exception e) {
            throw new RuntimeException("Evernote error", e);
        }
    }

    public List<NotebookInfo> notebooks() {
        return localStorage.getNotebooks();
    }

    public void synchronize() {
        try {
            synchronizeImpl();
        } catch (Exception e) {
            throw new RuntimeException("Error synchronize.", e);
        }
    }

    public String getNoteText(NoteInfo note) {
        return localStorage.getNoteContent(note);
    }

    public void saveNoteText(NoteInfo note, String content) throws UserErrorException, SystemException {
        NoteFormatConverter converter = new NoteFormatConverter();
        localStorage.saveNoteContent(note, content);
        try {
            Note enNote = noteStore.getNote(note.getId(), true, false, false, false);
            enNote.setContent(converter.fromLocalToEvernote(content));
            noteStore.updateNote(enNote);
        } catch (EDAMNotFoundException e) {
            throw new UserErrorException("Can't get evernote note", e);
        } catch (EDAMUserException e) {
            throw new UserErrorException("Can't get evernote note", e);
        } catch (EDAMSystemException e) {
            throw new UserErrorException("Can't get evernote note", e);
        } catch (TException e) {
            throw new UserErrorException("Can't get evernote note", e);
        }
    }

    private void synchronizeImpl() throws EDAMUserException, EDAMSystemException, TException, UserErrorException {
        List<Notebook> remoteNotebooks = noteStore.listNotebooks();
        StoreConfig config = StoreConfig.getInstance();
        NoteFormatConverter converter = new NoteFormatConverter();
        for (Notebook notebook : remoteNotebooks) {
            NotebookInfo localNotebook = localStorage.findNotebook(notebook.getGuid());
            if (localNotebook == null) {
                localNotebook = new NotebookInfo(notebook.getGuid(), notebook.getName());
                localStorage.addNotebook(localNotebook);
            } else {
                localNotebook.setName(notebook.getName());
            }

            File notebookDir = new File(config.getBaseDirectory().getAbsolutePath() + "/" + localNotebook.getName());
            if (!notebookDir.exists()) {
                notebookDir.mkdir();
            }

            NoteFilter filter = new NoteFilter();
            filter.setNotebookGuid(notebook.getGuid());
            try {
                NoteList notesList = noteStore.findNotes(filter, 0, 10000);
                for (Note note : notesList.getNotes()) {
                    NoteInfo localNote = localNotebook.findNote(note.getGuid());
                    if (localNote == null) {
                        localNote = new NoteInfo(note.getGuid(), note.getTitle(), new Date());
                        localNotebook.addNote(localNote);
                    } else {
                        localNote.setName(note.getTitle());
                    }

                    File localNoteFile = new File(notebookDir.getAbsolutePath() + "/" + note.getTitle());
                    if (!localNoteFile.exists()) {
                        localNoteFile.createNewFile();
                    }

                    FileOutputStream fio = new FileOutputStream(localNoteFile);
                    Note fullNote = noteStore.getNote(note.getGuid(), true, false, false, false);
                    fio.write(converter.fromEvernoteToLocal(fullNote.getContent()).getBytes());
                    fio.close();
                }
            } catch (EDAMNotFoundException e) {
                throw new RuntimeException("Evernote error", e);
            } catch (IOException e) {
                throw new RuntimeException("Can't create file", e);
            }
        }
    }

    public void close() {
        StoreConfig.getInstance().save();
    }

}
