package com.n0dwis.Evernix.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by n0dwis on 23.08.15.
 */
public class NotebookInfo {

    private String id;
    private String name;
    private List<NoteInfo> notes;

    public NotebookInfo(String id, String name) {
        this.id = id;
        this.name = name;
        notes = new ArrayList<NoteInfo>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NoteInfo> getNotes() {
        return notes;
    }

    public NoteInfo findNote(String id) {
        for (NoteInfo ni : notes) {
            if (ni.getId().equals(id)) {
                return ni;
            }
        }

        return null;
    }

    public void addNote(NoteInfo noteInfo) {
        notes.add(noteInfo);
        noteInfo.setNotebook(this);
    }

    public void removeNote(NoteInfo noteInfo) {
        notes.remove(noteInfo);
    }

    @Override
    public String toString() {
        return getName();
    }
}
