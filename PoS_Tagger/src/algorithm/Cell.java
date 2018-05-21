package algorithm;

public class Cell {
	
	public Double p;
	public int father;
	
	public Cell(Double pr,int fr){
		p=pr;
		father=fr;
	}
	
	public String toString(){
		return ""+p+"-f "+father;
	}

}
