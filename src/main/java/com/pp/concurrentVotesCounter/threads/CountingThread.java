package main.java.com.pp.concurrentVotesCounter.threads;

import main.java.com.pp.concurrentVotesCounter.util.CsvFileReader;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CountingThread extends Thread {

    private final CsvFileReader csvFileReader;
    private final CyclicBarrier barrier;
    private final Map<Integer, Integer> results;

    public CountingThread(String name, CsvFileReader csvFileReader, CyclicBarrier barrier, Map<Integer, Integer> results) {
        super(name);
        this.csvFileReader = csvFileReader;
        this.barrier = barrier;
        this.results = results;
    }

    @Override
    public void run() {
        long start = new Date().getTime();

        List<String> line;
        int partyNumber;

        try {
            while ((line = csvFileReader.getCsvLine()) != null) {
                for (int i = 1; i <= line.size() - 2; i += 2) {
                    int partyVotes = Integer.parseInt(line.get(i));

                    partyNumber = Integer.parseInt(line.get(i - 1));
                    results.compute(partyNumber, (key, val) -> (val == null) ? partyVotes : val + partyVotes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long end = new Date().getTime();

        System.out.println("Execution time of " + this.getName() + " : " + (end - start));

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}
