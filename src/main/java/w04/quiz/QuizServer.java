package w04.quiz;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class QuizServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(3141);
            System.out.println("Server gestartet. Warte auf Verbindungen...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client verbunden: " + clientSocket.getInetAddress().getHostAddress());

                // Frage und Antworten erstellen
                String question = "Welches Protokoll verliert keine Pakete?";
                String[] answers = {"TCP", "UDP", "IP"};

                // Frage und Antworten an den Client senden
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject(question);
                out.writeObject(answers);

                // Antwort des Clients empfangen
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                String clientAnswer = (String) in.readObject();

                // Antwort überprüfen und Quittung an den Client senden
                String correctAnswer = "TCP";
                String response;
                if (clientAnswer.equals(correctAnswer)) {
                    response = "Richtige Antwort!";
                } else {
                    response = "Falsche Antwort!";
                }
                out.writeObject(response);

                // Verbindungen schließen
                in.close();
                out.close();
                clientSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
