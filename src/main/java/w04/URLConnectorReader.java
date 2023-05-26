package w04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class URLConnectorReader {
    public static void main(String[] args) throws IOException {
        URL url = new URL("https://www.oracle.com");

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}
