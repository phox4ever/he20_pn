package w04.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientPong {

    public static final int EIGENER_PORT = 10001;
    public static final int GEGENSPIELER_PORT = 10000;
    public static final int SERVER_PORT = 10002;
    public static final String SERVER_HOSTNAME = "localhost";
    public static void main(String[] args) {
        System.out.println("Warten auf den Aufschlag");
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(EIGENER_PORT)) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String response = input.readLine();
                Thread.sleep(1000);
                if (response.equals("Ping")) {
                    System.out.println("Pong");
                    serverSocket.close();
                    Socket socket2 = new Socket(SERVER_HOSTNAME, SERVER_PORT);
                    OutputStream output2 = socket2.getOutputStream();
                    output2.write("Pong\n".getBytes());
                    output2.flush();
                    socket2.close();
                }
                Socket socket = new Socket(SERVER_HOSTNAME, GEGENSPIELER_PORT);
                OutputStream output = socket.getOutputStream();
                output.write("Pong\n".getBytes());
                output.flush();
                socket.close();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
