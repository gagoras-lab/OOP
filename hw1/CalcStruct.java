package ce326.hw1;

import java.lang.Math;

public class CalcStruct {

    // Binary tree node definition
    private static class TreeNode {
    	String value;
        char operator;
        
        TreeNode left;
        TreeNode right;
        
        TreeNode(String value) {
            this.value = value;
            this.operator = '\0';
            left = null;
            right = null;
        }
        TreeNode(char operator, TreeNode left, TreeNode right) {
            this.value = "\0";
            this.operator = operator;
            this.left = left;
            this.right = right;
        }
    }

    private TreeNode root;

    // Constructor builds the binary tree
    public CalcStruct(String expression) {
        root = buildTree(expression);
    }
    
    // Method to evaluate the expression
    public double calculate() {
        return calculate(root);
    }
    
    // Method to perform a post-order traversal of the binary tree
    public String toString() {
        StringBuilder sb = new StringBuilder();
        postOrderPrint(root, sb);
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    // Helper method to perform a post-order traversal of the binary tree
    private void postOrderPrint(TreeNode node, StringBuilder sb) {
        if (node == null) {
            return;
        }

        postOrderPrint(node.left, sb);
        postOrderPrint(node.right, sb);
        if(node.operator != '\0') {
        	sb.append(node.operator + " ");
        }
        else {
            if(node.value.charAt(0) == '(')
            	sb.append(node.value.substring(1, node.value.length()-1) + " ");
            else
            	sb.append(node.value + " ");
        }
    }
    
    // Helper method that is used by toDotString()
    public String toDotString(TreeNode node) {
        String str = "";

        if(node == null) {
            return "";
        }
        if(node.operator == '\0') {
            str += node.hashCode() + " [label=\"" + node.value + "\", shape = circle, color = black]\n"; 
        }
        else {
            str += node.hashCode() + " [label=\"" + node.operator + "\", shape = circle, color = black]\n";
            str += node.hashCode() + "->" + node.left.hashCode() + "\n";
            str += node.hashCode() + "->" + node.right.hashCode() + "\n";
        }

        if(node.right != null) {
            str += toDotString(node.right);
        }
        if(node.left != null) {
            str += toDotString(node.left);
        }
        return str;
    }
    
    // Public method that creates the string that describes the order of the Binary Tree
    public String toDotString() {
        return (toDotString(root));
    }
    
    // Method used to build the Binary Tree, based on the mathematical expression
    private static TreeNode buildTree(String expression) {
        int length = expression.length();
        int index = findOperator(expression);
        
        // Expression contains only an operand
        if (index == -1) {
        	return new TreeNode(expression);
            
        // Specific case when expression starts and ends with parentheses
        } else if (expression.charAt(0) == '(' && expression.charAt(length-1) == ')'){
            char operator = expression.charAt(index);
            String leftExpr = expression.substring(1, index);
            String rightExpr = expression.substring(index + 1, length-1);
            TreeNode left = buildTree(leftExpr);
            TreeNode right = buildTree(rightExpr);
            return new TreeNode(operator, left, right);
        
        // General case 
        } else {
            char operator = expression.charAt(index);
            String leftExpr = expression.substring(0, index);
            String rightExpr = expression.substring(index + 1, length);
            TreeNode left = buildTree(leftExpr);
            TreeNode right = buildTree(rightExpr);
            return new TreeNode(operator, left, right);
        }
    }
    
    // Method to find the first operator starting from the end of the expression
    private static int findOperator(String expression) {
        int length = expression.length();
        int parenCount = 0;
        int i;
        int best_index = -1;
        int best_operator_priority = -1;
        
        // Specific case when the expression starts and ends with parentheses,
        // in which we skip both of them and check the rest of the expression
        if(expression.charAt(0) == '(' && expression.charAt(length-1) == ')') {
            for (i = length - 2; i >= 1; i--) {
            	char c = expression.charAt(i);
                
                if (c == ')') {
                    parenCount++;
                } else if (c == '(') {
                    parenCount--;
                }
    
                if (parenCount == 0) {
                    if ((c == '+' || c == '-') && best_operator_priority < 3) {
                    	best_index = i;
                    	best_operator_priority = 3;
                    } else if ((c == '*' || c == 'x' || c == '/') && best_operator_priority < 2) {
                    	best_index = i;
                    	best_operator_priority = 2;
                    }
                    else if (c == '^' && best_operator_priority < 1) {
                    	best_index = i;
                    	best_operator_priority = 1;
                    }
                }
            }
            if(parenCount == 0) {
            	return best_index;
            }
            return -1;
        }
        // General search of the last operator in the expression,
        // following the math operator priority order
        else {
            for (i = length - 1; i >= 0; i--) {
                char c = expression.charAt(i);
                
                if (c == ')') {
                    parenCount++;
                } else if (c == '(') {
                    parenCount--;
                }
    
                if (parenCount == 0) {
                	if ((c == '+' || c == '-') && best_operator_priority < 3) {
                    	best_index = i;
                    	best_operator_priority = 3;
                    } else if ((c == '*' || c == 'x' || c == '/') && best_operator_priority < 2) {
                    	best_index = i;
                    	best_operator_priority = 2;
                    }
                    else if (c == '^' && best_operator_priority < 1) {
                    	best_index = i;
                    	best_operator_priority = 1;
                    }
                }
            }
            if(parenCount == 0) {
            	return best_index;
            }
            return -1;
        }
    }
    
    // Method to calculate the individual operations, going down the binary tree
    private double calculate(TreeNode node) {
        
        if (node.operator == '+') {
            return calculate(node.left) + calculate(node.right);
        } else if (node.operator == '-') {
            return calculate(node.left) - calculate(node.right);
        } else if (node.operator == '*') {
            return calculate(node.left) * calculate(node.right);
        } else if (node.operator == 'x') {
            return calculate(node.left) * calculate(node.right);
        } else if (node.operator == '/') {
            return calculate(node.left) / calculate(node.right);
        } else if (node.operator == '^') {
            return Math.pow(calculate(node.left), calculate(node.right));
        } else {
            return Double.parseDouble(node.value);
        }
    }
}