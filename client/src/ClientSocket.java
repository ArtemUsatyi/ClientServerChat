import java.io.*;

public class ClientSocket {
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {
        System.out.println("THIS CLIENT");

        ViewGUIClient guiClient = new ViewGUIClient();
        guiClient.windowClient();
    }
}