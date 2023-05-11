package w03;

public class JoinAndSleep extends Thread {

    protected long sleep;
    protected JoinAndSleep toJoin;

    public JoinAndSleep(int sleep, JoinAndSleep toJoin) {
        this.sleep = sleep;
        this.toJoin = toJoin;
    }

    @Override
    public void run() {
        super.run();
        try {
            if (toJoin != null) {
                System.out.println("Thread " + this.getId() + " waiting for Thread " + toJoin.getId());
                toJoin.join();
            }
            sleep(sleep);
            System.out.println("Thread " + this.getId() + " finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JoinAndSleep t3 = new JoinAndSleep(4000, null);
        JoinAndSleep t2 = new JoinAndSleep(3000, t3);
        JoinAndSleep t1 = new JoinAndSleep(2000, t2);

        t1.start();
        t2.start();
        t3.start();
    }


}
