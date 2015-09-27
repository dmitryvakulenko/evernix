package com.n0dwis.Evernix.ui;

import com.n0dwis.Evernix.Main;
import com.n0dwis.Evernix.model.NotebookInfo;
import com.n0dwis.Evernix.ui.action.ConnectNewAccount;
import com.n0dwis.Evernix.ui.action.OpenExistsAccount;
import com.n0dwis.Evernix.ui.action.NoteSelectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    private JTree notesTree;
    private JTextPane noteEditor;

    public MainWindow() throws HeadlessException {
        super("Evernix");
        setupGui();
        notesListChanged();
    }

    private void setupGui() {
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        createLayout();
        add(createMenu(), BorderLayout.NORTH);
        createNotesView();
    }

    private BorderLayout createLayout() {
        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        return layout;
    }

    private JMenuBar createMenu() {
        JMenuBar mainMenu = new JMenuBar();

        JMenu accountMenu = new JMenu("Account");
        JMenuItem connect = new JMenuItem("Connect");
        connect.addActionListener(new ConnectNewAccount());

        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(new OpenExistsAccount());
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        accountMenu.add(connect);
        accountMenu.add(open);
        accountMenu.addSeparator();
        accountMenu.add(exit);
        mainMenu.add(accountMenu);

        return mainMenu;
    }

    private void createNotesView() {
        notesTree = new JTree();
        notesTree.setModel(new NotesTreeModel(new ArrayList<NotebookInfo>()));
        notesTree.updateUI();
        noteEditor = new JTextPane();

        notesTree.setMinimumSize(new Dimension(0, 300));
        notesTree.addTreeSelectionListener(new NoteSelectionListener(noteEditor));

        noteEditor.setPreferredSize(new Dimension(0, 400));
        noteEditor.setEditable(true);
        noteEditor.setMargin(new Insets(5, 5, 5, 5));
        noteEditor.setCaretPosition(0);

        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, notesTree, noteEditor), BorderLayout.CENTER);
    }

    public void notesListChanged() {
        if (Main.synchronizedStore == null) {
            notesTree.setEnabled(false);
            noteEditor.setEnabled(false);
        } else {
            notesTree.setEnabled(true);
            notesTree.setModel(new NotesTreeModel(Main.synchronizedStore.notebooks()));
            notesTree.updateUI();
        }
    }
}
