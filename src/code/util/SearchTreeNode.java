package code.util;

public class SearchTreeNode implements Comparable<SearchTreeNode> {
	State state;
	SearchTreeNode parent;
	String operator;
	int depth;
	Comparable pathCost;
	
	public SearchTreeNode(State state, SearchTreeNode parent, String operator, int depth, Comparable pathCost) {
		super();
		this.state = state;
		this.parent = parent;
		this.operator = operator;
		this.depth = depth;
		this.pathCost = pathCost;
	}


	public State getState() {
		return state;
	}

	public SearchTreeNode getParent() {
		return parent;
	}

	public String getOperator() {
		return operator;
	}

	public int getDepth() {
		return depth;
	}

	public Comparable getPathCost() {
		return pathCost;
	}


	@Override
	public int compareTo(SearchTreeNode o) {
		// TODO Auto-generated method stub
		return pathCost.compareTo(o.pathCost);
	}
	
	
}
