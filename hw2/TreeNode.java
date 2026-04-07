package ce326.hw2;

import java.util.*;

public class TreeNode extends Leaf{
	ArrayList<TreeNode> children;
	double alpha;
	double beta;
	String type;
	int bestIndex;
	boolean checked;
	
	TreeNode(String select) {
		this.alpha = -Double.MAX_VALUE;
		this.beta  = Double.MAX_VALUE;
		setChildrenSize();
		this.type = select;
		this.bestIndex = -1;
		this.checked = false;
	}
	
	TreeNode(ArrayList<TreeNode> value) {
		this.children = value;
	}
	
	TreeNode(double value, String select) {
		this.alpha = -Double.MAX_VALUE;
		this.beta  = Double.MAX_VALUE;
		super.value = value;
		this.type = select;
		this.checked = false;
	}

	void setChildrenSize() {
		this.children = new ArrayList<TreeNode>();
	}
	
	int getChildSize() {
		return children.size();
	}
	
	// Insert child in specific jndex of the node
	void insertChild(int pos, TreeNode X) {
		this.children.add(pos,X);
	}
	
	// Get child in specific jndex of the node
	static TreeNode getChild(int pos, TreeNode X) {
		return X.children.get(pos);
	}
	
	// Builds optimal path for regular MinMax algorithm
	public ArrayList<Integer> getPath() {
        if (this.type.equals("leaf")) {
            return new ArrayList<>(0);
        }

        int optimalChildIndex = -1;
        double optimalChildValue = this.value;

        for (int i = 0; i < getChildSize(); i++) {
            TreeNode child = children.get(i);
            double childValue = child.value;
            
            if(childValue == optimalChildValue) {
                optimalChildIndex = i;
            }
        }
        ArrayList<Integer> Path = children.get(optimalChildIndex).getPath();
        Path.add(0, optimalChildIndex);
        return Path;
    }
	
	// Builds optimal path for Alpha-Beta pruning algorithm
	public ArrayList<Integer> getOptimalPath() {
        if (this.type.equals("leaf")) {
            return new ArrayList<>(0);
        }

        int optimalChildIndex = this.bestIndex;
        ArrayList<Integer> Path = children.get(optimalChildIndex).getOptimalPath();
        Path.add(0, optimalChildIndex);
        return Path;
    }
}