package org.arc2;

import org.apache.commons.codec.binary.Base64;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
import org.archive.io.ArchiveRecordHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * ArcReader
 * <p/>
 * Reads in a folder, subfolders and any .arc files therein. Or just an arc file if passed as an argument.
 * For each arc file, a folder named: /.file/ is created and all web material is placed into that. The filename
 * is the Base64 encoding of the url of that file plus the mimetype.
 * <p/>
 * The arc files cannot be zipped ( .gz ), as in certain JVM distributions the Zip reader may break. Use the
 * uncompressed .arc versions.
 * <p/>
 * Author: Lucien van Wouw <lwo@isg.nl>
 */
public class ArcReader {

    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final File arcFile;

    public ArcReader(File arcFile) {

        this.arcFile = arcFile;
    }

    private void dumpFiles() throws IOException {

        final ArchiveReader archiveReader = ArchiveReaderFactory.get(arcFile);
        final File folder = new File(arcFile.getParent() + File.separator + "." + arcFile.getName());
        log.info("URL\tArchive-date\tContent-type\tArchive-length\tprotocol\thost\tpath\tquery");
        while (dumpFile(archiveReader, folder));
    }

    /**
     * Dumps the file onto the filesystem
     *
     * @param archiveReader The reader, iterator
     * @param folder        Base folder
     * @return true when the read record get method was successfull
     * @throws IOException
     */
    private boolean dumpFile(ArchiveReader archiveReader, File folder) throws IOException {

        ArchiveRecord archiveRecord;
        try {
            archiveRecord = archiveReader.get();
        } catch (Exception e) {
            return false;
        }
        final ArchiveRecordHeader header = archiveRecord.getHeader();
        final int begin = header.getContentBegin();
        final String url = header.getUrl();
        final String message = String.format("%s\t%s\t%s\t%s", header.getDate(), header.getMimetype(), header.getLength(), url);
        log.info(message);
        final String filename = header.getMimetype() + "\t" + url;
        final byte[] bytes = Base64.encodeBase64(filename.getBytes());
        final File subf = new File(folder.getAbsoluteFile() + File.separator + extension(header.getMimetype()));
        subf.mkdirs();
        final String normalized = new String(bytes).replace("\\", "-").replace("/", "_");
        log.fine("filename=" + normalized);
        final String file = subf.getAbsolutePath() + File.separator + normalized;
        archiveRecord.skip(begin);
        final FileOutputStream os = new FileOutputStream(file);
        int b;
        while ((b = archiveRecord.read()) != -1) {
            os.write(b);
        }
        os.close();
        return true;
    }

    private String extension(String mime) {

        String[] split = mime.split("/");
        return split[split.length - 1];
    }

    /**
     * Just pass one or more arcFiles or folders that contain arc files.
     * This tool will then create a folder in each parent folder and dump all
     * arc files in it. After that, all will be ready for identification tooling.
     *
     * @param args file, files or folders
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.err.println("Usage: <an arc file, arc files or folders that contain arc files>");
            System.out.println("Example: myarcfile.arc or /my.folder.that.has.arc.files/");
            System.exit(1);
        }
        for (String arg : args) {
            getFolders(new File(arg));
        }
    }

    private static void getFolders(File f) throws IOException {

        if (f.isFile()) {
            dump(f);
        } else {
            final File[] files = f.listFiles();
            for (File file : files) {
                if (file.isDirectory() && !file.getAbsolutePath().startsWith("."))
                    getFolders(file);
                else {
                    dump(file);
                }
            }
        }

    }

    private static void dump(File file) throws IOException {

        final String absolutePath = file.getAbsolutePath();
        if (absolutePath.endsWith(".arc")) {    // ToDo: enable .gz
            ArcReader reader2 = new ArcReader(file);
            reader2.dumpFiles();
        }
    }
}
