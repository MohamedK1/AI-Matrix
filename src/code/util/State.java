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


	public ArrayList<StateOperatorPair> expand(){
		ArrayList<StateOperatorPair> nextStates=new ArrayList<>();
		if(neo.damage==100)return nextStates;

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

		if(curCell!=null&&curCell.cellContent instanceof Pad) {
			handleUsePad(nextStates, neoX, neoY);
		}


		handleKillAgent(nextStates, neoX+1, neoY);
		handleKillAgent(nextStates, neoX-1, neoY);
		handleKillAgent(nextStates, neoX, neoY+1);
		handleKillAgent(nextStates, neoX, neoY-1);


		handleMove(nextStates, neoX+1, neoY,"down");
		handleMove(nextStates, neoX-1, neoY,"up");
		handleMove(nextStates, neoX, neoY+1,"right");
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
			nextState.oneTimeStep();
			nextStates.add(new StateOperatorPair(nextState, operator));

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


	private void handleKillAgent(ArrayList<StateOperatorPair> nextStates, int x, int y) {
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
					nextStates.add(new StateOperatorPair(nextState, "kill"));
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
					nextStates.add(new StateOperatorPair(nextState, "kill"));
					break;
				}
			}


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
				nextStates.add(new StateOperatorPair(nextState, "takePill"));
			}
		}
	}





	private void handleDropHostages(ArrayList<StateOperatorPair> nextStates) {
		if(carriedHostages.size()>0) {
			State nextState=this.clone();
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
		return true;
	}



}
