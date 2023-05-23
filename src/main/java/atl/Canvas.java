package atl;

class Canvas {
    protected int x;
    protected int y;
    protected int xMin;
    protected int yMin;

    protected int xMax;
    protected int yMax;

    protected int generation;
    protected long timePerGeneration;
    protected long timeTotal;
    protected int alive;
    protected boolean dead;

    protected int threadCount;


    public Canvas(int x, int y, int xMin, int yMin, int xMax, int yMax) {
        this.x = x;
        this.y = y;
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.generation = 0;
        this.timePerGeneration = 0;
        this.timeTotal = 0;
        this.alive = 0;
        this.dead = false;
        this.threadCount = 1;
    }

    public int getXMin() {
        return xMin;
    }

    public int getYMin() {
        return yMin;
    }

    public int getxMax() {
        return xMax;
    }

    public int getyMax() {
        return yMax;
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

    public void moveUp() {
        if (xMin >= 10) {
            xMin = xMin - 10;
        }
    }

    public void moveDown() {
        if (xMin <= x - xMax - 11) {
            xMin = xMin + 10;
        }
        else {
            xMin = x - xMax - 1;
        }
    }

    public void moveLeft() {
        if (yMin >= 10) {
            yMin = yMin - 10;
        }
    }

    public void moveRight() {
        if (yMin <=  y - yMax - 11) {
            yMin = yMin + 10;
        }
        else {
            yMin = y - yMax - 1;
        }
    }
}
