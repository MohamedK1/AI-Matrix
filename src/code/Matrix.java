package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.function.Function;

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
	static int numberOfStates=0;
	public SearchTreeNode DFS(SearchTreeNode node,HashSet<State> visited,boolean limit,int limitedDepth) {
		State curState=node.getState();
	
		numberOfStates++;
		State s=node.getState();
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

//	public SearchTreeNode UCS(State initialState) {
//		SearchTreeNode root=new SearchTreeNode(initialState, null, "", 0, pathCostFunction(initialState));
//		//TODO change it with the encoding of the state as string
//		HashSet<State> visited= new HashSet();
//		PriorityQueue<SearchTreeNode> pq=new PriorityQueue<>();
//		pq.add(root);
//		int expandedNodes=0;
//		while(!pq.isEmpty()) {
//			SearchTreeNode treeNode=pq.poll();
//			State curState=treeNode.getState();
//			visited.add(curState);
//			if(isGoal(curState))
//				return treeNode;
//			System.out.println(++expandedNodes);
//			ArrayList<StateOperatorPair> nextStates = curState.expand();
//			for(StateOperatorPair stateOperator:nextStates) {
//				if(visited.contains(stateOperator.state)) {
//					continue;// avoid visiting already visted states
//				}
//				SearchTreeNode child=new SearchTreeNode(stateOperator.state,
//						treeNode, stateOperator.operator, treeNode.getDepth()+1, pathCostFunction(stateOperator.state));
//				pq.add(child);
//			}
//
//		}
//		return null;
//
//	}
//
//	
//	
//	public SearchTreeNode AS1(State initialState) {
//		SearchTreeNode root=new SearchTreeNode(initialState, null, "", 0, initialState.h1()+pathCostFunction(initialState));
//		//TODO change it with the encoding of the state as string
//		HashSet<State> visited= new HashSet();
//		PriorityQueue<SearchTreeNode> pq=new PriorityQueue<>();
//		pq.add(root);
//		int expandedNodes=0;
//		while(!pq.isEmpty()) {
//			SearchTreeNode treeNode=pq.poll();
//			State curState=treeNode.getState();
//			visited.add(curState);
//			if(isGoal(curState))
//				return treeNode;
//			System.out.println(++expandedNodes);
//			
//			ArrayList<StateOperatorPair> nextStates = curState.expand();
//			for(StateOperatorPair stateOperator:nextStates) {
//				if(visited.contains(stateOperator.state)) {
//					continue;// avoid visiting already visted states
//				}
//				SearchTreeNode child=new SearchTreeNode(stateOperator.state,
//						treeNode, stateOperator.operator, treeNode.getDepth()+1, stateOperator.state.h1()+pathCostFunction(stateOperator.state));
//				pq.add(child);
//			}
//
//		}
//		return null;
//
//	}
//
//
//	public SearchTreeNode AS2(State initialState) {
//		SearchTreeNode root=new SearchTreeNode(initialState, null, "", 0, initialState.h2()+pathCostFunction(initialState));
//		//TODO change it with the encoding of the state as string
//		HashSet<State> visited= new HashSet();
//		PriorityQueue<SearchTreeNode> pq=new PriorityQueue<>();
//		pq.add(root);
//		int expandedNodes=0;
//		while(!pq.isEmpty()) {
//			SearchTreeNode treeNode=pq.poll();
//			State curState=treeNode.getState();
//			visited.add(curState);
//			if(isGoal(curState))
//				return treeNode;
////			System.out.println(++expandedNodes);
//			
//			ArrayList<StateOperatorPair> nextStates = curState.expand();
//			for(StateOperatorPair stateOperator:nextStates) {
//				if(visited.contains(stateOperator.state)) {
//					continue;// avoid visiting already visted states
//				}
//				SearchTreeNode child=new SearchTreeNode(stateOperator.state,
//						treeNode, stateOperator.operator, treeNode.getDepth()+1, stateOperator.state.h2()+pathCostFunction(stateOperator.state));
//				pq.add(child);
//			}
//
//		}
//		return null;
//
//	}
//	
	
	public SearchTreeNode UCS(State initialState) {
		return genericSortedSearch(initialState, Matrix::UCSCost);
	}
	
	
	public SearchTreeNode AS1(State initialState) {
		return genericSortedSearch(initialState, Matrix::AS1Cost);
	}
	
	public SearchTreeNode AS2(State initialState) {
		return genericSortedSearch(initialState, Matrix::AS2Cost);
	}
	public SearchTreeNode GR1(State initialState) {
		return genericSortedSearch(initialState, Matrix::GR1Cost);
	}
	public SearchTreeNode GR2(State initialState) {
		return genericSortedSearch(initialState, Matrix::GR2Cost);
	}
	
	
	
	public SearchTreeNode genericSortedSearch(State initialState,Function<GenericState,Integer> function) {
//		System.out.println(function.apply(initialState));
		SearchTreeNode root=new SearchTreeNode(initialState, null, "", 0, function.apply(initialState));
		//TODO change it with the encoding of the state as string
		HashSet<State> visited= new HashSet();
		PriorityQueue<SearchTreeNode> pq=new PriorityQueue<>();
		pq.add(root);
		int expandedNodes=0;
		while(!pq.isEmpty()) {
			SearchTreeNode treeNode=pq.poll();
			State curState=treeNode.getState();
			pw.println(++expandedNodes);
			pw.println(treeNode.getOperator());
			int cost=(Integer) treeNode.getPathCost();
			int hostTrans=cost/1000;
			int agentsKilled=cost%1000;
			pw.println(hostTrans+" "+agentsKilled);
			pw.println(Utils.visualize(curState.getMatrix(), curState.getNeo(), curState.getCarriedHostages(), curState.getTelephoneBoothHostages()));

			visited.add(curState);
			if(isGoal(curState))
				return treeNode;
			
			ArrayList<StateOperatorPair> nextStates = curState.expand();
			for(StateOperatorPair stateOperator:nextStates) {
				if(visited.contains(stateOperator.state)) {
					continue;// avoid visiting already visted states
				}
				SearchTreeNode child=new SearchTreeNode(stateOperator.state,
						treeNode, stateOperator.operator, treeNode.getDepth()+1, function.apply(stateOperator.state));
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
			/*
			
			 * for visualization and tracing only 
			  
			 * */
//			State s=node.getState();
//			System.out.println(node.getOperator());
//			System.out.println(Utils.visualize(s.getMatrix(), s.getNeo(), s.getCarriedHostages(), s.getTelephoneBoothHostages()));
			
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

//	@Override
//	public Pair pathCostFunction(GenericState stateSequence) {
//		State s=(State)stateSequence;
//
//		// TODO Auto-generated method stub
//		return new Pair(s.getHostagesTransformed(),s.getKilledAgents());
//	}

	@Override
	public Integer pathCostFunction(GenericState stateSequence) {
		State s=(State)stateSequence;

		// TODO Auto-generated method stub
		return s.getHostagesTransformed()*1000+s.getKilledAgents();
	}
	
	public static int UCSCost(GenericState stateSequence) {
		State s=(State)stateSequence;

		// TODO Auto-generated method stub
		return s.getHostagesTransformed()*1000+s.getKilledAgents();

	}

	
	public static int AS1Cost(GenericState stateSequence) {
		State s=(State)stateSequence;

		// TODO Auto-generated method stub
		return s.h1()+s.getHostagesTransformed()*1000+s.getKilledAgents();

	}
	public static int AS2Cost(GenericState stateSequence) {
		State s=(State)stateSequence;

		// TODO Auto-generated method stub
		return s.h2()+s.getHostagesTransformed()*1000+s.getKilledAgents();

	}
	
	public static int GR1Cost(GenericState stateSequence) {
		State s=(State)stateSequence;

		// TODO Auto-generated method stub
		return s.h1();

	}
	public static int GR2Cost(GenericState stateSequence) {
		State s=(State)stateSequence;

		// TODO Auto-generated method stub
		return s.h2();

	}
	
	public static int func() {
		return 0;
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

	public static Pair randomGridCell(ArrayList<Pair> locations) {
		Random rand = new Random();
		int idx = rand.nextInt(locations.size());
		Pair ans = locations.get(idx);
		locations.remove(idx);
		return ans;
	}

	public static String genGrid() {
		Random rand = new Random();
		int m = rand.nextInt(11) + 5; // columns
		int n = rand.nextInt(11) + 5; // rows
		int cntHostages = rand.nextInt(8) + 3; 
		int c = rand.nextInt(4) + 1;
		int cntPills = rand.nextInt(cntHostages) + 1;
		int rem = (m * n) - cntHostages - cntPills - 2; // -2 is for neo and the telephone booth
		int cntAgents = rand.nextInt(rem + 1);
		rem -= cntAgents;
		int cntPads = rand.nextInt(rem + 1);
		if(cntPads%2!=0)
			cntPads --;
		ArrayList<Pair> locations = new ArrayList();
		for(int i = 0; i < n; i++)
			for(int j = 0; j < m; j++)
				locations.add(new Pair(i, j));
		String grid = "";
		grid+=(m + "," + n +";" + c+";");
		
		Pair neoLocation = randomGridCell(locations);
		grid+=(neoLocation.x+","+neoLocation.y+";");
		
		Pair telephoneBoothLocation = randomGridCell(locations);
		grid+=(telephoneBoothLocation.x+","+telephoneBoothLocation.y+";");
		
		for(int i = 0; i < cntAgents; i++) {
			Pair agentLocation = randomGridCell(locations);
			grid+=(agentLocation.x+","+agentLocation.y);
			grid+=(i < cntAgents - 1)?",":";";
		}
		
		for(int i = 0; i < cntPills; i++) {
			Pair pillLocation = randomGridCell(locations);
			grid+=(pillLocation.x+","+pillLocation.y);
			grid+=(i < cntPills - 1)?",":";";
		}
		
		for(int i = 0; i < cntPads; i+=2) {
			Pair padLocation1 = randomGridCell(locations);
			Pair padLocation2 = randomGridCell(locations);
			
			grid+=(padLocation1.x+","+padLocation1.y+","+padLocation2.x+","+padLocation2.y+",");			
			grid+=(padLocation2.x+","+padLocation2.y+","+padLocation1.x+","+padLocation1.y);			
			
			grid+=(i < cntPads- 2)?",":";";
		}
		
		for(int i = 0; i < cntHostages; i++) {
			Pair hostageLocation = randomGridCell(locations);
			int damage = rand.nextInt(99) + 1;
			
			grid+=(hostageLocation.x+","+hostageLocation.y + "," + damage);
			grid+=(i < cntHostages - 1)?",":";";
		}
		
		
		return grid;
		
	}
	
	
	
	
	static PrintWriter pw;
	public static void main(String[] args) throws FileNotFoundException {
		pw=new PrintWriter(new File("DFS trace.txt"));
		//	String grid="2,4;2;0,0;1,1;0,1;0,2;0,3,1,2,1,2,0,3;1,0,96";
		State state=Utils.parse("5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80");
		//	State state=Utils.parse(grid);
		Matrix mat=new Matrix();
		//	SearchTreeNode goalBFS= mat.BFS(state);
		//	System.out.println(goalBFS.getState().getNeo());
		//	System.out.println(constructPath(goalBFS));
		
//		SearchTreeNode goalDFS= mat.genericSortedSearch(state, Matrix::AS1Cost);
		
		SearchTreeNode goalDFS= mat.IDS(state);
//		System.out.println(numberOfStates);
//		pw.flush();
		State goalState=goalDFS.getState();
		System.out.println(goalDFS.getState().getNeo());
		System.out.println(constructPath(goalDFS));
		System.out.println(Utils.visualize(goalState.getMatrix(), goalState.getNeo(), goalState.getCarriedHostages(), goalState.getTelephoneBoothHostages()));
	}










}
