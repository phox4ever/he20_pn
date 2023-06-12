package atl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static java.lang.Thread.sleep;

/**
 * Conway's Game of Life
 * <p>
 * Rules:
 * 1. Any live cell with fewer than two live neighbours dies, as if caused by underpopulation.
 * 2. Any live cell with two or three live neighbours lives on to the next generation.
 * 3. Any live cell with more than three live neighbours dies, as if by overpopulation.
 * 4. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
 * <p>
 * This implementation uses a ForkJoinPool to parallelize the computation of the next generation.
 * The grid is split into a number of tasks, each of which is executed by a thread in the pool.
 * The number of tasks is determined by the granularity parameter.
 * The granularity parameter is the number of rows that each task will process.
 *
 * @author Philipp Martin <philipp.martin@hf-ict.info>
 * @version 1.0
 */
public class GameOfLife {

    // Terminal size (check with `tput cols` and `tput lines` or `stty size` or 'mode con')
    final int TERMINAL_WIDTH = 156;
    final int TERMINAL_HEIGHT = 40;

    protected Grid grid;

    protected ForkJoinPool pool = new ForkJoinPool();

    protected List<GridTask> tasks;

    protected StringBuffer buffer;

    protected int renderInterval = 1;

    protected int sleepInterval = 100;

    public void setSubTaskCount(int subTaskCount) {
        this.subTaskCount = subTaskCount;
    }

    protected int subTaskCount = 1;


