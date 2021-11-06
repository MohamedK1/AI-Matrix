package code.util;

import java.util.ArrayList;

import code.GenericState;

public class State implements GenericState {

 Neo neo;
 int c;

 ArrayList<Hostage> hostages;
 ArrayList<Hostage> carriedHostages;
 
 ArrayList<Agent> agents;
 ArrayList<Pill> pills;
 ArrayList<Pad> pads;
 TelephoneBooth telephoneBooth;
 Cell[][] matrix;	
 




public State(Neo neo, int c, ArrayList<Hostage> hostages, ArrayList<Hostage> carriedHostages, ArrayList<Agent> agents,
		ArrayList<Pill> pills, ArrayList<Pad> pads, TelephoneBooth telephoneBooth, Cell[][] matrix) {
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
	 
	 return new State(neo, c, hostages, carriedHostages, agents, pills, pads, telephoneBooth, matrix);
	 
}


	
	
}
