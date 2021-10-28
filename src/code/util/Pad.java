package code.util;

import java.util.ArrayList;

public class Pad implements CellContent{
int x,y;
ArrayList<Pad> destinations;

public Pad(int x, int y) {
	super();
	this.x = x;
	this.y = y;
	destinations=new ArrayList<>();
}
public void addDestination(Pad p) {
destinations.add(p);	
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
	Pad other = (Pad) obj;
	if (x != other.x)
		return false;
	if (y != other.y)
		return false;
	return true;
}
@Override
public String toString() {
	String s= "Pad [x=" + x + ", y=" + y + ", destinations=["  ;
	for(Pad p:destinations)
		s+="("+p.x+", "+p.y+"), ";
	s+="]";
	return s;
}
@Override
public String visualize() {
	// TODO Auto-generated method stub
	String s= "Pad[";
	for(Pad pad:destinations)
		s+="("+pad.x+", "+pad.y+"), ";
	s+="]";
	return s;
}




}
