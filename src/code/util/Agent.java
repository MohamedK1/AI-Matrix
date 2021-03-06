package code.util;


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
	
	
	

		
}
