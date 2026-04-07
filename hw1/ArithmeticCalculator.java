package ce326.hw1;
import java.util.*;  

public class ArithmeticCalculator {
	// HELPER METHODS FOR ERROR CHECKING //
	
	// Helper method to detect consecutive operators
	private static int checkOps(char Char1, char Char2) {
    	if(Char1 == '(' || Char1 == ')' || Char2 == '(' || Char2 == ')')
    		return(0);
    	else if (!Character.isDigit(Char1) && !Character.isDigit(Char2)) {
        	return(-1);
        }
        return(0);
    }
    
	// Helper method to detect operand after an opening parenthesis
    private static int OperandAfterPar(String expression, int i) {
    	int j = i+1;
    	while(j < expression.length() && expression.charAt(j) == ' ')
    		j++;
        if (j < expression.length() && Character.isDigit(expression.charAt(j))) {
        	return(-1);
        }
        return(0);
    }
    
    // Helper method to detect consecutive operators
    private static int OperatorAfterPar(String expression, int i) {
    	int j = i+1;
    	while(j < expression.length() && expression.charAt(j) == ' ')
    		j++;
    	char possibleOperator = expression.charAt(j);
        if (j < expression.length() && !Character.isDigit(possibleOperator) && possibleOperator != '(') {
        	return(-1);
        }
        return(0);
    }
    
    
    // MAIN METHOD //
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); //System.in is a standard input stream 
        
        System.out.println("Expression: ");
        String expression = sc.nextLine();
        
        
        //////////////////////////////// ERROR CHECKING ////////////////////////////////
        int rightPar = 0, leftPar = 0;
        char lastValidChar = '0';
        
        // Helping variables to detect consecutive operands
        boolean whiteSpaceDetected = false; 
        boolean firstValidCharDetected = false;
        
        char firstChar = expression.charAt(0);
        char lastChar  = expression.charAt(expression.length()-1);
        
        // Invalid first or last character
        if(firstChar == '+' || firstChar == '-' || firstChar == '*' || firstChar == 'x' || firstChar == '/' || firstChar == '^'
         || lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == 'x' || lastChar == '/' || lastChar == '^') {
        	System.out.printf("[ERROR] Starting or ending with operator\n");
        	System.exit(1);
        }
        
        // Linear error checking
        for(int i = 0; i < expression.length(); i++) {
        	char currentChar = expression.charAt(i);
        	if (currentChar == ' ') {
        		whiteSpaceDetected = true;
        		continue;
        	}
        	
        	// Check for invalid character
        	if ((currentChar != '(') &&
        		(currentChar != ')') &&
            	(currentChar != '+') &&
            	(currentChar != '-') &&
           		(currentChar != '*') &&
           		(currentChar != 'x') &&
           		(currentChar != '/') &&
           		(currentChar != '^') &&
           		(currentChar != '.') &&
           		(!Character.isDigit(currentChar))) {
            		System.out.printf("[ERROR] Invalid character\n");
            		System.exit(1);
        	}
        	
        	// Check consecutive operators
        	if(!Character.isDigit(currentChar)) {
        		int ConsOps = checkOps(currentChar, lastValidChar);
        		if (ConsOps == -1) {
                	System.out.printf("[ERROR] Consecutive operators\n");
                	System.exit(1);
        		}
        	}
        	
        	if (currentChar == '(') {
        		leftPar++;
        		if(Character.isDigit(lastValidChar) && i > 0) { // Check if the last character was an operand
            		System.out.printf("[ERROR] Operand before opening parenthesis\n");
            		System.exit(1);
        		}
        		if(OperatorAfterPar(expression, i) == -1) { // Check if the next character is an operator
                	System.out.printf("[ERROR] Operator appears after opening parenthesis\n");
                	System.exit(1);
        		}
        		if(lastValidChar == ')') { // Check if there is a closing parenthesis without a corresponding opening one
                	System.out.printf("[ERROR] ')' appears before opening parenthesis\n");
                	System.exit(1);
            	}
        	}
        	else if(currentChar == ')') {
        		rightPar++;
        		if(OperandAfterPar(expression, i) == -1) { // Check if the next character is an operand
                	System.out.printf("[ERROR] Operand after closing parenthesis\n");
                	System.exit(1);
        		}
        		if(!Character.isDigit(lastValidChar) && lastValidChar != ')') { // Check if the last character was an operator
                	System.out.printf("[ERROR] Operator appears before closing parenthesis\n");
                	System.exit(1);
        		}
        		if(rightPar > leftPar) {
        			System.out.printf("[ERROR] Closing unopened parenthesis\n");
                	System.exit(1);
        		}
        	}
        	
        	// Consecutive operands checking
        	if(Character.isDigit(currentChar) && Character.isDigit(lastValidChar) && firstValidCharDetected) {
        		if(whiteSpaceDetected) {
                	System.out.printf("[ERROR] Consecutive operands\n");
                	System.exit(1);
        		}
        	}

        	lastValidChar = currentChar;
        	whiteSpaceDetected = false;
        	firstValidCharDetected = true;
        }
        // Parenthesis error checking
        int ParCheck = leftPar-rightPar;
        if(ParCheck > 0) {
        	System.out.printf("[ERROR] Not closing opened parenthesis\n");
        	System.exit(1);
        }
        else if(ParCheck < 0) {
        	System.out.printf("[ERROR] Closing unopened parenthesis\n");
        	System.exit(1);
        }
        //////////////////////////////// END OF ERROR CHECKING ////////////////////////////////
        expression = expression.replaceAll("\\s+",""); // Removes whitespace in the expression
        CalcStruct calculator = new CalcStruct(expression);
        
        String option = sc.nextLine();
        sc.close();
        
        // Different outputs for each input option
    	if(option.equals("-s")) {
    		String PostfixResult = calculator.toString();
    		System.out.println("Postfix: " + PostfixResult);
    		System.out.println();
    	}
    	else if (option.equals("-c")) {
    		double result = calculator.calculate();
    		System.out.printf("Result: %.6f\n", result);
    	}
    	else if (option.equals("-d")) {
    		String DotStr = calculator.toDotString();
    		System.out.println();
    		System.out.println(DotStr);
    	}
    }
}
