package code.util;


public class Hostage  implements CellContent{
	int x,y;
	int damage;
	public void decrease() {
		damage=Math.max(0, damage-2);
	}
	// make sure he is not in the booth
	public boolean isAgent() {
		return damage==0;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + damage;
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
		Hostage other = (Hostage) obj;
		if (damage != other.damage)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	public Hostage(int x, int y, int damage) {
		super();
		this.x = x;
		this.y = y;
		this.damage = damage;
	}
	@Override
	public String toString() {
		return "Hostage [x=" + x + ", y=" + y + ", damage=" + damage + "]";
	}
	@Override
	public String visualize() {
		// TODO Auto-generated method stub
		return "H ("+damage+")";
	}
	@Override
	public Hostage clone(){
		
		return new Hostage(x, y, damage);
	}
	
	
	
	
	
}
