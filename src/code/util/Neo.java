package code.util;

public class Neo  implements CellContent{
int x,y;
int damage;


public Neo(int x, int y) {
	super();
	this.x = x;
	this.y = y;
	this.damage=0;
}


@Override
public String toString() {
	return "Neo [x=" + x + ", y=" + y + ", damage=" + damage + "]";
}


@Override
public String visualize() {
	// TODO Auto-generated method stub
	return "Neo ("+damage+")";
}

}
