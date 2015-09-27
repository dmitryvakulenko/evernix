package com.n0dwis.Evernix.utils;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;

/**
 * Created by n0dwis on 26.09.15.
 */
public class StyledDocumentFormatter implements Runnable {

    private Element line;

    public StyledDocumentFormatter(Element currentLine) {
        line = currentLine;
    }

    public void run() {
        DefaultStyledDocument doc = (DefaultStyledDocument) line.getDocument();
        String lineContent = "";
        try {
            lineContent = doc.getText(line.getStartOffset(), line.getEndOffset() - line.getStartOffset());
        } catch (BadLocationException e) {

        }

        if (lineContent.isEmpty()) {
            return;
        }

        if (lineContent.charAt(0) == '*') {
            doc.setLogicalStyle(line.getStartOffset(), doc.getStyle("header"));
        } else {
            doc.setLogicalStyle(line.getStartOffset(), doc.getStyle("text"));
        }
    }
}
