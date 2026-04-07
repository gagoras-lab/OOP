package ce326.hw2;

import org.json.*;
import java.util.*;
import java.io.*;
import java.lang.Math;

public class Tree {
	public static TreeNode root;
	
	/////////////////////////
	///// TREE CREATION /////
	/////////////////////////
	
	// Constructor that builds the binary tree
    public Tree(String expression) throws JSONException {
    	if(isJSON(expression) == false) {
    		System.out.printf("\n");
    		System.out.println("Invalid format");
    		System.out.printf("\n\n");
    		root = null;
    	}
    	else {
    		JSONObject json = new JSONObject(expression);
            root = buildTree(json);
    	}
    }
    
    // Helper method to build the binary tree
    private TreeNode buildTree(JSONObject json) {
        String typeJson = json.getString("type");
        if(typeJson.equals("leaf")) {
        	TreeNode node = new TreeNode(json.getDouble("value"), typeJson);
        	return node;
        }
        else {
        	TreeNode node = new TreeNode(typeJson);
            JSONArray childrenJson = json.getJSONArray("children");
            if (childrenJson != null) {
            	for (int i = 0; i < childrenJson.length(); i++) {
                    JSONObject child = childrenJson.getJSONObject(i);
                    node.insertChild(i, buildTree(child));
                }
            }
            return node;
        }
    }
    
    // Method to check if given expression is in JSON format
    public boolean isJSON(String expression) {
    	try {
            new JSONObject(expression);
        } catch (JSONException ex) {
        	try {
        		new JSONArray(expression);
        	} catch (JSONException ex1) {
        		return false;
        	}
        }
    	return true;
    }
    
    
	///////////////////////////
	//// MIN MAX ALGORITHM ////
	///////////////////////////
    
    public static double minMax() {
    	return minMax(root);
    }
    
    private static double minMax(TreeNode node) {
    	if(node.type.equals("leaf"))
    		return node.value;
    	else if (node.type.equals("max")) {
        	double bestValue = -Double.MAX_VALUE;
        	for (TreeNode child : node.children) {
                double value = minMax(child);
                bestValue = Math.max(bestValue, value);
            }
        	node.value = bestValue;
            return bestValue;
        } else {
        	double bestValue = Double.MAX_VALUE;
            for (TreeNode child : node.children) {
                double value = minMax(child);
                bestValue = Math.min(bestValue, value);
            }
            node.value = bestValue;
            return bestValue;
        }
    }
    
    //////////////////////////
    //// SIZE CALCULATION ////
    //////////////////////////
    
    public static int size(TreeNode node) {
        int size = 1;
        
        if(node.type.equals("leaf"))
        	return 1;
        
        for (TreeNode child : node.children) {
            size += size(child);
        }
        return size;
    }
	
	/////////////////////////////
	//// OPTIMAL PATH SEARCH ////
	/////////////////////////////
	
    public static ArrayList<Integer> findPath() {
        ArrayList<Integer> path = new ArrayList<>();
        path = root.getPath();
        return path;
    }
    
	/////////////////////////////////
	//// TO STRING FUNCTIONALITY ////
	/////////////////////////////////
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringHelper(sb, root, 1);
        sb.append("\n");
        return sb.toString();
    }

    private static void toStringHelper(StringBuilder sb, TreeNode node, int level) {
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
        
        // Print the value of the node
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        if(node.type.equals("leaf"))
        	sb.append("\"value\": ").append(node.value);
        else
        	sb.append("\"value\": ").append(node.value).append(",\n");
        
        // Print the children of the node, if there are any
        if (!node.type.equals("leaf")) {
            for (int i = 0; i < level; i++) {
                sb.append("  ");
            }
            sb.append("\"children\": [\n");

            int lastChildIndex = node.children.size() - 1;
            for (int i = 0; i < node.children.size(); i++) {
                TreeNode child = node.children.get(i);
                toStringHelper(sb, child, level + 1);
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
	
	public void toDotFile(File file) {
		String final_string = toDotString(root);
		try {
			FileWriter myWriter = new FileWriter(file);
			myWriter.write(final_string);
			myWriter.close();
			System.out.printf("\n");
			System.out.println("OK");
			System.out.printf("\n\n");
	    } catch (IOException e) {
	    	System.out.printf("\n");
	    	System.out.println("Unable to write '"+ file.getName() + "'");
	    	System.out.printf("\n\n");
	    }
	}
	
	private static String toDotString(TreeNode node) {
        String str = "";

        if (node.type.equals("leaf")) {
            return str += node.hashCode() + " [label=\"" + node.value + "\", shape = circle, color = white]\n";
        }
        else {
        	for (TreeNode child : node.children) {
            	str += node.hashCode() + "->" + child.hashCode() + "\n";
                str += toDotString(child);
            }
        }

        return str;
    }
}
