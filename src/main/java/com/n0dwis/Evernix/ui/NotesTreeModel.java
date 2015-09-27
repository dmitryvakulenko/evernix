package com.n0dwis.Evernix.ui;

import com.n0dwis.Evernix.model.NoteInfo;
import com.n0dwis.Evernix.model.NotebookInfo;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;

public class NotesTreeModel implements TreeModel {

    private List<NotebookInfo> localNotebooks;

    public NotesTreeModel(List<NotebookInfo> notebooks) {
        localNotebooks = notebooks;
    }

    public Object getRoot() {
        return "root";
    }

    public Object getChild(Object parent, int index) {
        if (parent instanceof String) {
            return localNotebooks.get(index);
        }

        if (parent instanceof NotebookInfo) {
            return ((NotebookInfo) parent).getNotes().get(index);
        }

        return null;
    }

    public int getChildCount(Object parent) {
        if (parent instanceof String) {
            return localNotebooks.size();
        }

        if (parent instanceof NotebookInfo) {
            return ((NotebookInfo) parent).getNotes().size();
        }

        return 0;
    }

    public boolean isLeaf(Object node) {
        return node instanceof NoteInfo;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof String) {
            return localNotebooks.indexOf(child);
        }

        if (parent instanceof NotebookInfo) {
            return ((NotebookInfo) parent).getNotes().indexOf(child);
        }

        return 0;
    }

    public void addTreeModelListener(TreeModelListener l) {

    }

    public void removeTreeModelListener(TreeModelListener l) {

    }
}
