package code.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import code.GenericState;
import code.util.Utils.Pair;

public class State implements GenericState {

	Neo neo;
	int c;

	ArrayList<Hostage> hostages; // carries alived hostages or transformed hostages in case that damage=100
	ArrayList<Hostage> carriedHostages;
	ArrayList<Hostage> telephoneBoothHostages;

	int hostagesTransformed;//x
	int killedAgents;//general counter for all killed agents included those who are transformed from hostages >=x

	ArrayList<Agent> agents;
	ArrayList<Pill> pills;
	ArrayList<Pad> pads;
	TelephoneBooth telephoneBooth;
	Cell[][] matrix;	
	boolean[][] visitedCell;




	public State(Neo neo, int c, ArrayList<Hostage> hostages, ArrayList<Hostage> carriedHostages, ArrayList<Agent> agents,
			ArrayList<Pill> pills, ArrayList<Pad> pads, TelephoneBooth telephoneBooth, Cell[][] matrix,int hostagesTransformed,int killedAgents,ArrayList<Hostage>telephoneBoothHostages) {
		super();
		this.neo = neo;
		this.c = c;
		this.hostages = hostages;
		this.carriedHostages = carriedHostages;
		this.agents = agents;
		this.pills = pills;
		this.pads = pads;
		this.telephoneBooth = telephoneBooth;
		this.matrix = matrix;
		this.hostagesTransformed=hostagesTransformed;
		this.killedAgents=killedAgents;
		this.telephoneBoothHostages=telephoneBoothHostages;
	}





	public State clone() {



		Neo neo=this.neo.clone();
		int c=this.c;

		ArrayList<Hostage> hostages=Utils.cloneList(this.hostages);
		ArrayList<Hostage> carriedHostages=Utils.cloneList(this.carriedHostages);
		ArrayList<Agent> agents=Utils.cloneList(this.agents);
		ArrayList<Pill> pills=Utils.cloneList(this.pills);
		ArrayList<Pad> pads=Utils.cloneList(this.pads);
		TelephoneBooth telephoneBooth=this.telephoneBooth.clone();
		ArrayList<Hostage>telephoneBoothHostages=Utils.cloneList(this.telephoneBoothHostages);

		int m=matrix.length,n=matrix[0].length;

		Cell[][] matrix= Utils.buildMatrix(m, n, telephoneBooth, agents, pills, pads, hostages);

		return new State(neo, c, hostages, carriedHostages, agents, pills, pads, telephoneBooth, matrix,hostagesTransformed,killedAgents,telephoneBoothHostages);

	}


