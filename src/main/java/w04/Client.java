package w04;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket server = new Socket("g8.is", 9999);
        InputStream in = server.getInputStream();
        OutputStream out= server.getOutputStream();
        out.write(4);
        out.write(9);
        int result=in.read();
        System.out.println(result);server.close();
    }
}
