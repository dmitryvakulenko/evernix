package com.n0dwis.Evernix.utils;

import org.junit.Before;
import org.junit.Test;

import javax.swing.text.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by n0dwis on 26.09.15.
 */
public class StyledDocumentFormatterTest {

    @Test
    public void testEmptyDocument() {
        Element curLine = createElementMock("", 0);

        DefaultStyledDocument doc = (DefaultStyledDocument) curLine.getDocument();
        Style style = mock(Style.class);
        when(doc.getStyle("text")).thenReturn(style);

        StyledDocumentFormatter formatter = new StyledDocumentFormatter(curLine);
        formatter.run();
        verify(doc, never()).setLogicalStyle(anyInt(), (Style) anyObject());
    }

    @Test
    public void testSimpleString() {
        int startOffset = 0;
        Element curLine = createElementMock("hello world", startOffset);
        DefaultStyledDocument doc = (DefaultStyledDocument) curLine.getDocument();
        Style style = mock(Style.class);
        when(doc.getStyle("text")).thenReturn(style);

        StyledDocumentFormatter formatter = new StyledDocumentFormatter(curLine);

        formatter.run();

        verify(doc).setLogicalStyle(startOffset, style);
    }


    @Test
    public void testHeaders() {
        int startOffset = 0;
        Element curLine = createElementMock("*** header 3", startOffset);
        DefaultStyledDocument doc = (DefaultStyledDocument) curLine.getDocument();
        Style style = mock(Style.class);
        when(doc.getStyle("header")).thenReturn(style);

        StyledDocumentFormatter formatter = new StyledDocumentFormatter(curLine);

        formatter.run();

        verify(doc).setLogicalStyle(startOffset, style);
    }


    private Element createElementMock(String content, int startOffset) {
        Element curLine = mock(Element.class);
        DefaultStyledDocument doc = mock(DefaultStyledDocument.class);

        int endOffset = startOffset + content.length();
        when(curLine.getDocument()).thenReturn(doc);
        when(curLine.getStartOffset()).thenReturn(startOffset);
        when(curLine.getEndOffset()).thenReturn(endOffset);

        try {
            when(doc.getText(startOffset, endOffset)).thenReturn(content);
        } catch (BadLocationException e) {
            assertTrue(false);
        }

        return curLine;
    }
}