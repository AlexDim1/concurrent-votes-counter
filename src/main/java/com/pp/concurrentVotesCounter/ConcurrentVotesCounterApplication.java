package main.java.com.pp.concurrentVotesCounter;

import main.java.com.pp.concurrentVotesCounter.threads.CountingThread;
import main.java.com.pp.concurrentVotesCounter.util.CsvFileReader;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

public class ConcurrentVotesCounterApplication {

    // CHOOSE NUMBER OF THREADS
    private static final int NUM_THREADS = 8;

    // CHANGE PATHS TO SUIT YOUR FILE LOCATIONS
    private static final String VOTES_FILE_PATH = "E:\\Dev\\Java\\Parallel Programming\\concurrent-votes-counter\\src\\main\\resources\\votes_02.10.2022.csv";
    private static final String PARTIES_FILE_PATH = "E:\\Dev\\Java\\Parallel Programming\\concurrent-votes-counter\\src\\main\\resources\\cik_parties_02.10.2022.csv";

    private static CyclicBarrier barrier;

    /**
     * Stores the number and vote count of the parties
     */
    private static final Map<Integer, Integer> results = new ConcurrentHashMap<>();

    /**
     * Stores the number and name of the party on the ballot
     */
    private static final Map<Integer, String> parties = new HashMap<>();

    /**
     * Prints the final results when the barrier is tripped
     */
    private static final Runnable barrierAction = () -> {
        System.out.println();
        System.out.println("---02.10.2022 Parliamentary Election Results---");
        System.out.println();

        for (int i = 1; i <= results.keySet().size() + 1; i++) {
            String message = results.get(i) == null
                    ? "N/A"
                    : results.get(i).toString();

            String party = parties.get(i).replace("\r", "");

            System.out.println(message + ": " + i + " " + party);
        }
    };

    public static void main(String[] args) {
        barrier = new CyclicBarrier(NUM_THREADS, barrierAction);

        try {
            loadParties();

            CsvFileReader csvFileReader = new CsvFileReader(VOTES_FILE_PATH);

            processVotesFile(csvFileReader);
        } catch (FileNotFoundException e) {
            System.out.println("File " + VOTES_FILE_PATH + " doesn't exist!");
        }
    }

    /**
     * Initiates the reading of the file and starts all the {@link CountingThread}s
     * @param csvFileReader the {@link CsvFileReader} from which to read data
     */
    private static void processVotesFile(CsvFileReader csvFileReader) {
        for (int i = 1; i <= NUM_THREADS; i++) {
            CountingThread thread = new CountingThread("Counting Thread #" + i, csvFileReader, barrier, results);
            thread.start();
        }
    }

    /**
     * Reads and stores the number and party name from a CSV file into a {@link Map}
     */
    private static void loadParties() {
        try (CsvFileReader csvFileReader = new CsvFileReader(PARTIES_FILE_PATH)) {
            List<String> line;

            while ((line = csvFileReader.getCsvLine()) != null)
                parties.put(Integer.valueOf(line.get(0)), line.get(1));

        } catch (FileNotFoundException e) {
            System.out.println("File " + PARTIES_FILE_PATH + " doesn't exist!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}