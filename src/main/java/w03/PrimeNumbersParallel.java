package w03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class PrimeNumbersParallel extends RecursiveTask {

    private int lowerBound;
    private int upperBound;
    private int granularity;
    static final List<Integer> GRANULARITIES
      = Arrays.asList(1, 10, 100, 1000, 10000);
    private AtomicInteger noOfPrimeNumbers;

    PrimeNumbersParallel(int lowerBound, int upperBound, int granularity, AtomicInteger noOfPrimeNumbers) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.granularity = granularity;
        this.noOfPrimeNumbers = noOfPrimeNumbers;
    }

    PrimeNumbersParallel(int upperBound) {
        this(1, upperBound, 100, new AtomicInteger(0));
    }

    private PrimeNumbersParallel(int lowerBound, int upperBound, AtomicInteger noOfPrimeNumbers) {
        this(lowerBound, upperBound, 100, noOfPrimeNumbers);
    }

    private List<PrimeNumbersParallel> subTasks() {
        List<PrimeNumbersParallel> subTasks = new ArrayList<>();

        for (int i = 1; i <= this.upperBound / granularity; i++) {
            int upper = i * granularity;
            int lower = (upper - granularity) + 1;
            subTasks.add(new PrimeNumbersParallel(lower, upper, noOfPrimeNumbers));
        }
        return subTasks;
    }

    @Override
    protected Object compute() {
        System.out.println("compute called!");
        if (((upperBound + 1) - lowerBound) > granularity) {
            ForkJoinTask.invokeAll(subTasks());
        } else {
            findPrimeNumbers();
        }
        return noOfPrimeNumbers();
    }

    void findPrimeNumbers() {
        for (int num = lowerBound; num <= upperBound; num++) {
            if (isPrime(num)) {
                noOfPrimeNumbers.getAndIncrement();
            }
        }
    }

    private boolean isPrime(int number) {
        if (number == 2) {
            return true;
        }

        if (number == 1 || number % 2 == 0) {
            return false;
        }

        int noOfNaturalNumbers = 0;

        for (int i = 1; i <= number; i++) {
            if (number % i == 0) {
                noOfNaturalNumbers++;
            }
        }

        return noOfNaturalNumbers == 2;
    }

    public int noOfPrimeNumbers() {
        return noOfPrimeNumbers.intValue();
    }

    public static void main( String[] args) {

        PrimeNumbersParallel primes = new PrimeNumbersParallel(10000);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        Integer result = (Integer)pool.invoke(primes);
        System.out.println("Anzahl Primzahlen: " + result);
        pool.shutdown();
    }
}