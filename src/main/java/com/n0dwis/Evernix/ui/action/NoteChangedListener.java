package com.n0dwis.Evernix.ui.action;

import com.n0dwis.Evernix.utils.StyledDocumentFormatter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;

/**
 * Created by n0dwis on 21.09.15.
 */
public class NoteChangedListener implements DocumentListener {

    public void insertUpdate(DocumentEvent evt) {
        final DefaultStyledDocument doc = (DefaultStyledDocument) evt.getDocument();
        final Element line = doc.getParagraphElement(evt.getOffset());
        SwingUtilities.invokeLater(new StyledDocumentFormatter(line));
    }

    public void removeUpdate(DocumentEvent evt) {
        final DefaultStyledDocument doc = (DefaultStyledDocument) evt.getDocument();
        final Element line = doc.getParagraphElement(evt.getOffset());
        SwingUtilities.invokeLater(new StyledDocumentFormatter(line));
    }

    public void changedUpdate(DocumentEvent evt) {
        final DefaultStyledDocument doc = (DefaultStyledDocument) evt.getDocument();
        final Element line = doc.getParagraphElement(evt.getOffset());
        SwingUtilities.invokeLater(new StyledDocumentFormatter(line));
    }
}
