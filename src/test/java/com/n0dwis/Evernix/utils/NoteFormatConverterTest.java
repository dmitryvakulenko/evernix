package com.n0dwis.Evernix.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by n0dwis on 26.09.15.
 */
public class NoteFormatConverterTest {

    private NoteFormatConverter converter;
    private String notePrefix;

    @Before
    public void prepareNote() {
        converter = new NoteFormatConverter();
        notePrefix =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">\n"
                + "<en-note>";

    }

    @Test
    public void emptyNoteToText() {
        String note = notePrefix + "</en-note>";
        String result;
        try {
            result = converter.fromEvernoteToLocal(note);
            assertEquals("", result);
        } catch (UserErrorException e) {
            assertTrue(false);
        }
    }

    @Test
    public void simpleStringToText() {
        String note = notePrefix + "simple text</en-note>";
        String result;
        try {
            result = converter.fromEvernoteToLocal(note);
            assertEquals("simple text", result);
        } catch (UserErrorException e) {
            assertTrue(false);
        }
    }

    @Test
    public void headersToText() {
        String note = notePrefix + "<h1>header 1</h1>"
                + "<h2>header 2</h2>"
                + "<h3>header 3</h3>"
                + "</en-note>";
        String[] result;
        try {
            result = converter.fromEvernoteToLocal(note).split("\n");
            assertEquals(3, result.length);
            assertEquals("* header 1", result[0]);
            assertEquals("** header 2", result[1]);
            assertEquals("*** header 3", result[2]);

        } catch (UserErrorException e) {
            assertTrue(false);
        }
    }


    @Test
    public void linksToText() {
        String note = notePrefix + "<a href=\"http://google.com\">link</a>"
                + "</en-note>";
        String[] result;
        try {
            result = converter.fromEvernoteToLocal(note).split("\n");
            assertEquals(1, result.length);
            assertEquals("[[http://google.com][link]]", result[0]);

        } catch (UserErrorException e) {
            assertTrue(false);
        }
    }


    @Test
    public void emptyTextToNote() {
        String note = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">\n"
                + "<en-note/>";
        try {
            assertEquals(note, converter.fromLocalToEvernote(""));
        } catch (SystemException e) {
            assertTrue(false);
        }
    }

    @Test
    public void simpleStringTextToNote() {
        String text = "hello world";
        String note = notePrefix + text + "</en-note>";
        try {
            assertEquals(note, converter.fromLocalToEvernote(text));
        } catch (SystemException e) {
            assertTrue(false);
        }
    }

    @Test
    public void headersTextToNote() {
        String text = "* header 1\n"
                + "** header 2\n"
                + "*** header 3";
        String note = notePrefix + "<h1>header 1</h1><h2>header 2</h2><h3>header 3</h3></en-note>";
        try {
            assertEquals(note, converter.fromLocalToEvernote(text));
        } catch (SystemException e) {
            assertTrue(false);
        }
    }
}
