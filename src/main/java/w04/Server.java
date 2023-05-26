package w04;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(3141);
            while (true) {
                Socket client = serverSocket.accept();
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();
                int zahl1 = in.read();//-128..+127
                int zahl2 = in.read();
                out.write(zahl1*zahl2);
                out.write("\nHallo Du".getBytes());
                client.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
