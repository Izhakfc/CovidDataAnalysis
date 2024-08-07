package edu.upenn.cit594.datamanagement;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVLexer {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 5130409650040L;
    private final CharacterReader reader;
    private int nextInt = -2;
    
    enum STATES {ESCAPED, CR, UNESCAPED, ESC_QUOTE, ENDFILE, UNESC_CR}

    public CSVLexer(CharacterReader reader) {
        this.reader = reader;
    }
    
	  public int read() throws IOException{
		
		  if (nextInt != -2) {
				int temp = nextInt;
				nextInt = -2; 
				return temp;
			}
		  return reader.read(); 
	  }

 
  public int peek() throws IOException {
	if (nextInt == -2) {
		nextInt = reader.read();
	}
	return nextInt;
}
    

    /**
     * This method uses the class's {@code CharacterReader} to read in just enough
     * characters to process a single valid CSV row, represented as an array of
     * strings where each element of the array is a field of the row. If formatting
     * errors are encountered during reading, this method throws a
     * {@code CSVFormatException} that specifies the exact point at which the error
     * occurred.
     *
     * @return a single row of CSV represented as a string array, where each
     *         element of the array is a field of the row; or {@code null} when
     *         there are no more rows left to be read.
     * @throws IOException when the underlying reader encountered an error
     * @throws CSVFormatException when the CSV file is formatted incorrectly
     */
    public String[] readRow() throws IOException {
       
        StringBuilder sb = new StringBuilder();

    	ArrayList<String> row = new ArrayList<>();
    	
    	char a = 0;
    	int r = 0;
    	
    	int line = 0;
    	int field = 0;
    	int column = 0;
    	int roweq = 0;
        
    	STATES state = STATES.UNESCAPED;
    	 
   
    	
    	while ((r = read()) > 0) {
    		a = (char) r;
    		column ++;
    		
    		//char c = (char) reader.read();
    		//System.out.println(reader.read());
    	
        
        // array list for word but may need to just do a trie
 
	        // the state starts as an unescaped (not in any double quotes)
	       
	  
	        switch(state) {
	        	case UNESCAPED:
	        		switch(a) {
	        		
			        	// 10 rep line feed aka new line
			        	case '\n':
			        		line ++;
			        		roweq++;
			        		// add word in row
			        		row.add(sb.toString());
//			        		sb.setLength(0);
							return row.toArray(new String[0]);
		
			        	// 44 if comma
			        	case ',':
			        	// end of word/ a separate field
			        		// concatenate word in comma
			        		// add to row
			        		field++;
			        		row.add(sb.toString());
			        		sb.setLength(0);
			        		break;
			        	
			        	// 34 if double quote
			        	case '"':
			        		state = STATES.ESCAPED;
			        		break;
			        	// 13 if carriage return
			        	case '\r':
			        		//state = STATES.UNESC_CR;
				        		if (peek() != '\n') {
				        			System.out.println("This row is being skipped");
				        			continue;
				        		}
				        		else {
				        			// consume character
				        			line++;
									roweq++;
									read();
									// add word in row
					        		row.add(sb.toString());
					        		
									return row.toArray(new String[0]);
				        		}
				        // shouldn't be valid in a file unless in escaped field: semicolons, colons, straight slash thing
//			        	case ';': case ':':  case '|': case '/': 
//			        		throw new CSVFormatException();
			        		
					default:
			        		// keep adding char to word
			        		sb.append(a); 
			        	break;
			        		
			        }
			        break;
		       // need to add char to array list of words
		        // but need to concatenate that array list before i append it to string of rows
		        		
		        		
		        		// throw new CSVFormatException("CR not followed by LF, must be escaped.", );
		        	
		        		
		        // use enum, create states and use switch to switch between these states
		
			
			case ESCAPED:
				switch(a) {
					case '"':
						// check if there is a quotation mark after -> if it is append this one
						if (peek() == '"') {
							sb.append('"');
							read(); //read that quotation mark
							state = STATES.ESCAPED;
						}
						else {
							state = STATES.UNESCAPED;
						}
						
						break;
					
					default:
						// can add anythin in an escaped
						sb.append(a);
					break;
				}
				break;
			case ESC_QUOTE:
				switch(a) {
					case '"':
						sb.append('"');
						state = STATES.ESCAPED;
						break;
					default:
						// can add anything in escaped
						sb.append(a);
						state = STATES.UNESCAPED;
					break;
				}
			break;
			
			case UNESC_CR:
				switch(a) {
					case '\n':
						line++;
						roweq++;
						// add word in row
		        		row.add(sb.toString());
						return row.toArray(new String[0]);
					default:
						System.out.println("This row is being skipped");
	        			continue;
	        }
				}
    	}
    	
    	// String message, int line, int column, int row, int field
    	// if we never exited escape state
    	if (state == STATES.ESCAPED) {
    		System.out.println("This row is being skipped");
    	}
    	
    	if (sb.length() > 0) {
    		row.add(sb.toString());
    	}
    	if (!row.isEmpty()) {
    		return row.toArray(new String[0]);
    	}
    	else {
    		return null;
    	}
    	
    }
    
    

    /**
     * Feel free to edit this method for your own testing purposes. As given, it
     * simply processes the CSV file supplied on the command line and prints each
     * resulting array of strings to standard out. Any reading or formatting errors
     * are printed to standard error.
     *
     * @param args command line arguments (1 expected)
     * @throws CSVFormatException 
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("usage: CSVReader <filename.csv>");
            return;
        }
//        File f = new File("easy2.csv");
//        try {
//            System.out.println(f.getCanonicalPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        String filename = "/Users/amalsorathia/eclipse-workspace/594_hw_7/easy1.csv";
//        try (var reader = new CharacterReader(filename)) {
//            var csvReader = new CSVReader(reader);
//            String[] row;
//            while ((row = csvReader.readRow()) != null) {
//                System.out.println(Arrays.toString(row));
//            }
//        } catch (IOException | CSVFormatException e) {
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//        }
        String test = "Hi,mom\r\n";
		StringReader stringReader = new StringReader(test);
		CharacterReader reader1 = new CharacterReader(stringReader);
		CSVLexer csvReader = new CSVLexer(reader1);
		String[] result = csvReader.readRow();
//		CSVFormatException exception = assertThrows(CSVFormatException.class, () -> csvReader.readRow());
//		assertEquals("Quotation marks were not closed: error at 0, 6, 0, 0", exception.getMessage());
		
        

        /*
         * This block of code demonstrates basic usage of CSVReader's row-oriented API:
         * initialize the reader inside try-with-resources, initialize the CSVReader
         * using the reader, and repeatedly call readRow() until null is encountered. Since
         * CharacterReader implements AutoCloseable, the reader will be automatically
         * closed once the try block is exited.
         */
//        var filename = args[0];
//        try (var reader = new CharacterReader(filename)) {
//            var csvReader = new CSVReader(reader);
//            String[] row;
//            while ((row = csvReader.readRow()) != null) {
//                System.out.println(Arrays.toString(row));
//            }
//        } catch (IOException | CSVFormatException e) {
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//        }
    }

}
