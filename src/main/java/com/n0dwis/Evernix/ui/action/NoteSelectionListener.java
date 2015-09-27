package com.n0dwis.Evernix.ui.action;

import com.n0dwis.Evernix.Main;
import com.n0dwis.Evernix.model.NoteInfo;
import com.n0dwis.Evernix.model.NotebookInfo;
import com.n0dwis.Evernix.utils.NoteViewConverter;
import com.n0dwis.Evernix.utils.SystemException;
import com.n0dwis.Evernix.utils.UserErrorException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

/**
 * Created by n0dwis on 06.09.15.
 */
public class NoteSelectionListener implements TreeSelectionListener {

    private JTextPane editor;

    private NoteInfo curSelectedNote = null;

    public NoteSelectionListener(JTextPane editor) {
        this.editor = editor;
    }

    public void valueChanged(TreeSelectionEvent evt) {
        TreePath selPath = evt.getNewLeadSelectionPath();
        Object selected = selPath.getLastPathComponent();
        if (selected instanceof NotebookInfo) {
            return;
        }

        if (curSelectedNote != null) {
            StyledDocument doc = (StyledDocument) editor.getDocument();
            try {
                Main.synchronizedStore.saveNoteText(curSelectedNote, doc.getText(0, doc.getLength()));
            } catch (BadLocationException e) {
                throw new RuntimeException("Error getting text", e);
            } catch (UserErrorException e) {
                throw new RuntimeException("Error saving note", e);
            } catch (SystemException e) {
                throw new RuntimeException("Error saving note", e);
            }
        }
        curSelectedNote = (NoteInfo) selected;
        NoteViewConverter conv = new NoteViewConverter();
        StyledDocument doc = conv.createDocument(Main.synchronizedStore.getNoteText((NoteInfo) selected));
        editor.setStyledDocument(doc);
        editor.setEnabled(true);
        editor.updateUI();
    }
}
