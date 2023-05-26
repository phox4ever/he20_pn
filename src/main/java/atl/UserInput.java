package atl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

public class UserInput implements Runnable {

    protected Canvas canvas;

    public UserInput(Canvas canvas) {
        this.canvas = canvas;
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
