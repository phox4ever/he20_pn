package w03;

import java.util.concurrent.*;

public class ForkJoinSum extends RecursiveTask<Long> {
    private static final int THRESHOLD = 1000; // Threshold for splitting tasks
    private int[] array;
    private int start;
    private int end;

    public ForkJoinSum(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            // If the task is small enough, compute the sum directly
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            // Split the task into subtasks
            int mid = (start + end) / 2;
            ForkJoinSum leftTask = new ForkJoinSum(array, start, mid);
            ForkJoinSum rightTask = new ForkJoinSum(array, mid, end);

            // Invoke the subtasks in parallel
            leftTask.fork();
            rightTask.fork();

            // Combine the results of subtasks
            long leftResult = leftTask.join();
            long rightResult = rightTask.join();

            // Return the combined result
            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) {
        // Create a ForkJoinPool instance
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

        // Create an example array
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }

        // Create a ForkJoinSum task for the entire array
        ForkJoinSum task = new ForkJoinSum(array, 0, array.length);

        // Submit the task to the ForkJoinPool and obtain the result
        long result = forkJoinPool.invoke(task);

        // Print the result
        System.out.println("Sum: " + result);
    }
}
