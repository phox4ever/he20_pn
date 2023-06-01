package w04.quiz;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class QuizClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 3141);
            System.out.println("Verbindung zum Server hergestellt.");
            int count = 0;
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            while(count < 10) {
                // Frage und Antworten vom Server empfangen
                String question = (String) in.readObject();
                String[] answers = (String[]) in.readObject();

                // Frage und Antworten anzeigen
                System.out.println("Frage: " + question);
                System.out.println("Antwortmöglichkeiten:");
                for (int i = 0; i < answers.length; i++) {
                    System.out.println((i + 1) + ". " + answers[i]);
                }

                // Antwort einlesen und an den Server senden
                Scanner scanner = new Scanner(System.in);
                System.out.print("Antwort eingeben (1-" + answers.length + "): ");
                int choice = scanner.nextInt();
                String clientAnswer = answers[choice - 1];
                out.writeObject(clientAnswer);
                // Quittung vom Server empfangen und anzeigen
                String response = (String) in.readObject();
                System.out.println("Server: " + response);

                count++;
            }
            // Verbindungen schließen
            in.close();
            out.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
