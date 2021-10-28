package code.util;

import java.util.ArrayList;

public class State {

 Neo neo;
 int c;

 ArrayList<Hostage> hostages;
 ArrayList<Hostage> carriedHostages;
 
 ArrayList<Agent> agents;
 Cell[][] matrix;	
 
public State(Neo neo, int c, ArrayList<Hostage> hostages, ArrayList<Hostage> carriedHostages, ArrayList<Agent> agents,
		Cell[][] matrix) {
	super();
	this.neo = neo;
	this.c = c;
	this.hostages = hostages;
	this.carriedHostages = carriedHostages;
	this.agents = agents;
	this.matrix = matrix;
}
	
	
}