	/*
M,N; C; NeoX,NeoY,NeoDamage; TelephoneX,TelehoneY;
AgentX1,AgentY1, ...,AgentXk,AgentYk;
PillX1,PillY1, ...,PillXg,PillYg;
StartPadX1,StartPadY1,FinishPadX1,FinishPadY1,...,
StartPadXl,StartPadYl,FinishPadXl,FinishPadYl;
HostageX1,HostageY1,HostageDamage1, ...,HostageXw,HostageYw,HostageDamag;
CarriedHostageX1,CarriedHostageY1,CarriedHostageDamage1, ...,CarriedHostageXw,CarriedHostageYw,CarriedHostageDamag;
TelephoneBoothHostageX1,TelephoneBoothHostageY1,TelephoneBoothHostageDamage1, ...,TelephoneBoothHostageXw,TelephoneBoothHostageYw,TelephoneBoothHostageDamag;
hostagesTransformed;killedAgents
	 */
	public String encode() {
		return encode(false);
	}
	public String encode(boolean skipDamage) {
		skipDamage=false;
		int m = matrix[0].length;
		int n = matrix.length;
		String res = m+","+n+";"+c+";"+neo.x+","+neo.y+","+neo.damage+";"+telephoneBooth.x+","+telephoneBooth.y+";";

		//Agents encoding
		for(int i = 0; i < agents.size(); i++) {
			Agent agent=agents.get(i);
			res+=""+agent.x+","+agent.y;

			res+=(i < agents.size()-1)?",":"";
		}
		res+=";";

		//Pills encoding
		for(int i = 0; i < pills.size(); i++) {
			Pill pill=pills.get(i);
			res+=""+pill.x+","+pill.y;

			res+=(i < pills.size()-1)?",":"";
		}
		res+=";";

		//Pads encoding
		for(int i = 0; i < pads.size(); i++) {
			Pad srcPad=pads.get(i);
			for(int j = 0; j< srcPad.destinations.size();j++) {		
				Pad dest = srcPad.destinations.get(j);
				res+=""+srcPad.x+","+srcPad.y+","+dest.x+","+dest.y;
				res+=(j < srcPad.destinations.size() - 1)? ",":"";
			}

			res+=(i < pads.size()-1)?",":"";
		}
		res+=";";


		//Hostages encoding
		for(int i = 0; i < hostages.size(); i++) {
			Hostage hostage=hostages.get(i);
			
			res+=""+hostage.x+","+hostage.y;
			if(!skipDamage)
				res+=","+hostage.damage;

			res+=(i < hostages.size()-1)?",":"";
		}
		res+=";";

		//Carried hostages encoding
		for(int i = 0; i < carriedHostages.size(); i++) {
			Hostage hostage=carriedHostages.get(i);
			
			res+=""+hostage.x+","+hostage.y;
			if(!skipDamage)
				res+=","+hostage.damage;

			
			res+=(i < carriedHostages.size()-1)?",":"";
		}
		res+=";";

		//Telephone booth hostages encoding
		for(int i = 0; i < telephoneBoothHostages.size(); i++) {
			Hostage hostage=telephoneBoothHostages.get(i);

			res+=""+hostage.x+","+hostage.y;
			if(!skipDamage)
				res+=","+hostage.damage;

			res+=(i < telephoneBoothHostages.size()-1)?",":"";
		}
		res+=";";

		// HostagesTransformed and agents killed
		res+=hostagesTransformed+";"+killedAgents;		
		return res;	
	}
	
	public static State decode(String s) {

		StringTokenizer st=new StringTokenizer(s,";");
		String []arr = s.split(";");
		int idx = 0;
		//  M,N size of grid
		StringTokenizer internalSt=new StringTokenizer(arr[idx++],",");
		int n=Integer.parseInt(internalSt.nextToken());//columns
		int m=Integer.parseInt(internalSt.nextToken());//rows
		
		
		
		// c
		internalSt=new StringTokenizer(arr[idx++],",");
		int c=Integer.parseInt(internalSt.nextToken());

		//Neo location and damage
		internalSt=new StringTokenizer(arr[idx++],",");
		int neoX=Integer.parseInt(internalSt.nextToken());
		int neoY=Integer.parseInt(internalSt.nextToken());
		int neoDamage=Integer.parseInt(internalSt.nextToken());
		Neo neo=new Neo(neoX, neoY);
		neo.damage = neoDamage;


		//Telephone booth location
		internalSt=new StringTokenizer(arr[idx++],",");
		int teleX=Integer.parseInt(internalSt.nextToken());
		int teleY=Integer.parseInt(internalSt.nextToken());
		TelephoneBooth tele=new TelephoneBooth(teleX, teleY);


		//Agent locations
		ArrayList<Agent> agentList=new ArrayList();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		while(internalSt.hasMoreElements()) {
			int agentX=Integer.parseInt(internalSt.nextToken());
			int agentY=Integer.parseInt(internalSt.nextToken());
			Agent agent=new Agent(agentX,agentY);
			agentList.add(agent);
		}
		}
		idx++;

