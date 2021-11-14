package code;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import code.util.SearchTreeNode;
import code.util.State;
import code.util.State.StateOperatorPair;
import code.util.Utils;

public class Matrix extends GenericSearch{
	/*
	 * 
	 *  n*n size of the matrix. max=225
	 *  telophone booth, neo, hostage (min 3 max 10), pills ( max 10), agents (remaining ), pads
	 * 
	 * 6 * 5 * 4^8 * 2^215
	 * 
	 * 
	 * 
	 * */


	public  SearchTreeNode BFS(State initialState) {
		SearchTreeNode root=new SearchTreeNode(initialState, null, "", 0, 0);
		Queue<SearchTreeNode> queue=new LinkedList<>();
		queue.add(root);
		//TODO change it with the encoding of the state as string
		HashSet<State> visited= new HashSet();

		while(!queue.isEmpty()) {
			SearchTreeNode treeNode=queue.poll();
			State curState=treeNode.getState();
			visited.add(curState);
			if(isGoal(curState))
				return treeNode;

			ArrayList<StateOperatorPair> nextStates = curState.expand();
			for(StateOperatorPair stateOperator:nextStates) {
				if(visited.contains(stateOperator.state)) {
					continue;// avoid visiting already visted states
				}
				SearchTreeNode child=new SearchTreeNode(stateOperator.state, treeNode, stateOperator.operator, treeNode.getDepth()+1, 0);
				queue.add(child);
			}
		}
		return null;// means that there is no goal
	}

	public SearchTreeNode DFS(State initialState) {
		SearchTreeNode root=new SearchTreeNode(initialState, null, "", 0, 0);
		//TODO change it with the encoding of the state as string
		HashSet<State> visited= new HashSet();
		return DFS(root,visited,false,Integer.MAX_VALUE);

	}
	public SearchTreeNode DFS(SearchTreeNode node,HashSet<State> visited,boolean limit,int limitedDepth) {
		State curState=node.getState();
		if(limit&&node.getDepth()>limitedDepth)return null;
		if(isGoal(curState))return node;
		visited.add(curState);
		ArrayList<StateOperatorPair> nextStates = curState.expand();
		for(StateOperatorPair stateOperator:nextStates) {
			if(visited.contains(stateOperator.state))continue;// avoid visiting already visted states

			SearchTreeNode child=new SearchTreeNode(stateOperator.state, node, stateOperator.operator, node.getDepth()+1, 0);
			SearchTreeNode ans=DFS(child,visited,limit,limitedDepth);
			if(ans!=null)// if one of the dfs calls get an answer to satisfy goal function so I will return that answer
				return ans;
		}
		return null;


	}

	public SearchTreeNode IDS(State initialState) {

		for(int i=0;i<Integer.MAX_VALUE;i++) {
			SearchTreeNode root=new SearchTreeNode(initialState, null, "", 0, 0);
			//TODO change it with the encoding of the state as string
			HashSet<State> visited= new HashSet();
			SearchTreeNode node= DFS(root,visited,true,i);
			if(node!=null)return node;

		}
		return null;
	}

	public SearchTreeNode UCS(State initialState) {
		SearchTreeNode root=new SearchTreeNode(initialState, null, "", 0, new Pair(0,0));
		//TODO change it with the encoding of the state as string
		HashSet<State> visited= new HashSet();
		PriorityQueue<SearchTreeNode> pq=new PriorityQueue<>();
		pq.add(root);
		int expandedNodes=0;
		while(!pq.isEmpty()) {
			SearchTreeNode treeNode=pq.poll();
			State curState=treeNode.getState();
			visited.add(curState);
			if(isGoal(curState))
				return treeNode;
			ArrayList<StateOperatorPair> nextStates = curState.expand();
			for(StateOperatorPair stateOperator:nextStates) {
				if(visited.contains(stateOperator.state)) {
					continue;// avoid visiting already visted states
				}
				SearchTreeNode child=new SearchTreeNode(stateOperator.state,
						treeNode, stateOperator.operator, treeNode.getDepth()+1, pathCostFunction(stateOperator.state));
				pq.add(child);
			}

		}
		return null;

	}


	public static String constructPath(SearchTreeNode node) {
		Stack<String> sequence=new Stack();
		int cnt=0;
		while(node.getParent()!=null) {
			sequence.add(node.getOperator());
			cnt++;
			node=node.getParent();
		}
		String ans="";
		while(!sequence.isEmpty())
		{
			ans+=sequence.pop();
			cnt--;
			if(cnt==0) {
				ans+=";";
			}else {
				ans+=",";
			}
		}

		return ans;
	}

	@Override
	public ArrayList<GenericState> successors(GenericState state) {
		// TODO Auto-generated method stub
		State s=(State)state;
		ArrayList<StateOperatorPair> successors=s.expand();
		ArrayList<GenericState> output=new ArrayList<>();
		for(StateOperatorPair pair:successors)
			output.add(pair.state);
		return output;
	}

	@Override
	public boolean isGoal(GenericState state) {
		State s=(State)state;

		// TODO Auto-generated method stub
		return s.getHostages().size()==0&&s.getCarriedHostages().size()==0;
	}

	@Override
	public Pair pathCostFunction(GenericState stateSequence) {
		State s=(State)stateSequence;

		// TODO Auto-generated method stub
		return new Pair(s.getHostagesTransformed(),s.getKilledAgents());
	}

	private static class Pair implements Comparable<Pair>{
		int x,y;

		public Pair(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public int compareTo(Pair o) {
			// TODO Auto-generated method stub
			return x==o.x?y-o.y:x-o.x;
		}


	}


	public static void main(String[] args) {
		//	String grid="2,4;2;0,0;1,1;0,1;0,2;0,3,1,2,1,2,0,3;1,0,96";
		State state=Utils.parse("5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80");
		//	State state=Utils.parse(grid);
		Matrix mat=new Matrix();
		//	SearchTreeNode goalBFS= mat.BFS(state);
		//	System.out.println(goalBFS.getState().getNeo());
		//	System.out.println(constructPath(goalBFS));

		SearchTreeNode goalDFS= mat.BFS(state);
		State goalState=goalDFS.getState();
		System.out.println(goalDFS.getState().getNeo());
		System.out.println(constructPath(goalDFS));
		System.out.println(Utils.visualize(goalState.getMatrix(), goalState.getNeo(), goalState.getCarriedHostages(), goalState.getTelephoneBoothHostages()));
	}










}
