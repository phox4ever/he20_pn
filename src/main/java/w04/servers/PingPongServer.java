package w04.servers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class PingPongServer {
    public static final int SERVER_PORT = 10002;
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String response = input.readLine();
                if (response.equals("Ping") || response.equals("Pong")) {
                    System.out.println(response);
                } else {
                    System.out.println("Wrong input");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
