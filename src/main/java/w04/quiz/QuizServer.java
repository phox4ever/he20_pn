package w04.quiz;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizServer {
    public static void main(String[] args) {
        String[] questions = {
                "Wie heißt die Hauptstadt von Deutschland?",
                "Wie heißt die Hauptstadt von Frankreich?",
                "Wie heißt die Hauptstadt von Italien?",
                "Wie heißt die Hauptstadt von Spanien?",
                "Wie heißt die Hauptstadt von Österreich?",
                "Wie heißt die Hauptstadt von Polen?",
                "Wie heißt die Hauptstadt von Tschechien?",
                "Wie heißt die Hauptstadt von Ungarn?",
                "Wie heißt die Hauptstadt von der Schweiz?",
                "Wie heißt die Hauptstadt von den Niederlanden?"
        };

        String[][] answers = {
                {"Berlin", "Hamburg", "München", "Köln"},
                {"Paris", "Lyon", "Marseille", "Toulouse"},
                {"Rom", "Mailand", "Neapel", "Turin"},
                {"Madrid", "Barcelona", "Valencia", "Sevilla"},
                {"Wien", "Graz", "Linz", "Salzburg"},
                {"Warschau", "Krakau", "Lodz", "Breslau"},
                {"Prag", "Brünn", "Ostrau", "Pilsen"},
                {"Budapest", "Debrecen", "Szeged", "Miskolc"},
                {"Bern", "Genf", "Basel", "Zürich"},
                {"Amsterdam", "Rotterdam", "Den Haag", "Utrecht"}
        };

        try {
            ServerSocket serverSocket = new ServerSocket(3141);
            System.out.println("Server gestartet. Warte auf Verbindungen...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client verbunden: " + clientSocket.getInetAddress().getHostAddress());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                for (int i = 0; i < questions.length; i++) {
                    // Frage und Antworten an den Client senden

                    String[] shuffled = Arrays.copyOf(answers[i], answers[i].length);
                    List<String> answersShuffled = Arrays.asList(shuffled);
                    Collections.shuffle(answersShuffled);

                    out.writeObject(questions[i]);
                    out.writeObject(shuffled);
                    // Antwort vom Client empfangen und auswerten

                    String clientAnswer = (String) in.readObject();
                    String response;
                    if (clientAnswer.equals(answers[i][0])) {
                        response = "Richtig!";
                    } else {
                        response = "Falsch!";
                    }
                    System.out.println("Client: " + clientAnswer + " - " + response);

                    // Quittung an den Client senden
                    out.writeObject(response);
                }
                in.close();
                out.close();
                clientSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
