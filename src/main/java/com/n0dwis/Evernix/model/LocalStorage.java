package com.n0dwis.Evernix.model;

import com.n0dwis.Evernix.utils.UserErrorException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by n0dwis on 23.08.15.
 */
public class LocalStorage {

    private File storeDirectory;
    private List<NotebookInfo> notebooks;

    private DocumentBuilder builder;

    public LocalStorage(File dir, List<NotebookInfo> notebooks) {
        this.storeDirectory = dir;
        this.notebooks = notebooks;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setValidating(false);
            builder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error creating builder", e);
        }
    }

    public List<NotebookInfo> getNotebooks() {
        return notebooks;
    }

    public void addNotebook(NotebookInfo notebook) {
        notebooks.add(notebook);
        File notebookDir = new File(storeDirectory.getAbsolutePath() + "/" + notebook.getName());
        if (!notebookDir.exists()) {
            notebookDir.mkdir();
        }
    }


    public String getNoteContent(NoteInfo note) {
        File noteFile = getNoteFile(note);
        if (!noteFile.exists()) {
            throw new RuntimeException("Note path " + noteFile.getAbsolutePath() + " does not exist.");
        }

        try {
            return new String(Files.readAllBytes(Paths.get(noteFile.getAbsolutePath())));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file" + noteFile.getAbsolutePath(), e);
        }
    }


    public NotebookInfo findNotebook(String id) {
        for (NotebookInfo ni : notebooks) {
            if (ni.getId().equals(id)) {
                return ni;
            }
        }

        return null;
    }


    public void saveNoteContent(NoteInfo note, String content) throws UserErrorException {
        File noteFile = getNoteFile(note);
        try {
            Files.write(Paths.get(noteFile.getAbsolutePath()), content.getBytes());
        } catch (IOException e) {
            throw new UserErrorException("Can't write file " + noteFile.getAbsolutePath(), e);
        }
    }


    private File getNoteFile(NoteInfo note) {
        return new File(storeDirectory.getAbsolutePath() + "/" + note.getNotebook().getName() + "/" + note.getName());
    }
}
