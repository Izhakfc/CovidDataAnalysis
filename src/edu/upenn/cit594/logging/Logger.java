package edu.upenn.cit594.logging;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

public class Logger {
    private List<String> logs;
    private FileWriter fw = null;
    private PrintWriter pw = null;
    
    // filename member variable to store logger filename to be used later
    private String filename;
    
    // singleton instance
    private static Logger instance = new Logger();
    
    // private constructor
    private Logger() {}
    
    // singleton accessor method
    public static Logger getInstance() {
        return instance;
    }
    
    public void logEvent(String event) {
        if (pw != null) {
            synchronized (this) {
                pw.println(event);
                pw.flush();
            }
        }
    }
    
    public void setDestination(String filename) {
        if (filename != null && !filename.isEmpty()) {
            try {
                this.filename = filename;
                fw = new FileWriter(filename, true);
                pw = new PrintWriter(fw, true);  // true for auto-flush
            } catch (IOException e) {
                System.err.println("Error opening log file: " + e.getMessage());
            }
        }
    }
    
    public void setDestination(PrintStream filename) {
    		PrintWriter writer = new PrintWriter(System.err);
    }
    
    public void close() {
        if (pw != null) {
            pw.close();
        }
        if (fw != null) {
            try {
                fw.close();
            } catch (IOException e) {
                System.err.println("Error closing log file: " + e.getMessage());
            }
        }
    }
    
    public boolean isInitialized() {
        return pw != null && fw != null;
    }
}