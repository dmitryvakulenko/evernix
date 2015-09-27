package com.n0dwis.Evernix.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {

    private JTextField login;
    private JTextField password;
    private JTextField directory;

    private boolean isOk = false;

    public LoginDialog(Frame owner) {
        super(owner, "Login Evernote");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        makeUi();
        pack();
        setLocationRelativeTo(owner);
    }

    private void makeUi() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel loginPane = createPane();
        loginPane.setLayout(new BoxLayout(loginPane, BoxLayout.Y_AXIS));
        add(loginPane);
        JLabel label = new JLabel("Login");
        loginPane.add(label);
        login = new JTextField();
        login.setAlignmentX(LEFT_ALIGNMENT);
        login.setPreferredSize(new Dimension(225, 25));
        loginPane.add(login);

        JPanel passwordPane = createPane();
        passwordPane.setLayout(new BoxLayout(passwordPane, BoxLayout.Y_AXIS));
        add(passwordPane);
        passwordPane.add(new JLabel("Password"));
        password = new JTextField();
        password.setAlignmentX(LEFT_ALIGNMENT);;
        password.setPreferredSize(new Dimension(225, 25));
        passwordPane.add(password);

        JPanel dirPane = createPane();
        add(dirPane);
        dirPane.add(new JLabel("Directory"));
        JPanel dirSelectPane = new JPanel();
        dirSelectPane.setAlignmentX(LEFT_ALIGNMENT);
        dirPane.add(dirSelectPane);
        dirSelectPane.setLayout(new BoxLayout(dirSelectPane, BoxLayout.X_AXIS));
        directory = new JTextField();
        dirSelectPane.add(directory);
        JButton dirSelBtn = new JButton("...");
        dirSelectPane.add(dirSelBtn);

        final JDialog self = this;
        dirSelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(self);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    directory.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        JPanel buttonsPane = new JPanel();
        buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.X_AXIS));
        buttonsPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonsPane.setAlignmentX(LEFT_ALIGNMENT);
        buttonsPane.add(Box.createHorizontalGlue());

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isOk = true;
                setVisible(false);
            }
        });
        buttonsPane.add(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isOk = false;
                setVisible(false);
            }
        });
        buttonsPane.add(cancelBtn);
        add(buttonsPane);
    }

    private JPanel createPane() {
        JPanel pane = new JPanel();
        pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        return pane;
    }


    public String getLogin() {
        return login.getText();
    }

    public String getPassword() {
        return password.getText();
    }

    public String getDirectory() {
        return directory.getText();
    }

    public boolean isOk() {
        return isOk;
    }
}
