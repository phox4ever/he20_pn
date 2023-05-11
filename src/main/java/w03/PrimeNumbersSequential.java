package w03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class PrimeNumbersSequential extends RecursiveAction {

    private int lowerBound;
    private int upperBound;
    private int granularity;
    static final List<Integer> GRANULARITIES
      = Arrays.asList(1, 10, 100, 1000, 10000);
    private AtomicInteger noOfPrimeNumbers;

    PrimeNumbersSequential(int lowerBound, int upperBound, int granularity, AtomicInteger noOfPrimeNumbers) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.granularity = granularity;
        this.noOfPrimeNumbers = noOfPrimeNumbers;
    }

    PrimeNumbersSequential(int upperBound) {
        this(1, upperBound, 100, new AtomicInteger(0));
    }

    private PrimeNumbersSequential(int lowerBound, int upperBound, AtomicInteger noOfPrimeNumbers) {
        this(lowerBound, upperBound, 100, noOfPrimeNumbers);
    }

    private List<PrimeNumbersSequential> subTasks() {
        List<PrimeNumbersSequential> subTasks = new ArrayList<>();

        for (int i = 1; i <= this.upperBound / granularity; i++) {
            int upper = i * granularity;
            int lower = (upper - granularity) + 1;
            subTasks.add(new PrimeNumbersSequential(lower, upper, noOfPrimeNumbers));
        }
        return subTasks;
    }

    @Override
    protected void compute() {
        if (((upperBound + 1) - lowerBound) > granularity) {
            ForkJoinTask.invokeAll(subTasks());
        } else {
            findPrimeNumbers();
        }
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
        PrimeNumbersSequential primes = new PrimeNumbersSequential(10000);
        primes.findPrimeNumbers();
        System.out.println("Anzahl Primzahlen: " + primes.noOfPrimeNumbers);
    }
}