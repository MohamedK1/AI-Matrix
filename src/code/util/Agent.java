package code.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Agent  implements CellContent{

	int x,y;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Agent other = (Agent) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public Agent(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Agent [x=" + x + ", y=" + y + "]";
	}

	@Override
	public String visualize() {
		
		return "A";
	}

	
	@Override
	public Agent clone(){
		Agent agent=new Agent(x, y);
		return agent;
	}
	
	
	public static void main(String[] args) {
		Agent a=new Agent(3, 4);
		Agent b=a.clone();
		b.x=60;
		ArrayList<Agent> list=new ArrayList<>();
		list.add(a);
		list.add(b);
		
		ArrayList<Agent> l=Utils.cloneList(list);
		l.get(0).x=50;
		System.out.println(list);
		System.out.println(l);
	}
	
	

		
}