    public static void main(String[] args) throws InterruptedException {
        GameOfLife game;
        if (args.length > 0 && args.length < 5) {
            System.out.println("Usage: java GameOfLife <x> <y> <renderInterval> <sleepInterval> <numberOfSubtasks>");
            System.exit(1);
        }
        if (args.length == 0) {
            System.out.println("No arguments given, using default values.");
            Thread.sleep(1000);
            game = new GameOfLife();
        } else {
            game = new GameOfLife(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        }
        clearConsole(game.TERMINAL_HEIGHT);
        game.run();

    }


    /**
     * Load the game with default values
     */
    public GameOfLife() {
        this(120, 250);
    }

    /**
     * Load the game with a grid of size x*y
     *
     * @param x width of the grid
     * @param y height of the grid
     */
    public GameOfLife(int x, int y) {
        this.grid = new ArrayGrid(x, y);
        //this.grid = new MapGrid(x, y);
        grid.setGrid(initGrid(grid.getGrid()));
    }


    /**
     * Load the game with a grid of size x*y and set the render and sleep intervals
     *
     * @param x              width of the grid
     * @param y              height of the grid
     * @param renderInterval number of generations to skip before rendering
     * @param sleepInterval  time to sleep between generations
     * @param subTaskCount    number of tasks to use
     */
    public GameOfLife(int x, int y, int renderInterval, int sleepInterval, int subTaskCount) {
        this(x, y);
        this.renderInterval = renderInterval;
        this.sleepInterval = sleepInterval;
        this.subTaskCount = subTaskCount;
    }


    /**
     * Initialize the grid with some patterns
     *
     * @param grid the grid to set
     * @return the initialized grid
     */
    static int[][] initGrid(int[][] grid) {
        spawnPulsar(grid, 0, 0);
        spawnPulsar(grid, 0, 80);
        spawnPulsar(grid, 20, 0);
        spawnPulsar(grid, 20, 80);
        spawnPulsar(grid, 40, 0);
        spawnPulsar(grid, 40, 80);
        spawnGliderGun(grid, 0, 20);
        grid = mirrorGrid(grid);
        grid = moveGrid(grid, 0, 120);
        spawnPulsar(grid, 0, 0);
        spawnPulsar(grid, 0, 80);
        spawnPulsar(grid, 20, 0);
        spawnPulsar(grid, 20, 80);
        spawnPulsar(grid, 40, 0);
        spawnPulsar(grid, 40, 80);
        spawnGliderGun(grid, 0, 20);
        return grid;
    }


    /**
     * Run the game and render the grid
     *
     * @throws InterruptedException
     */
    public void run() throws InterruptedException {

        // Initialize the the working grid
        Grid workingGrid = new ArrayGrid(grid.getX(), grid.getY());

        int generation = 0;
        int alive;
        int kill = 0;
        long start = System.nanoTime();
        long time;
        double timePerGeneration = 0;

        // Create a canvas to draw on
        Canvas canvas = new Canvas(grid.getX(), grid.getY(), 0, 0, TERMINAL_HEIGHT * 2 - 2, TERMINAL_WIDTH);
        // Set the number of threads used to compute the next generation so that the canvas can display it
        canvas.setTaskCount(subTaskCount);

        // Create a user input handler
        new UserInput(canvas, this);

        // Loop until all cells are dead or the user presses 'q'
        do {
            alive = countAlive();
            canvas.setGeneration(generation);
            canvas.setTimePerGeneration(timePerGeneration, renderInterval * 100);
            // Draw the grid at the given interval
            if (renderInterval > 0 && generation % renderInterval == 0) {
                canvas.setTimeTotal((int) ((System.nanoTime() - start) / 1000000L));
                canvas.setAlive(alive);
                drawGrid(canvas);
            }
            // Compute the next generation
            time = System.nanoTime();
            if (subTaskCount == 1) {
                workingGrid = executeTaskSerial(workingGrid);
            } else {
                workingGrid = excecuteTaskParallel(workingGrid);
            }
            timePerGeneration = (System.nanoTime() - time) / 1000000D;
            // Sleep for the given interval
            try {
                sleep(sleepInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            generation++;
            if (alive == countAlive()) {
                kill++;
            } else {
                kill = 0;
            }
        } while (kill < 100);
        // Draw the final grid
        canvas.setDead(true);
        drawGrid(canvas);
    }

    /**
     * Compute the next generation using a parallel fork join pool
     *
     * @param workingGrid the grid to set
     * @return the next generation
     */
    private Grid excecuteTaskParallel(Grid workingGrid) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        workingGrid = (Grid) pool.invoke(new GridTask(this, 0, grid.getX(), grid.getX() / subTaskCount, workingGrid));
        workingGrid = grid.swapGrid(workingGrid);
        return workingGrid;
    }

    /**
     * Compute the next generation using a single thread
     *
     * @param workingGrid the grid to set
     * @return the next generation
     */
    private Grid executeTaskSerial(Grid workingGrid) {
        workingGrid = (Grid) (new GridTask(this, 0, grid.getX(), grid.getX(), workingGrid)).compute();
        return grid.swapGrid(workingGrid);
    }

    /**
     * Count the number of alive cells
     *
     * @return the number of alive cells
     */
    private int countAlive() {
        int count = 0;
        for (int[] row : grid.getGrid()) {
            for (int cell : row) {
                count += cell;
            }
        }
        return count;
    }

    /**
     * Print the grid to the console in low resolution mode
     *
     * This uses two unicode block characters to represent each cell.
     *
     * @param x    the x coordinate of the top left corner of the pattern
     * @param y    the y coordinate of the top left corner of the pattern
     * @param maxX the maximum x coordinate of the grid until which to render
     * @param maxY the maximum y coordinate of the grid until which to render
     * @return a string buffer containing the rendered grid
     */
    public StringBuffer renderLowRes(int x, int y, int maxX, int maxY) {
        int[][] grid = this.grid.getGrid();
        for (int i = x; i < x + maxX && i < this.grid.getX(); i++) {
            for (int j = y; j < y + maxY && j < this.grid.getY(); j++) {
                // only ██ or none
                char c = ' ';
                //middle: ██
                if (grid[i][j] == 1) {
                    c = '\u2588';
                }
                buffer.append(c).append(c);
            }
            if (i < x + maxX - 1 && i < this.grid.getX() - 1) {
                buffer.append("\n");
            }
        }
        return buffer;
    }

    /**
     * Print the grid to the console in medium resolution mode
     * <p>
     * This uses one unicode block character to represent two cells.
     *
     * @param x    the x coordinate of the top left corner of the pattern
     * @param y    the y coordinate of the top left corner of the pattern
     * @param maxX the maximum x coordinate of the grid until which to render
     * @param maxY the maximum y coordinate of the grid until which to render
     * @return a string buffer containing the rendered grid
     */
    public StringBuffer renderMidRes(int x, int y, int maxX, int maxY) {
        int[][] grid = this.grid.getGrid();
        for (int i = x; i < x + maxX && i < this.grid.getX(); i = i + 2) {
            for (int j = y; j < y + maxY && j < this.grid.getY(); j++) {
                //▀, ▄ or █
                char c = ' ';

                //top: ▀
                if (grid[i][j] == 1 && grid[i + 1][j] == 0) {
                    c = '\u2580';
                }
                //bottom: ▄
                if (grid[i][j] == 0 && grid[i + 1][j] == 1) {
                    c = '\u2584';
                }
                //middle: █
                if (grid[i][j] + grid[i + 1][j] == 2) {
                    c = '\u2588';
                }
                buffer.append(c);
            }
            if (i < x + maxX - 2 && i < this.grid.getX() - 2) {
                buffer.append("\n");
            }
        }
        return buffer;
    }

    /**
     * Print the grid to the console in high resolution mode
     * <p>
     * This uses one unicode block character to represent four cells.
     *
     * @param x    the x coordinate of the top left corner of the pattern
     * @param y    the y coordinate of the top left corner of the pattern
     * @param maxX the maximum x coordinate of the grid until which to render
     * @param maxY the maximum y coordinate of the grid until which to render
     * @return a string buffer containing the rendered grid
     */
    public StringBuffer renderHighRes(int x, int y, int maxX, int maxY) {
        int[][] grid = this.grid.getGrid();
        for (int i = x; i < x + maxX && i < this.grid.getX(); i = i + 2) {
            for (int j = y; j <= y + maxY && j < this.grid.getY(); j = j + 2) {
                //▖	▗	▘	▙	▚	▛	▜	▝	▞	▟
                char c = ' ';
                //top left: ▘
                if (grid[i][j] == 1 && grid[i + 1][j] + grid[i + 1][j + 1] + grid[i][j + 1] == 0) {
                    c = '\u2598';
                }
                //top right: ▝
                if (grid[i][j + 1] == 1 && grid[i + 1][j] + grid[i + 1][j + 1] + grid[i][j] == 0) {
                    c = '\u259D';
                }
                //bottom left: ▖
                if (grid[i + 1][j] == 1 && grid[i][j] + grid[i][j + 1] + grid[i + 1][j + 1] == 0) {
                    c = '\u2596';
                }
                //bottom right: ▗
                if (grid[i + 1][j + 1] == 1 && grid[i][j] + grid[i + 1][j] + grid[i][j + 1] == 0) {
                    c = '\u2597';
                }
                //top: ▀
                if (grid[i][j] + grid[i][j + 1] == 2 && grid[i + 1][j] + grid[i + 1][j + 1] == 0) {
                    c = '\u2580';
                }
                //bottom: ▄
                if (grid[i + 1][j] + grid[i + 1][j + 1] == 2 && grid[i][j] + grid[i][j + 1] == 0) {
                    c = '\u2584';
                }
                //left: ▌
                if (grid[i][j] + grid[i + 1][j] == 2 && grid[i][j + 1] + grid[i + 1][j + 1] == 0) {
                    c = '\u258C';
                }
                //right: ▐
                if (grid[i][j + 1] + grid[i + 1][j + 1] == 2 && grid[i][j] + grid[i + 1][j] == 0) {
                    c = '\u2590';
                }
                //middle: █
                if (grid[i][j] + grid[i + 1][j] + grid[i][j + 1] + grid[i + 1][j + 1] == 4) {
                    c = '\u2588';
                }
                //top left corner: ▛
                if (grid[i][j] + grid[i][j + 1] + grid[i + 1][j] == 3 && grid[i + 1][j + 1] == 0) {
                    c = '\u259B';
                }
                //top right corner: ▜
                if (grid[i][j] + grid[i][j + 1] + grid[i + 1][j + 1] == 3 && grid[i + 1][j] == 0) {
                    c = '\u259C';
                }
                //bottom left corner: ▙
                if (grid[i][j] + grid[i + 1][j] + grid[i + 1][j + 1] == 3 && grid[i][j + 1] == 0) {
                    c = '\u2599';
                }
                //bottom right corner: ▟
                if (grid[i][j + 1] + grid[i + 1][j] + grid[i + 1][j + 1] == 3 && grid[i][j] == 0) {
                    c = '\u259F';
                }
                //upper left and lower right: ▚
                if (grid[i][j] + grid[i + 1][j + 1] == 2 && grid[i][j + 1] + grid[i + 1][j] == 0) {
                    c = '\u259A';
                }
                //upper right and lower left: ▞
                if (grid[i][j + 1] + grid[i + 1][j] == 2 && grid[i][j] + grid[i + 1][j + 1] == 0) {
                    c = '\u259E';
                }
                buffer.append(c);
            }
            if (i < x + maxX - 2 && i < this.grid.getX() - 2) {
                buffer.append("\n");
            }
        }
        return buffer;
    }


    /**
     * Prints the grid to the console using data from the canvas.
     *
     * @param canvas the canvas to draw the grid on
     */
    public void drawGrid(Canvas canvas) {
        if (buffer == null) {
            buffer = new StringBuffer();
        } else {
            buffer.delete(0, buffer.length());
        }
        buffer.append("\033[H\033[3J");
        if (canvas.getZoomLevel() > Canvas.ZOOM_DEFAULT) {
            buffer.append(renderLowRes(canvas.getXMin(), canvas.getYMin(), canvas.getxMax() - 2, canvas.getyMax()));
        } else if (canvas.getZoomLevel() == Canvas.ZOOM_DEFAULT) {
            buffer.append(renderMidRes(canvas.getXMin(), canvas.getYMin(), canvas.getxMax() - 2, canvas.getyMax()));
        } else {
            buffer.append(renderHighRes(canvas.getXMin(), canvas.getYMin(), canvas.getxMax() - 2, canvas.getyMax()));
        }
        buffer.append("\n\33[2K\r");
        buffer.append("Grid: ").append(grid.getX()).append("x").append(grid.getY())
                .append(" (x").append(canvas.getXMin()).append(":y").append(canvas.getYMin()).append("|").append(canvas.getxMax()).append("x").append(canvas.getyMax()).append(")")
                .append("  Subtasks: ").append(canvas.getTaskCount())
                .append("  Alive: ")
                .append(canvas.getAlive())
                .append("  Generations: ")
                .append(canvas.getGeneration())
                .append(" (")
                .append(new DecimalFormat("######0.00").format(canvas.getTimePerGeneration()))
                .append("ms/gen avg: ")
                .append(new DecimalFormat("######0.00").format(canvas.getAverageTimePerGeneration()))
                .append("ms/gen)  Time elapsed: ")
                .append(canvas.getTimeTotalString())
                .append("  Max threads: ")
                .append(subTaskCount > 1 ? ForkJoinPool.commonPool().getPoolSize() : 1);
        if (canvas.isDead()) {
            buffer.append("\n\33[2K\rThe grid is dead! Press 'q+Enter' to quit: ");
        } else {
            buffer.append("\n\33[2K\rMove: 'hjkl' or 'wsad' Zoom: 'i/o' Quit: 'q' ** Press ENTER to confirm: ");
        }
        System.out.print(buffer);
    }

    /**
     * Checks the neighbours of a cell according to the rules of the game of life and returns true if the cell should be alive in the next generation.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @return true if the cell is alive, false otherwise
     */
    public boolean checkNeighbours(int x, int y) {
        int neighbours = 0;
        // top left
        if (x > 0 && y > 0 && this.grid.isAlive(x - 1, y - 1)) {
            neighbours++;
        }
        // top
        if (x > 0 && this.grid.isAlive(x - 1, y)) {
            neighbours++;
        }
        // top right
        if (x > 0 && y < grid.getY() - 1 && this.grid.isAlive(x - 1, y + 1)) {
            neighbours++;
        }
        // left
        if (y > 0 && this.grid.isAlive(x, y - 1)) {
            neighbours++;
        }
        // right
        if (y < grid.getY() - 1 && this.grid.isAlive(x, y + 1)) {
            neighbours++;
        }
        // bottom left
        if (x < grid.getX() - 1 && y > 0 && this.grid.isAlive(x + 1, y - 1)) {
            neighbours++;
        }
        // bottom
        if (x < grid.getX() - 1 && this.grid.isAlive(x + 1, y)) {
            neighbours++;
        }
        // bottom right
        if (x < grid.getX() - 1 && y < grid.getY() - 1 && this.grid.isAlive(x + 1, y + 1)) {
            neighbours++;
        }
        if (this.grid.isAlive(x, y)) {
            return neighbours == 2 || neighbours == 3;
        } else {
            return neighbours == 3;
        }
    }

    /**
     * Cross-platform method to clear the console.
     *
     * @param rows the number of rows to clear
     */
    public static void clearConsole(int rows) {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\33[2K\n".repeat(rows));
                System.out.print("\033[H\033[2J");
            }
        } catch (IOException | InterruptedException ex) {
        }
    }

    /**
     * Spawns an oscillator on the grid.
     *
     * @param grid the grid to spawn the oscillator on
     * @param x    the x coordinate of the top left corner of the oscillator
     * @param y    the y coordinate of the top left corner of the oscillator
     */
    public static void spawnOscillator(int[][] grid, int x, int y) {
        grid[x + 1][y + 2] = 1;
        grid[x + 2][y + 2] = 1;
        grid[x + 3][y + 2] = 1;
    }

    /**
     * Spawns a glider on the grid.
     *
     * @param grid the grid to spawn the glider on
     * @param x    the x coordinate of the top left corner of the glider
     * @param y    the y coordinate of the top left corner of the glider
     */
    public static void spawnGlider(int[][] grid, int x, int y) {
        grid[x + 1][y + 2] = 1;
        grid[x + 2][y + 3] = 1;
        grid[x + 3][y + 1] = 1;
        grid[x + 3][y + 2] = 1;
        grid[x + 3][y + 3] = 1;
    }

    /**
     * Spawns a glider gun on the grid.
     *
     * @param grid the grid to spawn the glider gun on
     * @param x    the x coordinate of the top left corner of the glider gun
     * @param y    the y coordinate of the top left corner of the glider gun
     */
    public static void spawnGliderGun(int[][] grid, int x, int y) {
        grid[x + 5][y + 1] = 1;
        grid[x + 5][y + 2] = 1;
        grid[x + 6][y + 1] = 1;
        grid[x + 6][y + 2] = 1;
        grid[x + 3][y + 13] = 1;
        grid[x + 3][y + 14] = 1;
        grid[x + 4][y + 12] = 1;
        grid[x + 4][y + 16] = 1;
        grid[x + 5][y + 11] = 1;
        grid[x + 5][y + 17] = 1;
        grid[x + 6][y + 11] = 1;
        grid[x + 6][y + 15] = 1;
        grid[x + 6][y + 17] = 1;
        grid[x + 6][y + 18] = 1;
        grid[x + 7][y + 11] = 1;
        grid[x + 7][y + 17] = 1;
        grid[x + 8][y + 12] = 1;
        grid[x + 8][y + 16] = 1;
        grid[x + 9][y + 13] = 1;
        grid[x + 9][y + 14] = 1;
        grid[x + 1][y + 25] = 1;
        grid[x + 2][y + 23] = 1;
        grid[x + 2][y + 25] = 1;
        grid[x + 3][y + 21] = 1;
        grid[x + 3][y + 22] = 1;
        grid[x + 4][y + 21] = 1;
        grid[x + 4][y + 22] = 1;
        grid[x + 5][y + 21] = 1;
        grid[x + 5][y + 22] = 1;
        grid[x + 6][y + 23] = 1;
        grid[x + 6][y + 25] = 1;
        grid[x + 7][y + 25] = 1;
        grid[x + 3][y + 35] = 1;
        grid[x + 3][y + 36] = 1;
        grid[x + 4][y + 35] = 1;
        grid[x + 4][y + 36] = 1;
    }


    /**
     * Spawns a pulsar on the grid.
     *
     * @param grid the grid to spawn the pulsar on
     * @param x    the x coordinate of the top left corner of the pulsar
     * @param y    the y coordinate of the top left corner of the pulsar
     */
    public static void spawnPulsar(int[][] grid, int x, int y) {
        grid[x + 2][y + 4] = 1;
        grid[x + 2][y + 5] = 1;
        grid[x + 2][y + 6] = 1;
        grid[x + 2][y + 10] = 1;
        grid[x + 2][y + 11] = 1;
        grid[x + 2][y + 12] = 1;
        grid[x + 4][y + 2] = 1;
        grid[x + 4][y + 7] = 1;
        grid[x + 4][y + 9] = 1;
        grid[x + 4][y + 14] = 1;
        grid[x + 5][y + 2] = 1;
        grid[x + 5][y + 7] = 1;
        grid[x + 5][y + 9] = 1;
        grid[x + 5][y + 14] = 1;
        grid[x + 6][y + 2] = 1;
        grid[x + 6][y + 7] = 1;
        grid[x + 6][y + 9] = 1;
        grid[x + 6][y + 14] = 1;
        grid[x + 7][y + 4] = 1;
        grid[x + 7][y + 5] = 1;
        grid[x + 7][y + 6] = 1;
        grid[x + 7][y + 10] = 1;
        grid[x + 7][y + 11] = 1;
        grid[x + 7][y + 12] = 1;
        grid[x + 9][y + 4] = 1;
        grid[x + 9][y + 5] = 1;
        grid[x + 9][y + 6] = 1;
        grid[x + 9][y + 10] = 1;
        grid[x + 9][y + 11] = 1;
        grid[x + 9][y + 12] = 1;
        grid[x + 10][y + 2] = 1;
        grid[x + 10][y + 7] = 1;
        grid[x + 10][y + 9] = 1;
        grid[x + 10][y + 14] = 1;
        grid[x + 11][y + 2] = 1;
        grid[x + 11][y + 7] = 1;
        grid[x + 11][y + 9] = 1;
        grid[x + 11][y + 14] = 1;
        grid[x + 12][y + 2] = 1;
        grid[x + 12][y + 7] = 1;
        grid[x + 12][y + 9] = 1;
        grid[x + 12][y + 14] = 1;
        grid[x + 14][y + 4] = 1;
        grid[x + 14][y + 5] = 1;
        grid[x + 14][y + 6] = 1;
        grid[x + 14][y + 10] = 1;
        grid[x + 14][y + 11] = 1;
        grid[x + 14][y + 12] = 1;
    }

    /**
     * Mirrors the grid across the x and y axis.
     *
     * @param grid the grid to mirror
     * @return the mirrored grid
     */
    public static int[][] mirrorGrid(int[][] grid) {
        int[][] newGrid = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            newGrid[i] = grid[grid.length - 1 - i];
        }
        return newGrid;
    }

    /**
     * Moves the grid by x and y.
     *
     * @param grid the grid to move
     * @param x    the x coordinate to move the grid by
     * @param y    the y coordinate to move the grid by
     * @return the moved grid
     */
    public static int[][] moveGrid(int[][] grid, int x, int y) {
        int[][] newGrid = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length - x; i++) {
            if (grid[0].length - y >= 0) System.arraycopy(grid[i], 0, newGrid[i + x], y, grid[0].length - y);
        }
        return newGrid;
    }

}
