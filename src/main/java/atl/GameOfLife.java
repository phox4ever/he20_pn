package atl;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class GameOfLife {

    final int TERMINAL_WIDTH = 140;
    final int TERMINAL_HEIGHT = 38;

    protected Grid grid;

    protected ForkJoinPool pool = new ForkJoinPool();

    protected List<GridTask> tasks;

    protected StringBuffer buffer;

    protected int renderInterval = 1;

    protected int sleepInterval = 100;

    protected int threadCount = 4;

    protected boolean highRes = false;


    public static void main(String[] args) throws InterruptedException {

        if (args.length != 5) {
            System.out.println("Usage: java GameOfLife <x> <y> <renderInterval> <sleepInterval> <threadCount>");
            System.exit(1);
        }
        GameOfLife game = new GameOfLife(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        game.run();

    }

    public GameOfLife(int x, int y) {
        this.grid = new ArrayGrid(x, y);
        //this.grid = new MapGrid(x, y);
        grid.setGrid(initGrid(grid.getGrid()));
    }

    public GameOfLife(int x, int y, int renderInterval, int sleepInterval, int threadCount) {
        this(x, y);
        this.renderInterval = renderInterval;
        this.sleepInterval = sleepInterval;
        this.threadCount = threadCount;
    }

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


    public void run() throws InterruptedException {
        Grid workingGrid = new ArrayGrid(grid.getX(), grid.getY());
        //Grid workingGrid = new MapGrid(grid.getX(), grid.getY());
        int generation = 0;
        int alive;
        int kill = 0;
        long start = System.nanoTime();
        long time = start;
        long averageTimePerGeneration = 0;
        Canvas canvas = new Canvas(0, 0, TERMINAL_HEIGHT * 2 - 2, highRes ? TERMINAL_WIDTH * 2 : TERMINAL_WIDTH);
        canvas.setThreadCount(threadCount);
        do {
            alive = countAlive();
            if (renderInterval > 0 && generation % renderInterval == 0) {
                canvas.setGeneration(generation);
                canvas.setTimePerGeneration(averageTimePerGeneration);
                canvas.setTimeTotal((int) ((System.nanoTime() - start) / 1000000L));
                canvas.setAlive(alive);
                drawGrid(canvas);
            }
            time = System.nanoTime();
            ForkJoinPool pool = ForkJoinPool.commonPool();
            workingGrid = (Grid) pool.invoke(new GridTask(this, 0, grid.getX(), grid.getX() / threadCount, workingGrid));
            workingGrid = grid.swapGrid(workingGrid);
            averageTimePerGeneration = (averageTimePerGeneration * generation + (System.nanoTime() - time) / 1000L) / (generation + 1);
            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            generation++;
            if (alive == countAlive()) {
                kill++;
            } else {
                kill = 0;
            }
        } while (kill < 10);
        canvas.setDead(true);
        drawGrid(canvas);
    }


    private int countAlive() {
        int count = 0;
        for (int[] row : grid.getGrid()) {
            for (int cell : row) {
                count += cell;
            }
        }
        return count;
    }

    public StringBuffer renderLowRes(int x, int y, int maxX, int maxY) {
        int rows = 0;
        int columns = 0;
        for (int[] row : grid.getGrid()) {
            rows++;
            if (rows < x || rows >= maxX) {
                continue;
            }
            for (int cell : row) {
                columns++;
                if (columns < y || columns >= maxY) {
                    continue;
                }
                buffer.append(cell == 1 ? "\u2588\u2588" : "  ");
            }
            if (rows < grid.getX() - 1) {
                buffer.append("\n");
            }
        }
        return buffer;
    }

    public StringBuffer renderMidRes(int x, int y, int maxX, int maxY) {
        int[][] grid = this.grid.getGrid();
        for (int i = 0; i < this.grid.getX(); i = i + 2) {
            if (i<x || i>=maxX) {
                continue;
            }
            for (int j = 0; j < this.grid.getY(); j++) {
                if (j<y || j>=maxY) {
                    continue;
                }
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
            if (i < maxX - 2) {
                buffer.append("\n");
            }
        }
        return buffer;
    }

    public StringBuffer renderHighRes(int x, int y, int maxX, int maxY) {
        int[][] grid = this.grid.getGrid();
        for (int i = 0; i < this.grid.getX(); i = i + 2) {
            if (i<x || i>=maxX) {
                continue;
            }
            for (int j = 0; j < this.grid.getY(); j = j + 2) {
                if (j<y || j>=maxY) {
                    continue;
                }
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
            if (i < maxX - 2) {
                buffer.append("\n");
            }
        }
        return buffer;
    }

    public void drawGrid(Canvas canvas) {
        if (buffer == null) {
            buffer = new StringBuffer();
        } else {
            buffer.delete(0, buffer.length());
        }
        buffer.append("\033[H\033[3J");
        if (highRes) {
            buffer = renderHighRes(canvas.getX(), canvas.getY(), canvas.getxMax(), canvas.getyMax());
        } else {
            buffer = renderMidRes(canvas.getX(), canvas.getY(), canvas.getxMax(), canvas.getyMax());
        }
        buffer.append("\n\33[2K\r");
        buffer.append("Grid: ").append(grid.getX()).append("x").append(grid.getY())
                .append(" (").append(canvas.getxMax()).append("x").append(canvas.getyMax()).append(")")
                .append("  Threads: ").append(canvas.getThreadCount())
                .append("  Alive: ")
                .append(canvas.getAlive())
                .append("  Generations: ")
                .append(canvas.getGeneration())
                .append(" (")
                .append(canvas.getTimePerGeneration())
                .append("μs/gen)  Time elapsed: ")
                .append(canvas.getTimeTotal())
                .append("ms");
        if (canvas.isDead()) {
            buffer.append("\n");
            buffer.append("The grid is dead!");
        }
        System.out.print(buffer);
    }

   

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

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {
        }
    }

    public static void spawnOscillator(int[][] grid, int x, int y) {
        grid[x + 1][y + 2] = 1;
        grid[x + 2][y + 2] = 1;
        grid[x + 3][y + 2] = 1;
    }

    public static void spawnGlider(int[][] grid, int x, int y) {
        grid[x + 1][y + 2] = 1;
        grid[x + 2][y + 3] = 1;
        grid[x + 3][y + 1] = 1;
        grid[x + 3][y + 2] = 1;
        grid[x + 3][y + 3] = 1;
    }

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

    public static int[][] mirrorGrid(int[][] grid) {
        int[][] newGrid = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            newGrid[i] = grid[grid.length - 1 - i];
        }
        return newGrid;
    }

    public static int[][] moveGrid(int[][] grid, int x, int y) {
        int[][] newGrid = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length - x; i++) {
            if (grid[0].length - y >= 0) System.arraycopy(grid[i], 0, newGrid[i + x], y, grid[0].length - y);
        }
        return newGrid;
    }

}
