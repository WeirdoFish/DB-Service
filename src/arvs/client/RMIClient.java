package arvs.client;

import arvs.client.visualisation.GUI;
import arvs.dbreq.DBNote;
import arvs.server.RMIServerIntf;
import helma.xmlrpc.XmlRpc;
import helma.xmlrpc.XmlRpcClient;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class RMIClient extends Thread {

    private static RMIClient cli;
    private static RMIServerIntf obj;

    static private String user = "";
    static private Boolean authorized = false;

    static private GUI gui;
    static private JRadioButton flagTCP;
    static private JRadioButton flagUDP;
    static private JRadioButton flagXML;
    static private JRadioButton flagRMI;
    static private JMenuItem auth;
    static private JMenuItem register;
    static private JTextArea user_area = new JTextArea();
    static private JTextArea passw_area = new JTextArea();
    static private JLabel label = new JLabel();
    static private JButton addButton;
    static private JButton delButton;
    static private JButton updButton;
    static private JButton showButton;
    static private JButton clearButton;
    static private JTextArea noteField;
    static private JTextField titleField;
    static private JList titlesList;

    public static void main(String args[]) throws SocketException, IOException {

        obj = null;
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
            System.out.println("policy : " + System.getProperty("java.security.policy"));
        }

        cli = new RMIClient();

        try {
            obj = (RMIServerIntf) Naming.lookup("rmi://localhost/RMIServer");

        } catch (Exception e) {
            System.err.println("RMIClient exception: " + e);
            e.printStackTrace();

        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                gui = new GUI();
                gui.setVisible(true);
                initEvents();
            }
        }
        );
    }

    static private void updateLabel() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.changeLabel(user);
            }
        });
    }

    static private void updateList(String[] str) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.changeLabel(user);
                gui.updList(str);
            }
        });
    }

    static public void initEvents() {
        flagTCP = gui.getRadioTCP();
        flagUDP = gui.getRadioUDP();
        flagXML = gui.getRadioXML();
        flagRMI = gui.getRadioRMI();
        flagTCP.setVisible(false);
        flagUDP.setVisible(false);
        flagXML.setVisible(false);
        flagRMI.setVisible(false);

        clearButton = gui.getClearBut();
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                titleField.setText("");
                noteField.setText("");

            }
        });

        auth = gui.getAuth();
        register = gui.getRegister();

        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFrame regFrame = new JFrame();
                regFrame.setVisible(true);
                regFrame.setSize(new Dimension(200, 160));
                JPanel pane = new JPanel();
                regFrame.add(pane);

                JLabel uLabel = new JLabel("Введите имя пользователя");
                JLabel pLabel = new JLabel("Введите пароль");
                JButton regBut = new JButton();
                regBut.setText("Сохранить");
                user_area.setColumns(10);
                passw_area.setColumns(10);
                pane.add(uLabel);
                pane.add(user_area);
                pane.add(pLabel);
                pane.add(passw_area);
                pane.add(regBut);

                regBut.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (!user_area.getText().equals("") && !passw_area.getText().equals("")) {
                            String username = user_area.getText();
                            user_area.setColumns(1);
                            passw_area.setColumns(1);
                            String passw = passw_area.getText();
                            boolean flag = false;
                            try {
                                flag = obj.reg(username, passw);
                            } catch (RemoteException ex) {
                                Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (!flag) {
                                JOptionPane.showMessageDialog(regFrame, "Имя пользователя существует!");
                            } else {
                                user_area.setText("");
                                passw_area.setText("");
                                authorized = true;
                                user = username;
                                updateLabel();
                                try {
                                    getNotesList();
                                } catch (RemoteException ex) {
                                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                regFrame.setVisible(false);
                            }
                        }
                    }
                });
                System.out.println("new user");

            }
        });

        auth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFrame regFrame = new JFrame();
                regFrame.setVisible(true);
                regFrame.setSize(new Dimension(200, 160));
                JPanel pane = new JPanel();
                regFrame.add(pane);

                JLabel uLabel = new JLabel("Введите имя пользователя");
                JLabel pLabel = new JLabel("Введите пароль");
                JButton regBut = new JButton();
                //regFrame.setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);

                regBut.setText("Войти");
                user_area.setColumns(10);
                passw_area.setColumns(10);
                pane.add(uLabel);
                pane.add(user_area);
                pane.add(pLabel);
                pane.add(passw_area);
                pane.add(regBut);

                regBut.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (!user_area.getText().equals("") && !passw_area.getText().equals("")) {
                            String username = user_area.getText();
                            String passw = passw_area.getText();
                            boolean flag = false;
                            try {
                                flag = obj.check(username, passw);
                            } catch (RemoteException ex) {
                                Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (!flag) {
                                JOptionPane.showMessageDialog(regFrame, "Неверные данные!");
                            } else {
                                user_area.setText("");
                                passw_area.setText("");
                                authorized = true;
                                user = username;
                                updateLabel();
                                System.out.println(user);
                                regFrame.setVisible(false);
                                try {
                                    getNotesList();
                                } catch (RemoteException ex) {
                                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                });

            }
        });

        titlesList = gui.getList();
        addButton = gui.getAddBut();
        noteField = gui.getNoteArea();
        titleField = gui.getTitleArea();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!authorized) {
                    JOptionPane.showMessageDialog(gui, "Необходимо авторизоваться!");
                    return;
                }
                String title = titleField.getText();
                String text = noteField.getText();
                if (!title.equals("") && !text.equals("")) {

                    DBNote req = new DBNote();
                    req.setName(user);
                    req.setText(text);
                    req.setTitle(title);
                    try {
                        obj.add(req);
                    } catch (RemoteException ex) {
                        Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        getNotesList();
                    } catch (RemoteException ex) {
                        Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    JOptionPane.showMessageDialog(gui, "Заполните поля!");
                }
            }
        });

        showButton = gui.getShowBut();
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!authorized) {
                    JOptionPane.showMessageDialog(gui, "Необходимо авторизоваться!");
                    return;
                }
                String selected = titlesList.getSelectedValue().toString();
                if (selected == null) {
                    JOptionPane.showMessageDialog(gui, "Вы ничего не выбрали!");
                    return;
                }
                String[] tmp = selected.split(" ", 2);
                DBNote note;
                try {
                    note = obj.note(tmp[0]);
                    titleField.setText(note.getTitle());
                    noteField.setText(note.getText());
                } catch (RemoteException ex) {
                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        delButton = gui.getDelBut();
        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!authorized) {
                    JOptionPane.showMessageDialog(gui, "Необходимо авторизоваться!");
                    return;
                }
                String selected = titlesList.getSelectedValue().toString();
                if (selected == null) {
                    JOptionPane.showMessageDialog(gui, "Вы ничего не выбрали!");
                    return;
                }
                String[] tmp = selected.split(" ", 2);
                boolean flag = false;
                try {
                    flag = obj.del(tmp[0]);
                } catch (RemoteException ex) {
                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (flag) {
                    JOptionPane.showMessageDialog(gui, "Запись успешно удалена");
                } else {
                    JOptionPane.showMessageDialog(gui, "Не удалось удалить запись");
                }
                try {
                    getNotesList();
                } catch (RemoteException ex) {
                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        updButton = gui.getUpdBut();
        updButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!authorized) {
                    JOptionPane.showMessageDialog(gui, "Необходимо авторизоваться!");
                    return;
                }
                String title = titleField.getText();
                String text = noteField.getText();
                String selected = titlesList.getSelectedValue().toString();
                if (!title.equals("") && !text.equals("") && selected != null) {
                    String[] id = selected.split(" ", 2);

                    boolean flag = false;
                    try {
                        flag = obj.upd(id[0], title, text);
                    } catch (RemoteException ex) {
                        Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (flag) {
                        JOptionPane.showMessageDialog(gui, "Успешно обновлено");
                    } else {
                        JOptionPane.showMessageDialog(gui, "Не удалось обновить запись");
                    }
                    try {
                        getNotesList();
                    } catch (RemoteException ex) {
                        Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    JOptionPane.showMessageDialog(gui, "Заполните поля и выберите запись!");
                }
            }
        });
    }

    static private void getNotesList() throws RemoteException {
        String[] answ = obj.titles(user);
        if (answ[0].equals("fail")) {
            String[] empty = {"empty"};
            updateList(empty);
            return;
        }
        updateList(answ);
    }
}
