package filesystem;

import services.ServiceLocator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Default implementation for FileSystem that handles writing to (log)files.
 */
public final class FileSystem {

    /**
     * Writer to log files.
     */
    private Writer logWriter;

    /**
     * Name of the file where the writer puts the content.
     */
    public static final String LOGFILE_NAME = "logger.log";

    /**
     * Constructor.
     */
    public FileSystem() {
        File logFile = new File(FileSystem.LOGFILE_NAME);

        try {
           Writer fw = new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8);
           this.logWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers the file system object.
     * @param sL service locator that holds this object.
     */
    public static void register(ServiceLocator sL) {
        if (sL == null) {
            throw new IllegalArgumentException("The service locator can not be null");
        }
        sL.setFileSystem(new FileSystem());
    }

    /**
     * Writes the content to the logfile.
     * @param content Message to print
     */
    public void log(String content) {
        try {
            this.logWriter.write(content + "\n");
            this.logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Empties the file.
     */
    public void clearFile() {
        File file = null;
        try {
            file = this.getProjectFile(FileSystem.LOGFILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert file != null;
        try (OutputStream outputStream = new FileOutputStream(file);
             Writer w = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             PrintWriter pw = new PrintWriter(w, false)) {
            pw.flush();
            pw.close();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the project file specified by {@code filename} and checks if it is a valid file.
     * @param filename Name of the file to get.
     * @return File object.
     * @throws IOException Thrown file can't be found.
     */
    private File getProjectFile(String filename) throws IOException {
        File file = new File(filename);
        // The only reason we do this is to suppress a FindBugs warning
        final boolean didntExist = file.createNewFile();
        if (didntExist) {
            System.out.println("New file called \"" + filename + "\" created");
        }
        return file;
    }


    /**
     * Close all writers.
     * @throws IOException thrown when writer can not be closed.
     */
   public void closeWriter() throws IOException {
        logWriter.close();
   }
}
