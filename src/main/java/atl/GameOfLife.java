package atl;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class GameOfLife {

    protected int[][] grid;

    public static void main(String[] args) throws InterruptedException {
        int[][] grid = new int[30][50];
        int[][] workingGrid = new int[30][50];

        //oscillator
        /*
        grid[5][4] = 1;
        grid[5][5] = 1;
        grid[5][6] = 1;
        */

        //glider
        /*
        grid[1][2] = 1;
        grid[2][3] = 1;
        grid[3][1] = 1;
        grid[3][2] = 1;
        grid[3][3] = 1;
        */

        //pulsar
        /*
        grid[2][4] = 1;
        grid[2][5] = 1;
        grid[2][6] = 1;
        grid[2][10] = 1;
        grid[2][11] = 1;
        grid[2][12] = 1;
        grid[4][2] = 1;
        grid[4][7] = 1;
        grid[4][9] = 1;
        grid[4][14] = 1;
        grid[5][2] = 1;
        grid[5][7] = 1;
        grid[5][9] = 1;
        grid[5][14] = 1;
        grid[6][2] = 1;
        grid[6][7] = 1;
        grid[6][9] = 1;
        grid[6][14] = 1;
        grid[7][4] = 1;
        grid[7][5] = 1;
        grid[7][6] = 1;
        grid[7][10] = 1;
        grid[7][11] = 1;
        grid[7][12] = 1;
        grid[9][4] = 1;
        grid[9][5] = 1;
        grid[9][6] = 1;
        grid[9][10] = 1;
        grid[9][11] = 1;
        grid[9][12] = 1;
        grid[10][2] = 1;
        grid[10][7] = 1;
        grid[10][9] = 1;
        grid[10][14] = 1;
        grid[11][2] = 1;
        grid[11][7] = 1;
        grid[11][9] = 1;
        grid[11][14] = 1;
        grid[12][2] = 1;
        grid[12][7] = 1;
        grid[12][9] = 1;
        grid[12][14] = 1;
        grid[14][4] = 1;
        grid[14][5] = 1;
        grid[14][6] = 1;
        grid[14][10] = 1;
        grid[14][11] = 1;
        grid[14][12] = 1;
        */

        //glider gun
        grid[5][1] = 1;
        grid[5][2] = 1;
        grid[6][1] = 1;
        grid[6][2] = 1;
        grid[3][13] = 1;
        grid[3][14] = 1;
        grid[4][12] = 1;
        grid[4][16] = 1;
        grid[5][11] = 1;
        grid[5][17] = 1;
        grid[6][11] = 1;
        grid[6][15] = 1;
        grid[6][17] = 1;
        grid[6][18] = 1;
        grid[7][11] = 1;
        grid[7][17] = 1;
        grid[8][12] = 1;
        grid[8][16] = 1;
        grid[9][13] = 1;
        grid[9][14] = 1;
        grid[1][25] = 1;
        grid[2][23] = 1;
        grid[2][25] = 1;
        grid[3][21] = 1;
        grid[3][22] = 1;
        grid[4][21] = 1;
        grid[4][22] = 1;
        grid[5][21] = 1;
        grid[5][22] = 1;
        grid[6][23] = 1;
        grid[6][25] = 1;
        grid[7][25] = 1;
        grid[3][35] = 1;
        grid[3][36] = 1;
        grid[4][35] = 1;
        grid[4][36] = 1;






        GameOfLife game = new GameOfLife(grid);
        game.drawGrid();
        while (true) {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    workingGrid[i][j] = game.checkNeighbours(i, j) ? 1 : 0;
                }
            }
            workingGrid = game.swapGrid(workingGrid);
            game.drawGrid();
        }
    }

    public GameOfLife(int[][] grid) {
        this.grid = grid;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public int[][] swapGrid(int[][] grid) {
        int[][] swap = this.grid;
        this.grid = grid;
        return swap;
    }

    public void drawGrid() {
        clearConsole();
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell == 1 ? "\u2588\u2588" : "  ");
            }
            System.out.println();
        }
    }

    public boolean isAlive(int x, int y) {
        return grid[x][y] == 1;
    }

    public boolean checkNeighbours(int x, int y) {
        int neighbours = 0;
        // top left
        if (x > 0 && y > 0 && isAlive(x - 1, y - 1)) {
            neighbours++;
        }
        // top
        if (x > 0 && isAlive(x - 1, y)) {
            neighbours++;
        }
        // top right
        if (x > 0 && y < grid[0].length - 1 && isAlive(x - 1, y + 1)) {
            neighbours++;
        }
        // left
        if (y > 0 && isAlive(x, y - 1)) {
            neighbours++;
        }
        // right
        if (y < grid[0].length - 1 && isAlive(x, y + 1)) {
            neighbours++;
        }
        // bottom left
        if (x < grid.length - 1 && y > 0 && isAlive(x + 1, y - 1)) {
            neighbours++;
        }
        // bottom
        if (x < grid.length - 1 && isAlive(x + 1, y)) {
            neighbours++;
        }
        // bottom right
        if (x < grid.length - 1 && y < grid[0].length - 1 && isAlive(x + 1, y + 1)) {
            neighbours++;
        }
        if (isAlive(x, y)) {
            return neighbours == 2 || neighbours == 3;
        } else {
            return neighbours == 3;
        }
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {}
    }
}
