package atl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * UserInput is a Runnable that is used to read user input from the console.
 *
 * @version 1.0
 * @author philipp.martin@hf-ict.info
 */
public class UserInput implements Runnable {

    private Canvas canvas;

    private GameOfLife game;

    public UserInput(Canvas canvas, GameOfLife game) {
        this.canvas = canvas;
        this.game = game;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                char c = (char) br.read();
                if (c == 'q') {
                    System.exit(0);
                    break;
                }
                if (c == 'k' || c == 'w') {
                    canvas.moveUp();
                }
                if (c == 'j' || c == 's') {
                    canvas.moveDown();
                }
                if (c == 'h'  || c == 'a') {
                    canvas.moveLeft();
                }
                if (c == 'l' || c == 'd') {
                    canvas.moveRight();
                }
                if (c == 'i') {
                    canvas.zoomIn();
                }
                if (c == 'o') {
                    canvas.zoomOut();
                }
                if (c > '0' && c <= '9') {
                    game.setSubTaskCount(c - '0');
                    canvas.setTaskCount(c - '0');
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
