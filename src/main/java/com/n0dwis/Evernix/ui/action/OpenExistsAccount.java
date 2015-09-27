package com.n0dwis.Evernix.ui.action;

import com.n0dwis.Evernix.Main;
import com.n0dwis.Evernix.model.Synchronizer;
import com.n0dwis.Evernix.service.StoreConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by n0dwis on 20.08.15.
 */
public class OpenExistsAccount implements ActionListener {

    public void actionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(Main.mainWindow);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                Main.synchronizedStore = StoreConfig.getInstance().load(new File(chooser.getSelectedFile().getAbsolutePath()));
                Main.synchronizedStore.synchronize();
                Main.mainWindow.notesListChanged();
            } catch (Exception e) {
                throw new RuntimeException("Evernote error", e);
            }
        }
    }

}
