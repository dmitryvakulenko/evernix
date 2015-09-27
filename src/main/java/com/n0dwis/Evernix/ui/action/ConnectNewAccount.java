package com.n0dwis.Evernix.ui.action;

import com.n0dwis.Evernix.Main;
import com.n0dwis.Evernix.model.Synchronizer;
import com.n0dwis.Evernix.service.StoreConfig;
import com.n0dwis.Evernix.ui.LoginDialog;
import com.n0dwis.Evernix.utils.EvernoteOAuth;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ConnectNewAccount implements ActionListener {

    public void actionPerformed(ActionEvent evt) {
        LoginDialog dialog = new LoginDialog(Main.mainWindow);
        dialog.setModal(true);
        dialog.setVisible(true);

        if (dialog.isOk()) {

            try {
                EvernoteOAuth oauth = new EvernoteOAuth();
                String token = oauth.authorize(dialog.getLogin(), dialog.getPassword());
                Main.synchronizedStore = StoreConfig.getInstance().create(new File(dialog.getDirectory()), token);
                Main.synchronizedStore.synchronize();
                Main.mainWindow.notesListChanged();
            } catch (Exception e) {
                throw new RuntimeException("Evernote error", e);
            }
        }
    }

}
