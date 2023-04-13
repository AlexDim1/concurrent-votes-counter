package com.pp.concurrentVotesCounter.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvFileReader implements AutoCloseable {

    private final FileReader fr;
    private final StringBuilder sb = new StringBuilder();

    private boolean quoteEncountered = false;

    public CsvFileReader(String fileLocation) throws FileNotFoundException {
        fr = new FileReader(fileLocation);
    }

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
                    //System.out.println("Warning: File inconsistency - a quote not closed till End Of Line reached!");
                    quoteEncountered = false;
                }
                return fileLine;
            } else if (c == '"') {// QUOTE
                quoteEncountered = !quoteEncountered;
            } else if (c == ',') {// COMMA
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

//    public String getCsvCell(final int index) throws IOException {
//        sb.setLength(0);
//
//        int commaOccurrences = 0;
//        boolean capture = (index == 0);
//
//        while ((i = fr.read()) != -1) {
//            char c = (char) i;
//
//            if (c == ',' && !quoteEncountered) {
//                if (commaOccurrences == index) {
//                    capture = false;
//                }
//            } else if (c == 10) {
//                break;
//            } else if (c == '"') {
//                quoteEncountered = !quoteEncountered;
//            } else {
//                if (capture) {
//                    sb.append(c);
//                }
//            }
//        }
//
//        return sb.toString();
//    }

    @Override
    public void close() throws Exception {
        fr.close();
    }

}
