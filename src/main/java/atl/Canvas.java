package atl;

/**
 * Canvas class
 *
 * @version 1.0
 * @author philipp.martin@hf-ict.info
 */
class Canvas {
    private final int x;
    private final int y;
    private int xMin;
    private int yMin;
    private final int xMax;
    private final int yMax;
    private int generation;
    private double timePerGeneration;
    private double averageTimePerGeneration;
    private long timeTotal;
    private int alive;
    private boolean dead;
    private int taskCount;
    final public static int ZOOM_NEAREST = 3;
    final public static int ZOOM_DEFAULT = 2;
    final public static int ZOOM_FARTHEST = 1;
    private int zoomLevel = ZOOM_DEFAULT;

    public Canvas(int x, int y, int xMin, int yMin, int xMax, int yMax) {
        this.x = x;
        this.y = y;
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.generation = 0;
        this.timePerGeneration = 0;
        this.averageTimePerGeneration = 0;
        this.timeTotal = 0;
        this.alive = 0;
        this.dead = false;
        this.taskCount = 1;
    }

    /**
     * Gets the x size of the canvas.
     *
     * @return the x size of the canvas
     */
    public int getXMin() {
        return xMin;
    }

    /**
     * Gets the y size of the canvas.
     *
     * @return the y size of the canvas
     */
    public int getYMin() {
        return yMin;
    }

    /**
     * Gets the maximum x coordinate of the canvas.
     *
     * @return the maximum x coordinate of the canvas
     */
    public int getxMax() {
        if (zoomLevel == ZOOM_FARTHEST) {
            return xMax;
        } else if (zoomLevel == ZOOM_DEFAULT) {
            return xMax;
        } else {
            return xMax / 2 + 1;
        }
    }

    /**
     * Gets the maximum y coordinate of the canvas.
     *
     * @return the maximum y coordinate of the canvas
     */
    public int getyMax() {
        if (zoomLevel == ZOOM_DEFAULT) {
            return yMax;
        } else if (zoomLevel == ZOOM_FARTHEST) {
            return yMax * 2;
        } else {
            return yMax / 2 - 4;
        }
    }

    /**
     * Get the number of generations.
     *
     * @return the number of generations
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Set the number of generations.
     *
     * @param generation the generation to set
     */
    public void setGeneration(int generation) {
        this.generation = generation;
    }

    /**
     * Get the time per generation.
     *
     * @return the time per generation
     */
    public double getTimePerGeneration() {
        return timePerGeneration;
    }

    /**
     * Get the average time per generation.
     *
     * @return the average time per generation
     */
    public double getAverageTimePerGeneration() {
        return averageTimePerGeneration;
    }

    /**
     * Set the time per generation.
     *
     * @param timePerGeneration the timePerGeneration to set
     * @param range the range of generations to calculate the average time per generation
     */
    public void setTimePerGeneration(double timePerGeneration, int range) {
        int delta = Math.min(generation, range);
        // Calculate average time per generation, but only after 5 generations to avoid skewing the average.
        if (generation > 5) {
            this.averageTimePerGeneration = (averageTimePerGeneration * delta + timePerGeneration) / (delta + 1);
        }
        else {
            this.averageTimePerGeneration = timePerGeneration;
        }
        this.timePerGeneration = timePerGeneration;
    }

    /**
     * Get the total time.
     *
     * @return the total time
     */
    public long getTimeTotal() {
        return timeTotal;
    }

    /**
     * Get the total time as a formatted string.
     *
     * @return the total time as a string
     */
    public String getTimeTotalString() {
        if (timeTotal > 10000) {
            return timeTotal / 1000 + "s";
        }
        return timeTotal + "ms";
    }

    /**
     * Set the total time.
     *
     * @param timeTotal the total time to set
     */
    public void setTimeTotal(long timeTotal) {
        this.timeTotal = timeTotal;
    }

    /**
     * Get the alive number of cells.
     *
     * @return the alive number of cells
     */
    public int getAlive() {
        return alive;
    }

    /**
     * Set the alive number of cells.
     *
     * @param alive the number of alive cells to set
     */
    public void setAlive(int alive) {
        this.alive = alive;
    }

    /**
     * Check if the game is dead.
     *
     * @return true if the game is dead, false otherwise
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Set if the game is dead.
     *
     * @param dead true if the game is dead, false otherwise
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Get the number of tasks.
     *
     * @return the number of tasks
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Set the number of tasks.
     *
     * @param taskCount the number of tasks to set
     */
    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    /**
     * Get the zoom level.
     *
     * @return the zoomLevel
     */
    public int getZoomLevel() {
        return zoomLevel;
    }

    /**
     * Move the canvas up.
     */
    public void moveUp() {
        if (xMin >= 10) {
            xMin = xMin - 10;
        }
        else {
            xMin = 0;
        }
    }

    /**
     * Move the canvas down.
     */
    public void moveDown() {
        if (xMin <= x - xMax - 11) {
            xMin = xMin + 10;
        }
        else {
            xMin = x - xMax + 1;
        }
    }

    /**
     * Move the canvas left.
     */
    public void moveLeft() {
        if (yMin >= 10) {
            yMin = yMin - 10;
        } else {
            yMin = 0;
        }

    }

    /**
     * Move the canvas right.
     */
    public void moveRight() {
        if (yMin <=  y - yMax - 11) {
            yMin = yMin + 10;
        }
        else {
            yMin = y - yMax - 1;
        }
    }

    /**
     * Zoom in.
     */
    public void zoomIn() {
        if (zoomLevel < ZOOM_NEAREST) {
            zoomLevel++;
        }
    }

    /**
     * Zoom out.
     */
    public void zoomOut() {
        if (zoomLevel > ZOOM_FARTHEST) {
            zoomLevel--;
        }
    }
}
