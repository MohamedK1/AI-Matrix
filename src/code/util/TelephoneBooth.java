package code.util;

public class TelephoneBooth implements CellContent {
	int x,y;

	public TelephoneBooth(int x, int y) {
		super();
		this.x = x;
		this.y = y;
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
	
}
