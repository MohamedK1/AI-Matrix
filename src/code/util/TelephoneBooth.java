package code.util;

public class TelephoneBooth implements CellContent {
	public int x,y;

	public TelephoneBooth(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

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
		TelephoneBooth other = (TelephoneBooth) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TelephoneBooth [x=" + x + ", y=" + y + "]";
	}

	@Override
	public String visualize() {
		// TODO Auto-generated method stub
		return "TB";
	}
	
	@Override
	public TelephoneBooth clone(){
		return new TelephoneBooth(x, y);
	}
	
	
}