		//pills location 
		ArrayList<Pill> pillList=new ArrayList();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		while(internalSt.hasMoreElements()) {
			String x=internalSt.nextToken();
			int pillX=Integer.parseInt(x);
			int pillY=Integer.parseInt(internalSt.nextToken());
			Pill pill=new Pill(pillX,pillY);
			pillList.add(pill);
		}
		}
		idx++;

		//pads location 
		ArrayList<Pad> padList=new ArrayList<>();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		HashMap<Pair,Pad> padMap=new HashMap<>();// stores reference to each already created pad to avoid recreating it.
		while(internalSt.hasMoreElements()) {
			int startX=Integer.parseInt(internalSt.nextToken());
			int startY=Integer.parseInt(internalSt.nextToken());
			Pair startLocation=new Pair(startX,startY);
			Pad start=new Pad(startX,startY);
			if(padMap.get(startLocation)!=null)
			{
				start=padMap.get(startLocation);
			}else {
				padMap.put(startLocation, start);
			}
			
			
			int finishX=Integer.parseInt(internalSt.nextToken());
			int finishY=Integer.parseInt(internalSt.nextToken());
			Pair finishLocation=new Pair(finishX,finishY);
			Pad finish=new Pad(finishX,finishY);
			if(padMap.get(finishLocation)!=null)
			{	
				finish=padMap.get(finishLocation);
			}else {
				padMap.put(finishLocation, finish);
			}
			
			start.addDestination(finish);
			
		}

		for(Entry<Pair,Pad> e:padMap.entrySet() ) {
			padList.add(e.getValue());
		}
		}
		idx++;
		
		//hostage location
		
		ArrayList<Hostage> hostageList=new ArrayList();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		while(internalSt.hasMoreElements()) {
			int hostageX=Integer.parseInt(internalSt.nextToken());
			int hostageY=Integer.parseInt(internalSt.nextToken());
			int damage=Integer.parseInt(internalSt.nextToken());
			Hostage hostage=new Hostage(hostageX,hostageY,damage);
			hostageList.add(hostage);
		}
		}
		idx++;

		ArrayList<Hostage> carriedHostageList=new ArrayList();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		while(internalSt.hasMoreElements()) {
			int hostageX=Integer.parseInt(internalSt.nextToken());
			int hostageY=Integer.parseInt(internalSt.nextToken());
			int damage=Integer.parseInt(internalSt.nextToken());
			Hostage hostage=new Hostage(hostageX,hostageY,damage);
			carriedHostageList.add(hostage);
		}
		}
		idx++;

		ArrayList<Hostage> telephoneBoothHostageList=new ArrayList();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		while(internalSt.hasMoreElements()) {
			int hostageX=Integer.parseInt(internalSt.nextToken());
			int hostageY=Integer.parseInt(internalSt.nextToken());
			int damage=Integer.parseInt(internalSt.nextToken());
			Hostage hostage=new Hostage(hostageX,hostageY,damage);
			telephoneBoothHostageList.add(hostage);
		}
		}
		idx++;
		
		int transformedHostages = Integer.parseInt(arr[idx++]);
		int agentsKilled = Integer.parseInt(arr[idx++]);
		

				
		
		Cell[][] matrix= Utils.buildMatrix(m,n, tele, agentList, pillList, padList, hostageList);
		
