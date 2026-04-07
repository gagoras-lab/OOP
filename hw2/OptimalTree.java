package ce326.hw2;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONException;


public class OptimalTree extends Tree{
	
	public OptimalTree(String expression) throws JSONException {
		super(expression);
	}

	static TreeNode root = Tree.root;
	
	//Java program to demonstrate
	//working of Alpha-Beta Pruning
	public static double minMax(double alpha, double beta) {
		AtomicInteger checkedNodes = new AtomicInteger();
		double result = minMax(root, alpha, beta, checkedNodes);
		System.out.printf("\n[%d,%d] ", size(root), (size(root) - checkedNodes.get()));
		return result;
	}
	
	//Returns optimal value for
	//current player (Initially called for root)
	private static double minMax(TreeNode node, double InitAlpha, double InitBeta, AtomicInteger checkedNodes) {
		checkedNodes.incrementAndGet(); // Update the number of nodes that don't get pruned
		node.checked = true;
		
		if (node.type.equals("leaf")) {
            return node.value;
        }
		else if (node.type.equals("max")) {
	        double bestValue = -Double.MAX_VALUE;
	        int currIndex = 0;
	        int bestIndex = 0;
	        // Recursive check the children to get the best max value
	        for (TreeNode child : node.children) {
	            bestValue = Math.max(bestValue, minMax(child, node.alpha, InitBeta, checkedNodes));
	            if(bestValue > node.alpha)
	            	bestIndex = currIndex;
	            
	            if (InitBeta <= bestValue) { // Alpha-Beta pruning
	            	break;
	            }
	            node.alpha = Math.max(node.alpha, bestValue);
	            currIndex++;
	        }
	        node.value = bestValue;
	        node.bestIndex = bestIndex;
	        return bestValue;
	        
	    } else {
	        double bestValue = Double.MAX_VALUE;
	        int currIndex = 0;
	        int bestIndex = 0;
	        // Recursive check the children to get the best min value
	        for (TreeNode child : node.children) {
	            bestValue = Math.min(bestValue, minMax(child, InitAlpha, node.beta, checkedNodes));
	            if(bestValue < node.beta)
	            	bestIndex = currIndex;
	            
	            if (bestValue <= InitAlpha) { // Alpha-Beta pruning
	            	break;
	            }
	            node.beta = Math.min(node.beta, bestValue);
	            currIndex++;
	        }
	        node.value = bestValue;
	        node.bestIndex = bestIndex;
	        return bestValue;
	        
	    }
	}

	/////////////////////////////
	//// OPTIMAL PATH SEARCH ////
	/////////////////////////////
	
	public static ArrayList<Integer> findOptimalPath() {
        ArrayList<Integer> path = new ArrayList<>();
        path = root.getOptimalPath();
        return path;
    }
	
	/////////////////////////////////
	//// TO STRING FUNCTIONALITY ////
	/////////////////////////////////
	
	public static String toOptimalString() {
        StringBuilder sb = new StringBuilder();
        toOptimalStringHelper(sb, root, 1);
        sb.append("\n");
        return sb.toString();
    }

	private static void toOptimalStringHelper(StringBuilder sb, TreeNode node, int level) {
    	// Start new level
		for (int i = 0; i < level-1; i++) {
            sb.append("  ");
        }
		sb.append("{\n");
		
		// Print type of node
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        sb.append("\"type\": \"").append(node.type).append("\",\n");
        
        // Print the value of the node, if the node got checked
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        
        if(node.checked == true) {
		    if(node.type.equals("leaf"))
		    	sb.append("\"value\": ").append(node.value);
		    else
		    	sb.append("\"value\": ").append(node.value).append(",\n");
        } else {
        	if(node.value != 0) {
        		sb.append("\"value\": ").append(node.value).append(",\n");
        	
		        // Print the value of the node, if the node got checked
		        for (int i = 0; i < level; i++) {
		            sb.append("  ");
		        }
        	}
	        if(node.type.equals("leaf"))
	        	sb.append("\"pruned\": ").append(!node.checked);
	        else
	        	sb.append("\"pruned\": ").append(!node.checked).append(",\n");
        }
        
        // Print the children of the node, if there are any
        if (!node.type.equals("leaf")) {
            for (int i = 0; i < level; i++) {
                sb.append("  ");
            }
            sb.append("\"children\": [\n");

            int lastChildIndex = node.children.size() - 1;
            for (int i = 0; i < node.children.size(); i++) {
                TreeNode child = node.children.get(i);
                toOptimalStringHelper(sb, child, level + 1);
                if (i != lastChildIndex) {
                	if(child.type.equals("leaf")) {
                		sb.append("\n");
                		for (int j = 0; j < level; j++) {
                            sb.append("  ");
                        }
                        sb.append("},\n");
                	}
                	else
                		sb.append(",\n");
                }
                else {
                	if(child.type.equals("leaf")) {
                		sb.append("\n");
                		for (int j = 0; j < level; j++) {
                            sb.append("  ");
                        }
                		sb.append("}\n");
                	}
                	else
                		sb.append("\n");
                }
            }

            for (int i = 0; i < level; i++) {
                sb.append("  ");
            }
            sb.append("]\n");
            for (int i = 0; i < level-1; i++) {
                sb.append("  ");
            }
            sb.append("}");
        }
        
        // Specific case, only one node in the tree
        if (node.type.equals("leaf") && level == 1) {
        	sb.append("\n}");
        }
    }
	
	
	void toFile(File file) {
		String final_string = toString();
		try {
			FileWriter myWriter = new FileWriter(file);
			myWriter.write(final_string);
			myWriter.close();
	    } catch (IOException e) {
	    	System.out.printf("\n");
	    	System.out.println("Unable to write '"+ file.getName() + "'");
	    	System.out.printf("\n\n");
	    }
	}
	
	
	
	/////////////////////////////////////
	//// TO DOT STRING FUNCTIONALITY ////
	/////////////////////////////////////
	
	public static void toOptimalDotFile(File file) {
		String final_string = toDotString(root);
		try {
			FileWriter myWriter = new FileWriter(file);
			myWriter.write(final_string);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	private static String toDotString(TreeNode node) {
		String str = "";
		
		if (node.type.equals("leaf")) {
            return str += node.hashCode() + " [label=\"" + node.value + "\", shape = circle, color = white]\n";
        }
		else if(node.checked == false) {
			str += node.hashCode() + " [shape = circle, color = red]\n";
			for (TreeNode child : node.children) {
            	str += node.hashCode() + "->" + child.hashCode() + "\n";
                str += toDotString(child);
            }
		}
		else {
			str += node.hashCode() + " [label=\"" + node.value + "\", shape = circle, color = black]\n";
			for (TreeNode child : node.children) {
            	str += node.hashCode() + "->" + child.hashCode() + "\n";
                str += toDotString(child);
            }
		}
		
		return str;
	}
}