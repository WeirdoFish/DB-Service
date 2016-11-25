package arvs.client;

import arvs.client.visualisation.GUI;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
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

class Client extends Thread {

    static Integer port = 8411;
    static String host = "localhost";
    static private Boolean isTcp = true;
    static private String spl = "#-#";

    static private String user = "";
    static private Boolean authorized = false;

    static private GUI gui;
    static private JRadioButton flagTCP;
    static private JRadioButton flagUDP;
    static private JMenuItem auth;
    static private JMenuItem register;
    static private JTextArea user_area = new JTextArea();
    static private JTextArea passw_area = new JTextArea();
    static private JLabel label = new JLabel();
    static private JButton addButton;
    static private JButton showButton;
    static private JButton udpListButton;
    static private JTextArea noteField;
    static private JTextField titleField;
    static private JList titlesList;

    public static void main(String args[]) throws SocketException, IOException {
        // String dataSend = "1" + spl + "user" + spl + "passw";
        //sendTCP(dataSend);
        //sendUDP(dataSend);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                gui = new GUI();
                gui.setVisible(true);
                initEvents();
                //update();
            }
        });
    }

    static private void updateLabel() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.changeLabel(user);
            }
        });
    }
    
     static private void updateList(DefaultListModel listModel) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.changeLabel(user);
                gui.updList(listModel);
            }
        });
    }

    static public void initEvents() {
        flagTCP = gui.getRadioTCP();
        flagUDP = gui.getRadioUDP();

        flagTCP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                isTcp = true;
                flagTCP.setSelected(true);
                flagUDP.setSelected(false);
                System.out.println("tcp now");
            }
        });

        flagUDP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                isTcp = false;
                flagUDP.setSelected(true);
                flagTCP.setSelected(false);
                System.out.println("udp now");
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
                            String passw = passw_area.getText();
                            String req = "reg" + spl + username + spl + passw;
                            String res = send(req);
                            if (res == null || res.equals("fail")) {
                                JOptionPane.showMessageDialog(regFrame, "Имя пользователя существует!");
                            } else {
                                user_area.setText("");
                                passw_area.setText("");
                                authorized = true;
                                user = username;

                                //label.setText("dd");
                                //gui.changeLabel("dd");
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
                            String req = "auth" + spl + username + spl + passw;
                            String res = send(req);
                            if (res == null || res.equals("fail")) {
                                JOptionPane.showMessageDialog(regFrame, "Неверные данные!");
                            } else {
                                user_area.setText("");
                                passw_area.setText("");
                                authorized = true;
                                user = username;
                                updateLabel();
                                System.out.println(user);
                                label.setText(username);
                                regFrame.setVisible(false);
                                getNotesList();
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
                String title = titleField.getText();
                String text = noteField.getText();
                if (!title.equals("") && !text.equals("")) {
                    String req = "add" + spl + user + spl + title + spl + text;
                    System.out.println(send(req));
                    getNotesList();
                } else {
                    JOptionPane.showMessageDialog(gui, "Заполните поля!");
                }
            }
        });
    }

    static private void getNotesList() {
        String req = "titles" + spl + user;
        String answ = send(req);
        String[] parsed = answ.split(spl);
        DefaultListModel listModel = new DefaultListModel();

        for (int i = 0; i < parsed.length - 3; i += 3) {
            String tmp = parsed[i] + " " + parsed[i + 1] + " " + parsed[i + 2];
            listModel.addElement(tmp);
        }
        updateList(listModel);
        //titlesList.setModel(listModel);
        //titlesList = new JList(listModel);
        //titlesList.setLayoutOrientation(JList.VERTICAL);
    }

    static public String sendUDP(String str) {
        try {
            byte[] data = str.getBytes();

            InetAddress addr = InetAddress.getByName(host);
            DatagramPacket pack
                    = new DatagramPacket(data, data.length, addr, port);
            DatagramSocket udpSocket = new DatagramSocket();
            udpSocket.send(pack);
//ответ
            byte[] buf = new byte[64 * 1024];
            DatagramPacket answ = new DatagramPacket(buf, buf.length);
            udpSocket.receive(answ);
            String answData = new String(answ.getData());

//вывод, далее - вызов функции-обработчика
            System.out.println(answData + "resevd");
            udpSocket.close();
            return answData;
        } catch (Exception e) {
            System.out.println("init error: " + e);
            return null;
        }
    }

    static public String sendTCP(String str) {
        try {
            Socket tcpSocket = new Socket(host, port);
            tcpSocket.getOutputStream().write(str.getBytes());

            // читаем ответ
            byte buf[] = new byte[64 * 1024];
            int r = tcpSocket.getInputStream().read(buf);
            String data = new String(buf, 0, r);

            // выводим ответ в консоль
            System.out.println(data);
            return data;
        } catch (Exception e) {
            System.out.println("init error: " + e);
            return null;
        } // вывод исключений
    }

    static public String send(String str) {
        if (isTcp) {
            return sendTCP(str);
        } else {
            return sendUDP(str);
        }
    }
}
