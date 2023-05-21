package atl;

public interface Grid {
    int getX();

    int getY();

    boolean isAlive(int x, int y);

    void setAlive(int x, int y, boolean alive);

    int[][] getGrid();

    void setGrid(int[][] grid);

    Grid swapGrid(Grid grid);
}
