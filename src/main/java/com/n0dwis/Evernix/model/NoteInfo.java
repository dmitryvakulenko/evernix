package com.n0dwis.Evernix.model;

import java.util.Date;

public class NoteInfo {
    private String id;
    private String name;
    private Date lastSync;
    private NotebookInfo notebook;

    public NoteInfo(String id, String name, Date lastSync) {
        this.id = id;
        this.name = name;
        this.lastSync = lastSync;
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

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }

    @Override
    public String toString() {
        return getName();
    }

    public NotebookInfo getNotebook() {
        return notebook;
    }

    public void setNotebook(NotebookInfo notebook) {
        this.notebook = notebook;
    }
}
