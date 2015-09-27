package com.n0dwis.Evernix.utils;

import com.n0dwis.Evernix.ui.action.NoteChangedListener;

import javax.swing.text.*;

/**
 * Created by n0dwis on 21.09.15.
 */
public class NoteViewConverter {

    public StyledDocument createDocument(String note) {
        DefaultStyledDocument doc = createEmptyDocument();

        int offset = 0;
        String[] noteStrings = note.split("\n");
        for (String curString : noteStrings) {
            try {
                doc.insertString(offset, curString, doc.getStyle("text"));
            } catch (BadLocationException e) {
                throw new RuntimeException("Can't set style", e);
            }
            offset += curString.length();
        }

        return doc;
    }

    private DefaultStyledDocument createEmptyDocument() {
        DefaultStyledDocument doc = new DefaultStyledDocument();

        Style text = doc.addStyle("text", null);
        StyleConstants.setFontFamily(text, "monospace");

        Style header = doc.addStyle("header", text);
        StyleConstants.setBold(header, true);

        doc.addDocumentListener(new NoteChangedListener());

        return doc;
    }
}
