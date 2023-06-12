package atl;

/**
 * Grid interface for the Game of Life.
 *
 * @version 1.0
 * @author philipp.martin@hf-ict.info
 */
public interface Grid {
    /**
     * @return the x size of the grid
     */
    int getX();

    /**
     * @return the y size of the grid
     */
    int getY();

    /**
     * Checks if the cell at the given coordinates is alive.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return true if the cell is alive
     */
    boolean isAlive(int x, int y);

    /**
     * Marks the cell at the given coordinates as alive or dead.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param alive true if the cell should be alive
     */
    void setAlive(int x, int y, boolean alive);

    /**
     * Returns the grid as a 2D array.
     *
     * @return the grid as a 2D array
     */
    int[][] getGrid();

    /**
     * Sets the grid from a 2D array.
     *
     * @param grid the grid to set
     */
    void setGrid(int[][] grid);

    /**
     * Swaps the grid with the given grid.
     *
     * @param grid the grid to swap with
     * @return the swapped grid
     */
    Grid swapGrid(Grid grid);
}
