import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.text.ParseException;

public class ViewGUIClient {
    private JTextField textField;
    private JTextArea area;
    private Socket socket;
    private static BufferedReader in; // входящие сообщения
    private static PrintWriter out; // отправка (послать) сообщения
    private JLabel infoConnect;
    private JFormattedTextField PORT;
    private JFrame frame;

    public void windowClient() {
        frame = new JFrame();
        JPanel panel = new JPanel();
        JPanel panelLeft = new JPanel();

        frame.setTitle("Клиент-чат ");

        infoConnect = new JLabel("НЕ ПОДКЛЮЧЕН");
        infoConnect.setFont(new Font("Impact", Font.PLAIN, 12));
        infoConnect.setForeground(new Color(199, 84, 80));
        infoConnect.setPreferredSize(new Dimension(99, 50));

        JLabel labelAddress = new JLabel("IP add ");
        labelAddress.setFont(new Font("Impact", Font.PLAIN, 13));
        JTextField HOST = new JTextField("localhost", 9);
        HOST.setFont(new Font("Impact", Font.PLAIN, 13));
        HOST.setPreferredSize(new Dimension(0, 25));
        JLabel labelPort = new JLabel("Port ");
        labelPort.setFont(new Font("Impact", Font.PLAIN, 13));

        try {
            MaskFormatter portFormatter = new MaskFormatter("****");
            portFormatter.setPlaceholderCharacter('*');

            PORT = new JFormattedTextField(portFormatter);
            PORT.setColumns(4);
            PORT.setText("9000");
            PORT.setFont(new Font("Impact", Font.PLAIN, 13));
            PORT.setPreferredSize(new Dimension(35, 25));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        JButton btConnect = new JButton("Connect");
        btConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int intPort = Integer.parseInt(PORT.getText());
                    connectionNetworking(HOST.getText(), intPort);
                    Thread runClientThread = new Thread(new ReadMessage());
                    runClientThread.start();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Некорректный ввод Порта." + "\n" + "Порт должно состоять из 4 чисел.");
                    infoConnect.setText("НЕ ПОДКЛЮЧЕН");
                    infoConnect.setForeground(new Color(199, 84, 80));
                }
            }
        });

        JButton btDisc = new JButton("Disconnect");
        btDisc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    socket.close();
                    in.close();
                    out.close();
                    infoConnect.setText("НЕ ПОДКЛЮЧЕН");
                    infoConnect.setForeground(new Color(199, 84, 80));
                    area.append("Нет соединение к серверу." + "\n");
                } catch (IOException | NullPointerException ex) {
                    area.append("Нет соединение к серверу." + "\n");
                    infoConnect.setText("НЕ ПОДКЛЮЧЕН");
                    infoConnect.setForeground(new Color(199, 84, 80));
                }
            }
        });

        area = new JTextArea(16, 42);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(new Font("Impact", Font.PLAIN, 15));

        JScrollPane scroller = new JScrollPane(area);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JLabel labelTextMessage = new JLabel("Write Message");
        labelTextMessage.setFont(new Font("Impact", Font.HANGING_BASELINE, 12));
        labelTextMessage.setForeground(Color.GRAY);
        labelTextMessage.setPreferredSize(new Dimension(500, 25));

        textField = new JTextField(43);
        textField.setFont(new Font("Impact", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(0, 25));

        JButton button = new JButton("Send");
        button.setPreferredSize(new Dimension(70, 25));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    out.println(textField.getText());
                    out.flush();
                    textField.setText("");
                    textField.requestFocus();
                } catch (NullPointerException ex) {
                    area.append("Нет соединение к серверу." + "\n");
                }
            }
        });
        panel.add(infoConnect);
        panel.add(labelAddress);
        panel.add(HOST);
        panel.add(labelPort);
        panel.add(PORT);
        panel.add(btConnect);
        panel.add(btDisc);

        panel.add(scroller);
        panel.add(labelTextMessage);
        panel.add(textField);
        panel.add(button);

        JLabel labelListConnect = new JLabel("Список подключений");
        labelListConnect.setFont(new Font("Impact", Font.PLAIN, 13));
        labelListConnect.setPreferredSize(new Dimension(20, 49));

        JTextArea areaList = new JTextArea();
        areaList.setLineWrap(true);
        areaList.setWrapStyleWord(true);
        areaList.setEditable(true);
        areaList.append(" Список подключений:" + "\n");
        areaList.setPreferredSize(new Dimension(150, 434));
        areaList.setFont(new Font("Impact", Font.PLAIN, 13));
        JScrollPane scrollerLeft = new JScrollPane(areaList);
        scrollerLeft.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollerLeft.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelLeft.add(scrollerLeft);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.getContentPane().add(BorderLayout.WEST, panelLeft);
        frame.setSize(730, 495);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void connectionNetworking(String HOST, int POST) {
        try {
            socket = new Socket(HOST, POST);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            infoConnect.setText("ПОДКЛЮЧЕН");
            infoConnect.setForeground(new Color(74, 143, 82));
            area.append("Есть подключение к серверу." + "\n");
        } catch (IOException e) {
            System.out.println("Cервер не обнаружен");
            infoConnect.setText("НЕ ПОДКЛЮЧЕН");
            infoConnect.setForeground(new Color(199, 84, 80));
            area.append("Нет соединение к серверу." + "\n");
            JOptionPane.showMessageDialog(frame, "Проверьте правильность ввода" + "\n" + "IP адреса и Порта");
        }
    }
    public class ReadMessage implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while (((message = in.readLine()) != null)) {
                    area.append(message + "\n");
                }
            } catch (IOException | NullPointerException e) {
                System.out.println("Нет подключения и нет сообщений");
                infoConnect.setForeground(new Color(199, 84, 80));
                area.append("Сервер отключился!" + "\n");
            }
        }
    }
}

