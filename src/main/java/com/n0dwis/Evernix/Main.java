package com.n0dwis.Evernix;


import com.n0dwis.Evernix.model.Synchronizer;
import com.n0dwis.Evernix.ui.MainWindow;

import javax.swing.*;
import java.io.IOException;

//  -Djavax.net.debug=all
public class Main {

    public static MainWindow mainWindow;
    public static Synchronizer synchronizedStore;

    public static void main(String[] args) throws IOException {
        Config.getInstance();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow = new MainWindow();
                mainWindow.setVisible(true);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Config.getInstance().saveConfig();
                if (synchronizedStore != null) {
                    synchronizedStore.close();
                }
            }
        });
    }

}
