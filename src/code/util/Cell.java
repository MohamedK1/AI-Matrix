package code.util;

public class Cell<T extends CellContent> {
	T cellContent;
	
	public Cell(T cellContent) {
		super();
		this.cellContent = cellContent;
	}
	public String visualize() {
		return cellContent.visualize();
	}
	
	
}
