package org.arc2;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.*;

/**
 * CollateCsv
 * <p/>
 * Reads in the droid csvFile (well any csv really, provided it has three required key fields: URI, TYPE and MIME_TYPE) and adds the Arc crawler's guess of what the mimetype could have been.
 * The end result will be two extra columns: url and mimetype
 * This ought to be sufficient to see the difference between the droid identifiction and that of the webcrawler.
 * <p/>
 * Author: Lucien van Wouw <lwo@iisg.nl>
 */
public class CollateCsv {

    final static String URI = "URI";
    final static String TYPE = "TYPE";
    final static String MIME_TYPE = "MIME_TYPE";
    final static String ARC_MIME_TYPE = "ARC_MIME_TYPE";
    final static String ARC_URL = "ARC_URL";

    private final File csvFile;

    public CollateCsv(File file) {
        this.csvFile = file;
    }

    private void collate() throws IOException {

        final FileInputStream fis = new FileInputStream(csvFile);
        final CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(fis)));

        final String final_csv = csvFile.getAbsolutePath() + ".final.csv";
        final FileOutputStream fos = new FileOutputStream(final_csv);
        final CSVWriter writer = new CSVWriter(new OutputStreamWriter(fos));

        final List<String> keys = new ArrayList(Arrays.asList(reader.readNext()));  // First row to contain our field values
        final int keyUri = keys.indexOf(URI);
        final int keyType = keys.indexOf(TYPE);
        final int keyMimetype = keys.indexOf(MIME_TYPE);
        if (keyUri == -1 || keyMimetype == -1 || keyType == -1) {  // must have these at least
            System.err.println("The csv file does not contain the required URI, TYPE or MIMETYPE fields.");
            System.exit(1);
        }

        keys.clear();
        keys.add(MIME_TYPE);
        keys.add(ARC_MIME_TYPE);
        keys.add(ARC_URL);
        writer.writeNext(keys.toArray(new String[]{}));

        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine[keyType].equalsIgnoreCase("file")) {
                final File u = new File(nextLine[keyUri]);
                final String name = u.getName().replace("_", "/").replace("-", "\\");
                final byte[] bytes = Base64.decodeBase64(name.getBytes());
                final String denormalized = new String(bytes);
                final String[] split = denormalized.split("\t", 2);
                final List<String> r = new ArrayList(keys.size());
                r.add(nextLine[keyMimetype]);
                for (String key : split) {
                    r.add(key);
                }
                writer.writeNext(r.toArray(new String[]{}));
                r.clear();
            }
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.err.println("Usage: <a csv file>");
            System.out.println("Example: myarcfile.csv");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("No such csv: " + file.getAbsolutePath());
            System.exit(1);
        }

        CollateCsv collateCsv = new CollateCsv(file);
        collateCsv.collate();
    }
}
