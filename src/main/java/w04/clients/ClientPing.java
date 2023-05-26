package w04.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientPing {

    public static final int EIGENER_PORT = 10000;
    public static final int GEGENSPIELER_PORT = 10001;
    public static final int SERVER_PORT = 10002;
    public static final String SERVER_HOSTNAME = "localhost";
    public static void main(String[] args) {
        System.out.println("Das Spiel beginnt mit dem Aufschlag");
        System.out.println("Ping");
        while (true) {
            try (Socket socket = new Socket(SERVER_HOSTNAME, GEGENSPIELER_PORT)) {
                OutputStream output = socket.getOutputStream();
                output.write("Ping\n".getBytes());
                output.flush();
                socket.close();
                ServerSocket serverSocket = new ServerSocket(EIGENER_PORT);
                Socket clientSocket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String response = input.readLine();
                Thread.sleep(1000);
                if (response.equals("Pong")) {
                    System.out.println("Ping");
                    serverSocket.close();
                    Socket socket2 = new Socket(SERVER_HOSTNAME, SERVER_PORT);
                    OutputStream output2 = socket2.getOutputStream();
                    output2.write("Ping\n".getBytes());
                    output2.flush();
                    socket2.close();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