//		System.out.println(visualize(matrix,neo));
		State state= new State(neo, c, hostageList, carriedHostageList, agentList, pillList,padList, tele, matrix,transformedHostages,agentsKilled,telephoneBoothHostageList);
		return state;

	}




	public ArrayList<StateOperatorPair> expand(String prevAction){
		ArrayList<StateOperatorPair> nextStates=new ArrayList<>();
		if(neo.damage>=100)return nextStates;

		int neoX=neo.x,neoY=neo.y;
		Cell curCell=matrix[neoX][neoY];

		if(curCell!=null&&curCell.cellContent instanceof Hostage) {// neo is with a hostage in the current cell;

			handleCarryHostage(nextStates, neoX, neoY);
		}

		if(curCell!=null&&curCell.cellContent instanceof TelephoneBooth) {
			if(telephoneBooth.x==neoX&&telephoneBooth.y==neoY)
				handleDropHostages(nextStates);
		}

		if(curCell!=null&&curCell.cellContent instanceof Pill) {
			handleTakePill(nextStates, neoX, neoY);
		}

		if(curCell!=null&&curCell.cellContent instanceof Pad&&!prevAction.equals("fly")) {
			handleUsePad(nextStates, neoX, neoY);
		}


		handleKillAgent(nextStates, neoX, neoY);

		if(!prevAction.equals("up"))
		handleMove(nextStates, neoX+1, neoY,"down");

		if(!prevAction.equals("down"))
		handleMove(nextStates, neoX-1, neoY,"up");

		if(!prevAction.equals("left"))
		handleMove(nextStates, neoX, neoY+1,"right");

		if(!prevAction.equals("right"))
		handleMove(nextStates, neoX, neoY-1,"left");

		return nextStates;
	}


	public static class StateOperatorPair{
		public State state;public String operator;
	
		public StateOperatorPair(State state, String operator) {
			super();
			this.state = state;
			this.operator = operator;
		}

	}



	private void handleMove(ArrayList<StateOperatorPair> nextStates, int x, int y,String operator) {
		State nextState=this.clone();
		if(isValid(x, y)) {
			if(!containsAgent(x, y)) {
				nextState.neo.x=x;
				nextState.neo.y=y;

				nextState.oneTimeStep();
				nextStates.add(new StateOperatorPair(nextState, operator));
			}
		}else {
			//			nextState.oneTimeStep();
			//			nextStates.add(new StateOperatorPair(nextState, operator));

		}
	}


	private boolean containsAgent(int x, int y) {
		for(int i=0;i<hostages.size();i++) {
			if(hostages.get(i).x==x&&hostages.get(i).y==y&&(hostages.get(i).isAgent()||hostages.get(i).damage+2>=100)) {
				return true;
			}
		}
		for(int i=0;i<agents.size();i++) {
			if(agents.get(i).x==x&&agents.get(i).y==y) {
				return true;
			}
		}
		return false;
	}


	private void handleKillAgent(ArrayList<StateOperatorPair> nextStates, int neoX, int neoY) {
		// handling the  case if there is a hostage that will die after killing action then i will not make this action
		for(Hostage hostage:hostages) {
			if(hostage.x==neoX&&hostage.y==neoY&&hostage.damage>=98)return;
		}
		
		
		int[]dx = new int [] {-1, 1, 0, 0};
		int[]dy = new int [] {0, 0, -1, 1};
		boolean killed = false;
		State nextState=this.clone();
		for(int j = 0; j < dx.length; j++) {
			int x = neoX + dx[j];
			int y = neoY + dy[j];

			if(isValid(x, y)) {
				//checking if we have a hostage that has transformed into agent so that we can kill it.
				for(int i=0;i<nextState.hostages.size();i++) {
					if(nextState.hostages.get(i).x==x&&nextState.hostages.get(i).y==y&&nextState.hostages.get(i).isAgent()) {
						//State nextState=this.clone();

						nextState.hostages.remove(i);
						nextState.matrix[x][y]=null;		

						nextState.killedAgents++;
						killed = true;
						//					nextState.neo.damage=Math.min(100, nextState.neo.damage+20);
						//
						//					nextState.oneTimeStep();
						//					nextStates.add(new StateOperatorPair(nextState, "kill"));
						break;
					}
				}

				//checking if we have an agent so that we can kill it.
				for(int i=0;i<nextState.agents.size();i++) {
					if(nextState.agents.get(i).x==x&&nextState.agents.get(i).y==y) {
						//State nextState=this.clone();

						nextState.agents.remove(i);
						nextState.matrix[x][y]=null;		

						nextState.killedAgents++;
						killed = true;
						//					nextState.neo.damage=Math.min(100, nextState.neo.damage+20);
						//
						//					nextState.oneTimeStep();
						//					nextStates.add(new StateOperatorPair(nextState, "kill"));
						break;
					}
				}
			}


		}

		if(killed) {
			nextState.neo.damage=Math.min(100, nextState.neo.damage+20);
			nextState.oneTimeStep();
			nextStates.add(new StateOperatorPair(nextState, "kill"));
		}
	}
	private boolean isValid(int i, int j ) {
		return i>=0&&i<matrix.length&&j>=0&&j<matrix[0].length;
	}




	private void handleUsePad(ArrayList<StateOperatorPair> nextStates, int neoX, int neoY) {
		for(int i=0;i<pads.size();i++) {
			if(pads.get(i).x==neoX&&pads.get(i).y==neoY) {
				Pad pad=pads.get(i);
				for(Pad nextPad:pad.destinations) {
					State nextState=this.clone();
					nextState.neo.x=nextPad.x;		
					nextState.neo.y=nextPad.y;
					nextState.oneTimeStep();
					nextStates.add(new StateOperatorPair(nextState,"fly"));
				}
			}
		}
	}





	private void handleTakePill(ArrayList<StateOperatorPair> nextStates, int neoX, int neoY) {
		State nextState=this.clone();

		int pillEnhancement=20;
		
		for(int i=0;i<nextState.pills.size();i++) {
			Pill pill=nextState.pills.get(i);
			if(pill.x==neoX &&pill.y==neoY) {
				// decreasing the damage of all alive hostages by 20
				for(Hostage hostage:nextState.hostages) {
					if(!hostage.isAgent()) {
						hostage.damage=Math.max(0, hostage.damage-pillEnhancement);
					}
				}

				// decreasing the damage of all alive carried hostages by 20
				for(Hostage hostage:nextState.carriedHostages) {
					if(!hostage.isAgent()) {
						hostage.damage=Math.max(0, hostage.damage-pillEnhancement);
					}
				}

				// decreasing the damage of neo
				nextState.neo.damage=Math.max(0,nextState.neo.damage-pillEnhancement);


				nextState.pills.remove(i);
				nextState.matrix[neoX][neoY]=null;

				//nextState.oneTimeStep();// When neo takes pill, we don't increase damage of all hostages. 
				nextStates.add(new StateOperatorPair(nextState, "takePill"));

			}
		}
	}





	private void handleDropHostages(ArrayList<StateOperatorPair> nextStates) {
		if(carriedHostages.size()>0) {

			State nextState=this.clone();
			nextState.telephoneBoothHostages.addAll(nextState.carriedHostages);

			nextState.carriedHostages.clear();
			nextState.oneTimeStep();
			nextStates.add(new StateOperatorPair(nextState, "drop"));
		}
	}





	private void handleCarryHostage(ArrayList<StateOperatorPair> nextStates, int neoX, int neoY) {
		if(c-carriedHostages.size()>0) {// check if Neo can still carry more hostages
			State nextState=this.clone();
			for(int i=0;i< nextState.hostages.size();i++) {
				Hostage hostage=nextState.hostages.get(i);
				if(hostage.x==neoX&&hostage.y==neoY&&!hostage.isAgent()) {// found at the current cell an alive hostage
					nextState.carriedHostages.add(hostage);
					nextState.hostages.remove(i);
					
					nextState.matrix[neoX][neoY]=null;// nullify the current cell since now I carried that hostage
					nextState.oneTimeStep();
					nextStates.add(new StateOperatorPair(nextState, "carry"));

					break;
				}

			}

		}
	}


	public void oneTimeStep() {
		for(Hostage hostage:hostages) {
			if(hostage.damage<100&&hostage.damage+2>=100) {
				hostagesTransformed++;
			}
			hostage.damage=Math.min(100,hostage.damage+2);
		}

		for(Hostage hostage:carriedHostages) {
			if(hostage.damage<100&&hostage.damage+2>=100) {
				hostagesTransformed++;
			}
			hostage.damage=Math.min(100,hostage.damage+2);
		}
	}

	public int h1() {
		int numberOfHostagesToBeKilled=0;
		for(Hostage hostage:hostages) {
			if(hostage.isAgent())numberOfHostagesToBeKilled++;
		}
		return numberOfHostagesToBeKilled;
	}

	static int[][] mat;// matrix containing the shortest path between (i,j) and (k,l) in mat[i*m+j][k*m+l] where m is the number of cols
	static public boolean computed=false;// computed is true if mat is already calculated
	static int INF=(int)1e8;
	public static int getIndex(int i, int j ,int m) {
		return i*m+j;
	}

	public void floydWarshal() {
		int n=matrix.length;
		int m=matrix[0].length;

		mat=new int[n*m][n*m];

		for(int i=0;i<mat.length;i++) {
			Arrays.fill(mat[i], INF);
		}

		for(Pad pad:pads) {
			for(Pad dest:pad.destinations) {
				int srcIdx=getIndex(pad.x, pad.y, m);
				int destIdx=getIndex(dest.x, dest.y, m);
				mat[srcIdx][destIdx]=mat[destIdx][srcIdx]=1;
			}
		}
		int dx[]=new int[] {1,-1,0,0};
		int dy[]=new int[] {0,0,1,-1};

		for(int x=0;x<n;x++) {
			for(int y=0;y<m;y++) {
				for(int i=0;i<dx.length;i++) {
					int newX=x+dx[i],newY=y+dy[i];
					if(isValid(newX, newY)) {
						int srcIdx=getIndex(x,y,m);
						int destIdx=getIndex(newX,newY,m);
						mat[srcIdx][destIdx]=mat[destIdx][srcIdx]=1;
					}
				}
			}
		}

		//floyd warshal part
		for(int i=0;i<mat.length;i++) {
			for(int j=0;j<mat.length;j++) {
				for(int mid=0;mid<mat.length;mid++) {
					if(mat[i][j]>mat[i][mid]+mat[mid][j])
						mat[i][j]=mat[i][mid]+mat[mid][j];
				}
			}
		}

		computed=true;

	}
	public int h2() {
		if(!computed) {
			floydWarshal();
		}
		int m=matrix[0].length;//number of columns
		int cntPills=pills.size();
		int cntDeadHostage=0;
		for(Hostage hostage:hostages) {
			if(hostage.isAgent())continue;
			int damage=hostage.damage-20*cntPills;
			int neoIdx=getIndex(neo.x, neo.y, m);
			int hostageIdx=getIndex(hostage.x,hostage.y,m);
			int telephoneIndex=getIndex(telephoneBooth.x, telephoneBooth.y, m);

			int totalDistance=mat[neoIdx][hostageIdx]+mat[hostageIdx][telephoneIndex]+1;// this +1 is to count for the carry action

			if(damage+totalDistance*2>=100)
				cntDeadHostage++;
			
		}



		
		return cntDeadHostage;

	}


	public String visualize() {
		return Utils.visualize(matrix, neo, carriedHostages, telephoneBoothHostages);
	}

	public Neo getNeo() {
		return neo;
	}





	public int getC() {
		return c;
	}





	public ArrayList<Hostage> getHostages() {
		return hostages;
	}





	public ArrayList<Hostage> getCarriedHostages() {
		return carriedHostages;
	}





	public int getHostagesTransformed() {
		return hostagesTransformed;
	}





	public int getKilledAgents() {
		return killedAgents;
	}





	public ArrayList<Agent> getAgents() {
		return agents;
	}





	public ArrayList<Pill> getPills() {
		return pills;
	}





	public ArrayList<Pad> getPads() {
		return pads;
	}





	public TelephoneBooth getTelephoneBooth() {
		return telephoneBooth;
	}





	public Cell[][] getMatrix() {
		return matrix;
	}






	public ArrayList<Hostage> getTelephoneBoothHostages() {
		return telephoneBoothHostages;
	}





	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agents == null) ? 0 : agents.hashCode());
		result = prime * result + c;
		result = prime * result + ((carriedHostages == null) ? 0 : carriedHostages.hashCode());
		result = prime * result + ((hostages == null) ? 0 : hostages.hashCode());
		result = prime * result + hostagesTransformed;
		result = prime * result + killedAgents;
		result = prime * result + ((neo == null) ? 0 : neo.hashCode());
		result = prime * result + ((pads == null) ? 0 : pads.hashCode());
		result = prime * result + ((pills == null) ? 0 : pills.hashCode());
		result = prime * result + ((telephoneBooth == null) ? 0 : telephoneBooth.hashCode());
		result = prime * result + ((telephoneBoothHostages == null) ? 0 : telephoneBoothHostages.hashCode());
		return result;
	}





	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (agents == null) {
			if (other.agents != null)
				return false;
		} else if (!agents.equals(other.agents))
			return false;
		if (c != other.c)
			return false;
		if (carriedHostages == null) {
			if (other.carriedHostages != null)
				return false;
		} else if (!carriedHostages.equals(other.carriedHostages))
			return false;
		if (hostages == null) {
			if (other.hostages != null)
				return false;
		} else if (!hostages.equals(other.hostages))
			return false;
		if (hostagesTransformed != other.hostagesTransformed)
			return false;
		if (killedAgents != other.killedAgents)
			return false;
		if (neo == null) {
			if (other.neo != null)
				return false;
		} else if (!neo.equals(other.neo))
			return false;
		if (pads == null) {
			if (other.pads != null)
				return false;
		} else if (!pads.equals(other.pads))
			return false;
		if (pills == null) {
			if (other.pills != null)
				return false;
		} else if (!pills.equals(other.pills))
			return false;
		if (telephoneBooth == null) {
			if (other.telephoneBooth != null)
				return false;
		} else if (!telephoneBooth.equals(other.telephoneBooth))
			return false;
		if (telephoneBoothHostages == null) {
			if (other.telephoneBoothHostages != null)
				return false;
		} else if (!telephoneBoothHostages.equals(other.telephoneBoothHostages))
			return false;
		return true;
	}



}
