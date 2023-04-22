package main.java.com.pp.concurrentVotesCounter.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows for concurrent reading of CSV files (; separated only!) line by line
 */
public class CsvFileReader implements AutoCloseable {

    private final FileReader fr;
    private final StringBuilder sb = new StringBuilder();

    private boolean quoteEncountered = false;

    /**
     *
     * @param fileLocation the absolute path of the CSV file to be read
     * @throws FileNotFoundException if the file doesn't exist in the specified location
     */
    public CsvFileReader(String fileLocation) throws FileNotFoundException {
        fr = new FileReader(fileLocation);
    }

    /**
     * Synchronized method for reading a line in a CSV file
     * Can be called by only one {@link Thread} at a time!
     *
     * @return a list containing the values on this line
     * @throws IOException if an I/O error occurs
     */
    public synchronized List<String> getCsvLine() throws IOException {
        sb.setLength(0);
        List<String> fileLine = new ArrayList<>();

        int i;
        while ((i = fr.read()) != -1) {
            char c = (char) i;

            if (c == 10) {// NEW LINE
                fileLine.add(sb.toString());
                sb.setLength(0);

                if (quoteEncountered) {
                    quoteEncountered = false;
                }
                return fileLine;
            } else if (c == '"') {// QUOTE
                quoteEncountered = !quoteEncountered;
            } else if (c == ';') {// SEMICOLON
                if (!quoteEncountered) {
                    fileLine.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }

        return null;
    }

    @Override
    public void close() throws Exception {
        fr.close();
    }
}
