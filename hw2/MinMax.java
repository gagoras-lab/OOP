package ce326.hw2;

import java.util.*;
import java.io.*;

public class MinMax {
	public static void main(String[] args) throws IOException{
		Scanner main_sc = new Scanner(System.in); //System.in is a standard input stream 
		StringBuilder strBuilder = new StringBuilder();
		Tree bintree = null;
		boolean chooseString = false; // Tree.toString() = false, Optimal.toString() = true
		
	    while(true) {
	    	//// AVAILABLE OPTIONS ////
	    	System.out.printf("\n");
	    	System.out.println("-i <filename>   :  insert tree from file\n"
	    					 + "-j [<filename>] :  print tree in the specified filename using JSON format\n"
	    					 + "-d [<filename>] :  print tree in the specified filename using DOT format\n"
	    					 + "-c              :  calculate tree using min-max algorithm\n"
	    					 + "-p              :  calculate tree using min-max and alpha-beta pruning optimization\n"
	    					 + "-q              :  quit this program\n");
	    	System.out.printf("$> ");
	    	//////////////////////////
	    	
	    	// Scan input from System.in
	    	String input = main_sc.nextLine();
	    	String scannedline = new String(input);
	    	String option = scannedline.substring(0,2);
	    	String filepath = null;
	    	
	    	// Get name of file path, if used
	    	if(scannedline.length() > 2) { 
	    		filepath = scannedline.substring(3,scannedline.length());
	    	}
	    	
	    	switch(option) {
	    		// Insert tree from file
	    		case "-i":
	    			File newJSON = new File(filepath);
	    			if (!newJSON.exists()) { // The file doesn't exist
	    				System.out.printf("\n");
	    		        System.out.println("Unable to find '" + filepath +"'");
	    		        System.out.printf("\n\n");
	    			}
	    			else if(!newJSON.canRead()) { // The file can't be read
	    				System.out.printf("\n");
	    				System.out.println("Unable to open '" + filepath +"'");
	    				System.out.printf("\n\n");
	    			}
	    			else { // Insert JSON file and build tree
		    		    try(Scanner insert_sc = new Scanner(newJSON)) {
		    		      while( insert_sc.hasNextLine() ) {
		    		        String str = insert_sc.nextLine();
		    		        strBuilder.append(str);
		    		        strBuilder.append("\n");
		    		      }
		    		      bintree = new Tree(strBuilder.toString());
		    		      if(Tree.root != null) {
		    		    	  System.out.printf("\n");
		    		    	  System.out.println("OK");
		    		    	  System.out.printf("\n\n");
		    		      }
		    		    } catch(FileNotFoundException ex) {
		    		    	System.out.printf("\n");
		    		    	System.out.println("Unable to open '" + filepath +"'");
		    		    	System.out.printf("\n\n");
		    		    }
	    			}
	    			break;
	    			
	    		
	    		// print tree in the specified filename using JSON format
	    		case "-j":
	    			boolean valid = true; // Checks the file validity before writing the JSON string
	    			
	    			// Create the txt file
	    			if(scannedline.length() > 2) {
		    			File PaintJsonFile = new File(filepath);
		    		    if (PaintJsonFile.exists()) { // The file already exists, don't overwrite
		    		    	System.out.printf("\n");
		    		    	System.out.println("File '"+ filepath +"' already exists");
		    		    	System.out.printf("\n\n");
		    		    	valid = false;
		    		    }
		    		    else { 
		    		    	try {
		    		    		PaintJsonFile.createNewFile();
		    		    		
		    		    		// File created
			    		    	valid = true;
		    		    	} catch (IOException ex1) { // The file cannot be written
		    		    		System.out.printf("\n");
		    		    		System.out.println("Unable to write '"+ filepath +"'");
			    		    	System.out.printf("\n\n");
			    		    	valid = false;
		    		    	}
		    		    }
	    			}
	    			
	    			// Write to the txt file
	    			if(valid == true && bintree != null) {
	    				String JSONTreeString = new String();
	    				if(chooseString == false)
	    					JSONTreeString = bintree.toString();
	    				else
	    					JSONTreeString = OptimalTree.toOptimalString();
		    			try {
		    				if(scannedline.length() > 2) { // Print JSON on txt file
			    				FileWriter myWriter = new FileWriter(filepath);
			    				myWriter.write(JSONTreeString);
			    				myWriter.close();
			    				System.out.printf("\n");
		    				}
		    				else {
		    					System.out.printf("\n");
		    					System.out.println(JSONTreeString); // Print JSON on System output
		    				}
		    				System.out.println("OK");
		    				System.out.printf("\n\n");
					    } catch (IOException e) {
					    	System.out.printf("\n");
					    	System.out.println("An error occurred.");
					    	System.out.printf("\n\n");
					    	e.printStackTrace();
					    }
	    			}
	    			break;
	    			
	    		
	    		// print tree in the specified filename using DOT format
	    		case "-d": 
	    			// Create the txt file
    				File PaintDotFile = new File(filepath);
    				if (PaintDotFile.exists()) {  // The file already exists, don't overwrite
    					System.out.printf("\n");
    					System.out.println("File '"+ filepath + "' already exists");
    		    		System.out.printf("\n\n");
    				}
    				else {
    					try {
    						PaintDotFile.createNewFile();
    						
    						// Print DOT string on txt file
    						System.out.printf("\n");
        		    		System.out.println("File created: " + PaintDotFile.getName());
        		    		System.out.printf("\n\n");
        		    		
        		    		// Write to the txt file
        		    		if(chooseString == false)
        		    			bintree.toDotFile(PaintDotFile);
    	    				else
    	    					OptimalTree.toOptimalDotFile(PaintDotFile);
        		    		
    					} catch (IOException ex2) { // The file cannot be written
    						System.out.printf("\n");
    						System.out.println("Unable to write '"+ filepath +"'");
    	    		    	System.out.printf("\n\n");
    					}
    				}
	    			break;
	    			
	    		
	    		// calculate tree using min-max algorithm
	    		case "-c":
	    			if(bintree != null) {
		    			Tree.minMax();
		    			
		    			System.out.printf("\n");
	    				System.out.println(Tree.findPath().toString().substring(1, Tree.findPath().toString().length()-1));
	    				System.out.printf("\n\n");
	    				chooseString = false;
	    			}
	    			else {
	    				System.out.printf("\n");
	    				System.out.println("Not OK");
	    				System.out.printf("\n\n");
	    			}
	    			break;
	    			
	    		
	    		// calculate tree using min-max and alpha-beta pruning optimization
	    		case "-p":
	    			if(bintree != null) {
		    			OptimalTree.minMax(-Double.MAX_VALUE, Double.MAX_VALUE);
		    			
	    				System.out.println(OptimalTree.findOptimalPath().toString().substring(1, OptimalTree.findOptimalPath().toString().length()-1));
	    				System.out.printf("\n\n");
	    				chooseString = true;
	    			}
	    			else {
	    				System.out.printf("\n");
	    				System.out.println("Not OK");
	    				System.out.printf("\n\n");
	    			}
	    			break;
	    			
	    		
	    		// quit this program
	    		case "-q":
	    			System.out.printf("\n");
	    			main_sc.close();
	    			return;
	    	}
	    }
	}
}
