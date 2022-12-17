import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class ViewGUIServer {
    private JTextArea area;
    private JTextArea areaLeft;
    private JLabel labelPort;
    private JFormattedTextField PORT;

    private Server server;

    public ViewGUIServer(Server server){
        this.server = server;
    }

    public void windowsServer() {
        JFrame frame = new JFrame();
        JPanel panelAll = new JPanel();
        JPanel panelListClient = new JPanel();
        JLabel labelConnect = new JLabel();

        area = new JTextArea(20, 32);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(true);
        area.setPreferredSize(new Dimension(370, 250));
        area.setFont(new Font("Impact", Font.PLAIN, 13));
        JScrollPane scroller = new JScrollPane(area);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        areaLeft = new JTextArea(20, 13);
        areaLeft.setLineWrap(true);
        areaLeft.setWrapStyleWord(true);
        areaLeft.setEditable(true);
        areaLeft.append(" Список подключений:" + "\n");
        areaLeft.setPreferredSize(new Dimension(100, 250));
        areaLeft.setFont(new Font("Impact", Font.PLAIN, 13));
        JScrollPane scrollerLeft = new JScrollPane(areaLeft);
        scrollerLeft.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollerLeft.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        labelConnect.setFont(new Font("Impact", Font.PLAIN, 13));
        labelConnect.setForeground(new Color(199, 84, 80));
        labelConnect.setText("Сервер отключен");
        labelConnect.setPreferredSize(new Dimension(117, 30));

        labelPort = new JLabel("PORT");
        labelPort.setFont(new Font("Impact", Font.PLAIN, 13));
        labelPort.setPreferredSize(new Dimension(35, 30));

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
        frame.setTitle("Сервер чат");

        JButton startServer = new JButton("Start");
        startServer.setFont(new Font("Impact", Font.PLAIN, 13));
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int intPort = Integer.parseInt(PORT.getText().trim());
                    server.startServer(intPort);

                    labelConnect.setForeground(new Color(74, 143, 82));
                    labelConnect.setText("Сервер запущен");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Некорректный ввод Порта."+ "\n"+"Порт должно состоять из 4 чисел.");
                }
            }
        });

        JButton stopServer = new JButton("Stop");
        stopServer.setFont(new Font("Impact", Font.PLAIN, 13));
        stopServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stopServer();
                labelConnect.setForeground(new Color(199, 84, 80));
                labelConnect.setText("Сервер отключен");
            }
        });

        panelAll.add(scroller);
        panelAll.add(labelConnect);
        panelAll.add(labelPort);
        panelAll.add(PORT);
        panelAll.add(startServer);
        panelAll.add(stopServer);

        panelListClient.add(scrollerLeft);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER, panelAll);
        frame.getContentPane().add(BorderLayout.WEST, panelListClient);
        frame.setSize(520, 430);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void dialogInfoArea(String text){
        area.append(text+"\n");
    }
}
