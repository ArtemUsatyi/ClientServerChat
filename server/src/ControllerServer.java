import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ControllerServer {
    private ServerSocket server;
    private Socket socket;
    private PrintWriter out; // отправка сообщений

    public ControllerServer(int PORT) {
        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");
            while (true) {
                try {
                    socket = server.accept();
                    System.out.println("Соединение установлено");
                    System.out.println(socket.getInetAddress() + ": " + socket.getPort());
                    out = new PrintWriter(socket.getOutputStream());

                    Thread t = new Thread(new ClientHandler(socket));
                    t.start();

                } catch (IOException e) {
                    System.out.println("exception" + e);
                }
            }
        } catch (IOException e) {
            System.out.println("отключился");
            throw new RuntimeException(e);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket; // сокет клиента
        private BufferedReader in;  // входящие сообщения

        private ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            String msg;
            try {
                while ((msg = in.readLine()) != null) {
                    sendMessage(msg);
                    System.out.println("От клиента: " + msg);
                }
            } catch (IOException e) {
                System.out.println("Клиент: " + socket.getInetAddress() + ": " + socket.getPort() + " отключился");
            }
        }
    }

    private synchronized void sendMessage(String clientText) {
        out.println(clientText);
        out.flush();
    }
}

