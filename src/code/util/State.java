package code.util;

import java.util.ArrayList;

import code.GenericState;

public class State implements GenericState {

	Neo neo;
	int c;

	ArrayList<Hostage> hostages; // carries alived hostages or transformed hostages in case that damage=100
	ArrayList<Hostage> carriedHostages;
	int hostagesTransformed;//x
	int killedAgents;//general counter for all killed agents included those who are transformed from hostages >=x

	ArrayList<Agent> agents;
	ArrayList<Pill> pills;
	ArrayList<Pad> pads;
	TelephoneBooth telephoneBooth;
	Cell[][] matrix;	





	public State(Neo neo, int c, ArrayList<Hostage> hostages, ArrayList<Hostage> carriedHostages, ArrayList<Agent> agents,
			ArrayList<Pill> pills, ArrayList<Pad> pads, TelephoneBooth telephoneBooth, Cell[][] matrix,int hostagesTransformed,int killedAgents) {
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

		int m=matrix.length,n=matrix[0].length;

		Cell[][] matrix= Utils.buildMatrix(m, n, telephoneBooth, agents, pills, pads, hostages);

		return new State(neo, c, hostages, carriedHostages, agents, pills, pads, telephoneBooth, matrix,hostagesTransformed,killedAgents);

	}


	public ArrayList<State> expand(){
		ArrayList<State> nextStates=new ArrayList<>();

		int neoX=neo.x,neoY=neo.y;
		Cell curCell=matrix[neoX][neoY];

		if(curCell.cellContent instanceof Hostage) {// neo is with a hostage in the current cell;

			handleCarryHostage(nextStates, neoX, neoY);
		}

		if(curCell.cellContent instanceof TelephoneBooth) {
			handleDropHostages();
		}

		if(curCell.cellContent instanceof Pill) {
			handleTakePill(nextStates, neoX, neoY);
		}

		if(curCell.cellContent instanceof Pad) {
			handleUsePad(nextStates, neoX, neoY);
		}


		handleKillAgent(nextStates, neoX+1, neoY);
		handleKillAgent(nextStates, neoX-1, neoY);
		handleKillAgent(nextStates, neoX, neoY+1);
		handleKillAgent(nextStates, neoX, neoY-1);


		handleMove(nextStates, neoX+1, neoY);
		handleMove(nextStates, neoX-1, neoY);
		handleMove(nextStates, neoX, neoY+1);
		handleMove(nextStates, neoX, neoY-1);
		
		return nextStates;
	}





	private void handleMove(ArrayList<State> nextStates, int x, int y) {
		State nextState=this.clone();
		if(isValid(x, y)) {
			if(!containsAgent(x, y)) {
				nextState.neo.x=x;
				nextState.neo.y=y;
				
				nextState.oneTimeStep();
				nextStates.add(nextState);
			}
		}else {
			nextState.oneTimeStep();
			nextStates.add(nextState);

		}
	}


	private boolean containsAgent(int x, int y) {
		for(int i=0;i<hostages.size();i++) {
			if(hostages.get(i).x==x&&hostages.get(i).y==y&&hostages.get(i).isAgent()) {
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


	private void handleKillAgent(ArrayList<State> nextStates, int x, int y) {
		if(isValid(x, y)) {
			//checking if we have a hostage that has transformed into agent so that we can kill it.
			for(int i=0;i<hostages.size();i++) {
				if(hostages.get(i).x==x&&hostages.get(i).y==y&&hostages.get(i).isAgent()) {
					State nextState=this.clone();

					nextState.hostages.remove(i);
					nextState.matrix[x][y]=null;		

					nextState.killedAgents++;
					nextState.neo.damage=Math.min(100, nextState.neo.damage+20);

					nextState.oneTimeStep();
					nextStates.add(nextState);
					break;
				}
			}

			//checking if we have an agent so that we can kill it.
			for(int i=0;i<agents.size();i++) {
				if(agents.get(i).x==x&&agents.get(i).y==y) {
					State nextState=this.clone();

					nextState.agents.remove(i);
					nextState.matrix[x][y]=null;		

					nextState.killedAgents++;
					nextState.neo.damage=Math.min(100, nextState.neo.damage+20);

					nextState.oneTimeStep();
					nextStates.add(nextState);
					break;
				}
			}


		}
	}
	private boolean isValid(int i, int j ) {
		return i>=0&&i<matrix.length&&j>=0&&j<matrix[0].length;
	}






	private void handleUsePad(ArrayList<State> nextStates, int neoX, int neoY) {
		for(int i=0;i<pads.size();i++) {
			if(pads.get(i).x==neoX&&pads.get(i).y==neoY) {
				Pad pad=pads.get(i);
				for(Pad nextPad:pad.destinations) {
					State nextState=this.clone();
					nextState.neo.x=nextPad.x;		
					nextState.neo.y=nextPad.y;
					nextState.oneTimeStep();
					nextStates.add(nextState);
				}
			}
		}
	}





	private void handleTakePill(ArrayList<State> nextStates, int neoX, int neoY) {
		State nextState=this.clone();

		for(int i=0;i<nextState.pills.size();i++) {
			Pill pill=nextState.pills.get(i);
			if(pill.x==neoX &&pill.y==neoY) {
				// decreasing the damage of all alive hostages by 20
				for(Hostage hostage:nextState.hostages) {
					if(!hostage.isAgent()) {
						hostage.damage=Math.max(0, hostage.damage-20);
					}
				}

				// decreasing the damage of all alive carried hostages by 20
				for(Hostage hostage:nextState.carriedHostages) {
					if(!hostage.isAgent()) {
						hostage.damage=Math.max(0, hostage.damage-20);
					}
				}

				// decreasing the damage of neo
				nextState.neo.damage=Math.max(0,nextState.neo.damage-20);


				nextState.pills.remove(i);
				nextState.matrix[neoX][neoY]=null;

				nextState.oneTimeStep();
				nextStates.add(nextState);
			}
		}
	}





	private void handleDropHostages() {
		if(carriedHostages.size()>0) {
			State nextState=this.clone();
			nextState.carriedHostages.clear();
			nextState.oneTimeStep();
		}
	}





	private void handleCarryHostage(ArrayList<State> nextStates, int neoX, int neoY) {
		if(c-carriedHostages.size()>0) {// check if Neo can still carry more hostages
			State nextState=this.clone();
			for(int i=0;i< nextState.hostages.size();i++) {
				Hostage hostage=nextState.hostages.get(i);
				if(hostage.x==neoX&&hostage.y==neoY&&!hostage.isAgent()) {// found at the current cell an alive hostage
					nextState.carriedHostages.add(hostage);
					nextState.hostages.remove(i);

					nextState.matrix[neoX][neoY]=null;// nullify the current cell since now I carried that hostage
					nextState.oneTimeStep();
					nextStates.add(nextState);

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



}
