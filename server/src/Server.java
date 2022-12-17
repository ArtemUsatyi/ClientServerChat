import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private static PrintWriter out; // отправка (послать) сообщения
    private static boolean isServerStart = false;
    private static ViewGUIServer guiServer;
    private List<PrintWriter> clientOutputStreams = new ArrayList<>();

    public static void main(String[] args) {
        Server server = new Server();
        guiServer = new ViewGUIServer(server);
        guiServer.windowsServer();
        while (true) {
            if (isServerStart) {
                server.acceptServer();
                isServerStart = false;
            }
        }
    }

    public void startServer(int PORT) {
        try {
            server = new ServerSocket(PORT);
            isServerStart = true;
            guiServer.dialogInfoArea("Сервер запущен, порт подключения: " + PORT);
        } catch (IOException e) {
            guiServer.dialogInfoArea("Сервер не запущен");
        }
    }

    protected void stopServer() {
        try {
            if (server != null && !server.isClosed()) {
                server.close();
                socket.close();
                guiServer.dialogInfoArea("Сервер остановлен");
            } else guiServer.dialogInfoArea("Сервер итак не запущен");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void acceptServer() {
        System.out.println("Сервер запущен");
        while (true) {
            try {
                socket = server.accept();
                out = new PrintWriter(socket.getOutputStream());
                Thread t = new Thread(new ClientConnection(socket));
                clientOutputStreams.add(out);
                t.start();

            } catch (Exception e) {
                break;
            }
        }
    }

    private class ClientConnection implements Runnable {
        private Socket clientSocket; // сокет клиента
        private BufferedReader in;  // входящие сообщения

        private ClientConnection(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    sendMessage(message);
                }
            } catch (IOException e) {
                sendMessage("Клиент отключился: "+ socket.getInetAddress());
            }
        }
    }
    private synchronized void sendMessage(String clientText) {
        Iterator iterator = clientOutputStreams.iterator();
        while (iterator.hasNext()) {
            PrintWriter out = (PrintWriter) iterator.next();
            out.println(clientText);
            out.flush();
        }
    }
}