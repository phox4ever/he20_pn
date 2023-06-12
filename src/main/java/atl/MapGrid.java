package atl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * MapGrid is a Grid implementation that uses a ConcurrentHashMap to store the grid.
 *
 * @version 1.0
 * @author philipp.martin@hf-ict.info
 */
public class MapGrid implements Grid {
    protected ConcurrentMap<Integer, int[]> grid;
    protected int x;
    protected int y;

    public MapGrid(int x, int y) {
        this.x = x;
        this.y = y;
        this.grid = new ConcurrentHashMap<>();
        for (int i = 0; i < x; i++) {
            grid.put(i, new int[y]);
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean isAlive(int x, int y) {
        return grid.get(x)[y] == 1;
    }

    @Override
    public int[][] getGrid() {
        return mapToArray();
    }

    @Override
    public void setGrid(int[][] grid) {
        arrayToMap(grid);
    }

    public void setGridMap(ConcurrentMap<Integer, int[]> grid) {
        this.grid = grid;
    }

    public ConcurrentMap<Integer, int[]> getGridMap() {
        return grid;
    }

    @Override
    public void setAlive(int x, int y, boolean alive) {
        grid.get(x)[y] = alive ? 1 : 0;
    }

    @Override
    public Grid swapGrid(Grid grid) {
        ConcurrentMap<Integer, int[]> swap = this.grid;
        this.grid = ((MapGrid) grid).getGridMap();
        ((MapGrid) grid).setGridMap(swap);
        return grid;
    }

    private void arrayToMap(int[][] array) {
        for (int i = 0; i < x; i++) {
            int[] row = grid.get(i);
            for (int j = 0; j < y; j++) {
                row[j] = array[i][j];
            }
        }
    }

    private int[][] mapToArray() {
        int[][] array = new int[x][y];
        for (int i = 0; i < x; i++) {
            int[] row = grid.get(i);
            for (int j = 0; j < y; j++) {
                array[i][j] = row[j];
            }
        }
        return array;
    }

}
