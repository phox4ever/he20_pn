package atl;

class Canvas {
    protected int x;
    protected int y;
    protected int xMax;
    protected int yMax;
    protected int generation;
    protected long timePerGeneration;
    protected long timeTotal;
    protected int alive;
    protected boolean dead;

    protected int threadCount;


    public Canvas(int x, int y, int xMax, int yMax) {
        this.x = x;
        this.y = y;
        this.xMax = xMax;
        this.yMax = yMax;
        this.generation = 0;
        this.timePerGeneration = 0;
        this.timeTotal = 0;
        this.alive = 0;
        this.dead = false;
        this.threadCount = 1;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getxMax() {
        return xMax;
    }

    public void setxMax(int xMax) {
        this.xMax = xMax;
    }

    public int getyMax() {
        return yMax;
    }

    public void setyMax(int yMax) {
        this.yMax = yMax;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public long getTimePerGeneration() {
        return timePerGeneration;
    }

    public void setTimePerGeneration(long timePerGeneration) {
        this.timePerGeneration = timePerGeneration;
    }

    public long getTimeTotal() {
        return timeTotal;
    }

    public void setTimeTotal(long timeTotal) {
        this.timeTotal = timeTotal;
    }

    public int getAlive() {
        return alive;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}
