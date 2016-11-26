package arvs.client;

import arvs.client.visualisation.GUI;
import helma.xmlrpc.XmlRpcClient;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Vector;
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
    static private String method = "TCP";
    final static private String spl = "#-#";

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

        flagTCP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                method = "TCP";
                flagTCP.setSelected(true);
                flagUDP.setSelected(false);
                flagRMI.setSelected(false);
                flagXML.setSelected(false);
                System.out.println("tcp now");
            }
        });

        flagUDP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                method = "UDP";
                flagUDP.setSelected(true);
                flagTCP.setSelected(false);

                flagRMI.setSelected(false);
                flagXML.setSelected(false);
                System.out.println("udp now");
            }
        });
        
        flagXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                method = "XML";
                flagUDP.setSelected(false);
                flagTCP.setSelected(false);

                flagRMI.setSelected(false);
                flagXML.setSelected(true);
                System.out.println("xml-rpc now");
            }
        });
        
        flagRMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                method = "RMI";
                flagUDP.setSelected(false);
                flagTCP.setSelected(false);

                flagRMI.setSelected(true);
                flagXML.setSelected(false);
                System.out.println("rmi now");
            }
        });

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
                            String req = "reg" + spl + username + spl + passw;
                            String res = send(req);
                            if (res == null || res.equals("fail")) {
                                JOptionPane.showMessageDialog(regFrame, "Имя пользователя существует!");
                            } else {
                                user_area.setText("");
                                passw_area.setText("");
                                authorized = true;
                                user = username;
                                updateLabel();
                                getNotesList();
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
                if (!authorized) {
                    JOptionPane.showMessageDialog(gui, "Необходимо авторизоваться!");
                    return;
                }
                String title = titleField.getText();
                String text = noteField.getText();
                if (!title.equals("") && !text.equals("")) {
                    if (title.contains(spl)) {
                        JOptionPane.showMessageDialog(gui, "Заголовок не должен содержать '" + spl + "'!");
                    } else {
                        String req = "add" + spl + user + spl + title + spl + text;
                        System.out.println(send(req));
                        getNotesList();
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
                String req = "note" + spl + tmp[0];
                String answ = send(req);

                tmp = answ.split(spl, 2);
                titleField.setText(tmp[0]);
                noteField.setText(tmp[1]);

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
                String req = "del" + spl + tmp[0];
                String answ = send(req);

                tmp = answ.split(spl, 2);
                if (tmp[0].equals("success")) {
                    JOptionPane.showMessageDialog(gui, "Запись успешно удалена");
                } else {
                    JOptionPane.showMessageDialog(gui, "Не удалось удалить запись");
                }
                getNotesList();
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
                    if (title.contains(spl)) {
                        JOptionPane.showMessageDialog(gui, "Заголовок не должен содержать '" + spl + "'!");
                    } else {
                        String req = "upd" + spl + id[0] + spl + title + spl + text;
                        String answ = send(req);
                        if (answ.equals("success")) {
                            JOptionPane.showMessageDialog(gui, "Успешно обновлено");
                        } else {
                            JOptionPane.showMessageDialog(gui, "Не удалось обновить запись");
                        }
                        getNotesList();
                    }
                } else {
                    JOptionPane.showMessageDialog(gui, "Заполните поля и выберите запись!");
                }
            }
        });
    }

    static private void getNotesList() {
        String req = "titles" + spl + user;

        String answ = send(req);
        if (answ.equals("fail")) {
            String[] empty = {"empty"};
            updateList(empty);
            return;
        }
        String[] parsed = answ.split(spl);
        updateList(parsed);
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
            buf = Arrays.copyOf(answ.getData(), answ.getLength());
            String answData = new String(buf);

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

    static public String sendXML(String str) {
        try {
            XmlRpcClient server = new XmlRpcClient("http://localhost:8841");
            Vector params = new Vector();
            params.addElement(str);
            String result = (String) server.execute("service.workWithDB", params);
            return result;
        } catch (Exception e) {
            System.out.println("Smth wrong: " + e);
            return "fail";
        }
    }

    static public String send(String str) {
     
        switch(method){
            case "TCP": return sendTCP(str);
            case "UDP": return sendUDP(str);
            case "XML": return sendXML(str);
            case "RMI": return "later";
            default: return "fail";
        }
       
    }
}
