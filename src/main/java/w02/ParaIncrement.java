package w02;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author s@scalingbits.com
 */
public class ParaIncrement extends Thread {
    public static int zaehler=0;
    public static final int  MAX= Integer.MAX_VALUE/100;

    /**
     * increment with potential conflicts (overwriting)
     * Add "synchronized" as key word to make it save (and slow)
     */
    public static void increment() { zaehler++; }

     /**
     * Starten des Threads
     */
     @Override
    public void run() {
        for (int i=0; i < MAX; i++) {
            increment();
        }
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        ParaIncrement thread1 = new ParaIncrement();
        ParaIncrement thread2 = new ParaIncrement();
        long time = System.nanoTime();
        thread1.start();
        try {
            thread1.join();
            thread2.start();
            thread2.join();
        } catch (InterruptedException e) {
        }
        time = (System.nanoTime() -time)/1000000L; // time in milliseconds
        if ((2* ParaIncrement.MAX) == ParaIncrement.zaehler)
            System.out.println("Korrekte Ausführung: " +
                    + ParaIncrement.zaehler + " (" + time + "ms)");
        else
            System.out.println("Fehler! Soll: " + (2* ParaIncrement.MAX) +
                    "; Ist: " +ParaIncrement.zaehler + " (" + time + "ms)");
    }
}
