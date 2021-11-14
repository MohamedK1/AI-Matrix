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

	
	static int expandedNodes;

	public  SearchTreeNode BFS(State initialState) {
		SearchTreeNode root=new SearchTreeNode(initialState, null, "", 0, 0);
		Queue<SearchTreeNode> queue=new LinkedList<>();
		queue.add(root);
		//TODO change it with the encoding of the state as string
		HashSet<String> visited= new HashSet();

		while(!queue.isEmpty()) {
			SearchTreeNode treeNode=queue.poll();
			State curState=treeNode.getState();
			visited.add(curState.encode());
			if(isGoal(curState))
				return treeNode;

			ArrayList<StateOperatorPair> nextStates = curState.expand();
			expandedNodes++;
			for(StateOperatorPair stateOperator:nextStates) {
				if(visited.contains(stateOperator.state.encode())) {
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
		HashSet<String> visited= new HashSet();
		return DFS(root,visited,false,Integer.MAX_VALUE);

	}
	static int numberOfStates=0;
	public SearchTreeNode DFS(SearchTreeNode node,HashSet<String> visited,boolean limit,int limitedDepth) {
		State curState=node.getState();
	
		numberOfStates++;
		State s=node.getState();
		if(limit&&node.getDepth()>limitedDepth)return null;
		if(isGoal(curState))return node;
		visited.add(curState.encode());
		ArrayList<StateOperatorPair> nextStates = curState.expand();
		expandedNodes++;
		for(StateOperatorPair stateOperator:nextStates) {
			if(visited.contains(stateOperator.state.encode()))continue;// avoid visiting already visted states

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
			HashSet<String> visited= new HashSet();
			SearchTreeNode node= DFS(root,visited,true,i);
			if(node!=null)return node;

		}
		return null;
	}
	
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
		HashSet<String> visited= new HashSet();
		PriorityQueue<SearchTreeNode> pq=new PriorityQueue<>();
		pq.add(root);
		while(!pq.isEmpty()) {
			SearchTreeNode treeNode=pq.poll();
			State curState=treeNode.getState();
//			pw.println(++expandedNodes);
//			pw.println(treeNode.getOperator());
//			int cost=(Integer) treeNode.getPathCost();
//			int hostTrans=cost/1000;
//			int agentsKilled=cost%1000;
//			pw.println(hostTrans+" "+agentsKilled);
//			pw.println(Utils.visualize(curState.getMatrix(), curState.getNeo(), curState.getCarriedHostages(), curState.getTelephoneBoothHostages()));

			visited.add(curState.encode());
			if(isGoal(curState))
				return treeNode;
			
			ArrayList<StateOperatorPair> nextStates = curState.expand();
			expandedNodes++;

			for(StateOperatorPair stateOperator:nextStates) {
				if(visited.contains(stateOperator.state.encode())) {
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
	//TODO check if you no longer have any hostages either transformed or not however, neo is dead so is this a gaol state ?
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
		Random rand = new Random((long)System.currentTimeMillis()%((int)1e9+7));
		int m = rand.nextInt(11) + 5; // columns
		int n = rand.nextInt(11) + 5; // rows
		int cntHostages = rand.nextInt(8) + 3; 
		int c = rand.nextInt(4) + 1;
		int cntPills = rand.nextInt(cntHostages) + 1;
		
		int rem = (m * n) - cntHostages - cntPills - 2; // -2 is for neo and the telephone booth
		if(rem<0)rem=0;
		
		int cntAgents = rand.nextInt(rem/2);
		rem -= cntAgents;
		if(rem<0)rem=0;
		
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
			grid+=(i < cntAgents - 1)?",":"";
		}
		grid+=";";
		
		for(int i = 0; i < cntPills; i++) {
			Pair pillLocation = randomGridCell(locations);
			grid+=(pillLocation.x+","+pillLocation.y);
			grid+=(i < cntPills - 1)?",":"";
		}
		grid+=";";
		
		for(int i = 0; i < cntPads; i+=2) {
			Pair padLocation1 = randomGridCell(locations);
			Pair padLocation2 = randomGridCell(locations);
			
			grid+=(padLocation1.x+","+padLocation1.y+","+padLocation2.x+","+padLocation2.y+",");			
			grid+=(padLocation2.x+","+padLocation2.y+","+padLocation1.x+","+padLocation1.y);			
			
			grid+=(i < cntPads- 2)?",":"";
		}
		grid+=";";
		for(int i = 0; i < cntHostages; i++) {
			Pair hostageLocation = randomGridCell(locations);
			int damage = rand.nextInt(99) + 1;
			
			grid+=(hostageLocation.x+","+hostageLocation.y + "," + damage);
			grid+=(i < cntHostages - 1)?",":"";
		}
		
		
		return grid;
		
	}
	

	public static String solve(String grid, String strategy, boolean visualize) throws Exception {
		State initialState=Utils.parse(grid);
		SearchTreeNode goal=null;
		Matrix mat=new Matrix();
		mat.initialState=initialState;
		expandedNodes=0;
		if(strategy.equals("BF")) {
			goal=mat.BFS(initialState);
		}else if(strategy.equals("DF")) {
			goal=mat.DFS(initialState);
			
		}else if(strategy.equals("ID")) {
			goal=mat.IDS(initialState);
			
		}else if(strategy.equals("UC")) {
			goal=mat.UCS(initialState);
			
		}else if(strategy.equals("GR1")) {
			goal=mat.GR1(initialState);
			
		}else if(strategy.equals("GR2")) {
			goal=mat.GR2(initialState);
			
		}else if(strategy.equals("AS1")) {
			goal=mat.AS1(initialState);
			
		}else if(strategy.equals("AS2")) {
			goal=mat.AS2(initialState);
			
		}else {
			throw new Exception("Enter a valid search strategy.");
		}
		
		String plan=constructPath(goal);
		State goalState=goal.getState();
		int deaths=goalState.getHostagesTransformed();
		int killed=goalState.getKilledAgents();
	
		//TODO handle the visualization
		
		return plan+deaths+";"+killed+";"+expandedNodes;
	}
	
	
	static PrintWriter pw;
	public static void main(String[] args) throws Exception {
		pw=new PrintWriter(new File("DFS trace.txt"));
//		String grid="2,4;2;0,0;1,1;0,1;0,2;0,3,1,2,1,2,0,3;1,0,96";
//		String grid="4,2;2;0,0;1,1;;;;";
		String grid="5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
		State state=Utils.parse("5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80");
		System.out.println(solve(grid, "AS2", false));
//		String grid=genGrid();
//		grid="14,15;2;8,13;0,10;13,8,0,6,3,7,9,1,11,6,0,11,13,7,6,9,4,0,4,4,1,8,10,11,6,7,2,1,3,0,5,5,0,8,7,13,7,1,7,0,6,12,7,9,0,13,10,10,5,11,1,6,3,2,11,10,14,5,13,4,6,13,12,2,12,10,5,9,1,7,14,4,11,8,0,2,8,10,2,7,1,13,11,7,5,0,6,1,1,2,11,5,0,4,11,13,13,5,12,13,11,9,14,12,2,13,2,2,1,3,1,11,4,5,12,8,5,8,4,11,0,12,10,1,6,11,8,4,11,3,9,8,10,4,7,7,6,10,5,13,13,2,3,8,1,4,9,7,14,13,8,1,9,12,10,8,10,0,5,4,6,6,3,12,4,12,2,8,1,12,14,10,1,0,3,6,6,5,2,12,12,4,8,0,13,10,12,6,11,2,6,3,13,9,2,0,2,11,13,6,1,1,14,7,2,6,1,10,0,9,4,13,7,10,1,9,12,5,8,5,14,6,7,12,5,1,5,2,9,3,11,12,4,10,10,6,2,10,10,13,9,9,9,4,7,6,7,4,0,3,6,2,3,1,9,10,4,2,10,12,14,2,8,9,14,3,4,6,14,11,0,7,3,4,3,13,7,5,10,2,6,4,12,11,5,10,14,1,1,5,8,7,14,0,7,8,13,12,2,5,5,7,5,6,12,12,7,3,14,9,3,3,8,3,8,6,13,0,7,11,5,12,12,3,9,11,12,7,12,1,11,0,2,9,13,3,4,3,10,5,12,9,0,5,6,8,4,9,9,13,9,2,11,11,4,7,12,0,9,5,11,1,9,0,4,8,2,3,0,1,8,11,11,4,13,1,9,6,0,0,8,2,8,8,13,13,10,7,3,10,10,9,3,9,13,11,7,2,10,3;14,8,4,1,5,3,3,5;2,4,84,6,0,56,3,11,55,8,12,16";
				
//		System.out.println(grid);
//		State state=Utils.parse(grid);
//		System.out.println(state.visualize());
		//	State state=Utils.parse(grid);
//		Matrix mat=new Matrix();
//			SearchTreeNode goalBFS= mat.GR2(state);
//			System.out.println(constructPath(goalBFS));
//			String encode=goalBFS.getState().encode();
//			State decState=State.decode(encode);
//			System.out.println(decState.equals(goalBFS.getState()));
//		SearchTreeNode goalDFS= mat.genericSortedSearch(state, Matrix::AS1Cost);
		
//		SearchTreeNode goalDFS= mat.AS2(state);
//		System.out.println(numberOfStates);
//		pw.flush();
//		State goalState=goalDFS.getState();
//		System.out.println(goalDFS.getState().getNeo());
//		System.out.println(constructPath(goalDFS));
//		System.out.println(Utils.visualize(goalState.getMatrix(), goalState.getNeo(), goalState.getCarriedHostages(), goalState.getTelephoneBoothHostages()));
	}










}
