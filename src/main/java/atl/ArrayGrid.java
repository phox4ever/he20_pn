package atl;

class ArrayGrid implements Grid {
    public int[][] grid;
    public int x;
    public int y;

    public ArrayGrid(int x, int y) {
        this.x = x;
        this.y = y;
        this.grid = new int[x][y];
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
        return grid[x][y] == 1;
    }

    @Override
    public int[][] getGrid() {
        return grid;
    }

    public void setAlive(int x, int y, boolean alive) {
        grid[x][y] = alive ? 1 : 0;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }
    @Override
    public Grid swapGrid(Grid grid) {
        int[][] swap = this.grid;
        this.grid = grid.getGrid();
        grid.setGrid(swap);
        return grid;
    }
}
