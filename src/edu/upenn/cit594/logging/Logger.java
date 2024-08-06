package edu.upenn.cit594.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Logger {
    
	private List<String> logs;
	private FileWriter fw = null;

	
    // filename member variable to store logger filename to be used later
    private String filename;
    
    // singleton instance
    private static Logger instance = new Logger();

    // to be used later in the same class
    PrintWriter pw = null;

    // private constructor
    private Logger() {}
    
    
    // singleton accessor method
    public static Logger getInstance() {
    	if (instance == null) {
    		instance = new Logger();
    	}
        return instance;
    }
    
    public void logEvent(String event) {
    	synchronized (this) {
    		try {
			fw.append(event);
			fw.append("\n");
			fw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    }
    public void setDestination(String filename) {
    	try {
    		this.filename = filename;
			fw = new FileWriter(filename, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    public void close() {
    	if (fw != null) {
    	try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
    }
  
   
    

}