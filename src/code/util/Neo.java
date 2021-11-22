package code.util;

public class Neo  implements CellContent{
public int x,y;
public int damage;


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

@Override
public Neo clone(){
	Neo neo=new Neo(x, y);
	neo.damage=damage;
	return neo;
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
	Neo other = (Neo) obj;
	if (damage != other.damage)
		return false;
	if (x != other.x)
		return false;
	if (y != other.y)
		return false;
	return true;
}



}
