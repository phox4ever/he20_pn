package atl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

class GridTask extends RecursiveTask {
    private final GameOfLife game;
    protected int lowerBound;

    protected int upperBound;

    protected int granularity;

    protected Grid workingGrid;

    protected List<GridTask> subtasks;

    public GridTask(GameOfLife game, int lowerBound, int upperBound, int granularity, Grid workingGrid) {
        this.game = game;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.granularity = granularity;
        this.workingGrid = workingGrid;
    }

    public GridTask(GameOfLife game, int upperBound, Grid workingGrid) {
        this(game, 0, upperBound, 100, workingGrid);
    }


    public GridTask(GameOfLife game, int lowerBound, int upperBound, Grid workingGrid) {
        this(game, lowerBound, upperBound, 100, workingGrid);
    }

    private List<GridTask> subTasks() {
        List<GridTask> subTasks = new ArrayList<>();

        for (int i = 1; i <= this.upperBound / granularity; i++) {
            int upper = i * granularity - 1;
            int lower = (upper - granularity + 1);
            subTasks.add(new GridTask(game, lower, upper, workingGrid));
        }
        if (this.upperBound % granularity != 0) {
            subTasks.add(new GridTask(game, (this.upperBound / granularity) * granularity, this.upperBound, workingGrid));
        }
        return subTasks;
    }

    protected Grid updateGrid(int lowerBound, int upperBound) {
        for (int i = lowerBound; i <= upperBound && i < game.grid.getX(); i++) {
            for (int j = 0; j < game.grid.getY(); j++) {
                workingGrid.setAlive(i, j, game.checkNeighbours(i, j));
            }
        }
        return workingGrid;
    }

    public Grid getWorkingGrid() {
        return workingGrid;
    }

    @Override
    protected Object compute() {
        if (((upperBound + 1) - lowerBound) > granularity) {
            ForkJoinTask.invokeAll(subTasks());
        } else {
            return updateGrid(lowerBound, upperBound);
        }
        return getWorkingGrid();
    }
}
