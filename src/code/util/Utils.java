package code.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import java.util.StringTokenizer;

import java.lang.*;
import java.io.*;

public class Utils {
	
//	"5,5;4;1,1;4,1;2,4,0,4,3,2,3,0,4,2,0,1,1,3,2,1;4,0,4,4,1,0;2,0,0,2,0,2,2,0,2;0,0,62,4,3,45,3,3,39,2,3,40";
	
	/*
M,N; C; NeoX,NeoY; TelephoneX,TelehoneY;
AgentX1,AgentY1, ...,AgentXk,AgentYk;
PillX1,PillY1, ...,PillXg,PillYg;
StartPadX1,StartPadY1,FinishPadX1,FinishPadY1,...,
StartPadXl,StartPadYl,FinishPadXl,FinishPadYl;
HostageX1,HostageY1,HostageDamage1, ...,HostageXw,HostageYw,HostageDamag
	 */
	public static State parse(String s) {

		StringTokenizer st=new StringTokenizer(s,";");
		String []arr = s.split(";");
//		System.out.println(Arrays.toString(arr));
		int idx = 0;
		//  M,N size of grid
		StringTokenizer internalSt=new StringTokenizer(arr[idx++],",");
		int n=Integer.parseInt(internalSt.nextToken());
		int m=Integer.parseInt(internalSt.nextToken());
		
		
		
		// c
		internalSt=new StringTokenizer(arr[idx++],",");
		int c=Integer.parseInt(internalSt.nextToken());

		//Neo location
		internalSt=new StringTokenizer(arr[idx++],",");
		int neoX=Integer.parseInt(internalSt.nextToken());
		int neoY=Integer.parseInt(internalSt.nextToken());
		Neo neo=new Neo(neoX, neoY);


		//Telephone booth location
		internalSt=new StringTokenizer(arr[idx++],",");
		int teleX=Integer.parseInt(internalSt.nextToken());
		int teleY=Integer.parseInt(internalSt.nextToken());
		TelephoneBooth tele=new TelephoneBooth(teleX, teleY);


		//Agent locations
		ArrayList<Agent> agentList=new ArrayList();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		while(internalSt.hasMoreElements()) {
			int agentX=Integer.parseInt(internalSt.nextToken());
			int agentY=Integer.parseInt(internalSt.nextToken());
			Agent agent=new Agent(agentX,agentY);
			agentList.add(agent);
		}
		}
		idx++;

		//pills location 
		ArrayList<Pill> pillList=new ArrayList();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		while(internalSt.hasMoreElements()) {
			String x=internalSt.nextToken();
			int pillX=Integer.parseInt(x);
			int pillY=Integer.parseInt(internalSt.nextToken());
			Pill pill=new Pill(pillX,pillY);
			pillList.add(pill);
		}
		}
		idx++;

		//pads location 
		ArrayList<Pad> padList=new ArrayList<>();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		HashMap<Pair,Pad> padMap=new HashMap<>();// stores reference to each already created pad to avoid recreating it.
		while(internalSt.hasMoreElements()) {
			int startX=Integer.parseInt(internalSt.nextToken());
			int startY=Integer.parseInt(internalSt.nextToken());
			Pair startLocation=new Pair(startX,startY);
			Pad start=new Pad(startX,startY);
			if(padMap.get(startLocation)!=null)
			{
				start=padMap.get(startLocation);
			}else {
				padMap.put(startLocation, start);
			}
			
			
			int finishX=Integer.parseInt(internalSt.nextToken());
			int finishY=Integer.parseInt(internalSt.nextToken());
			Pair finishLocation=new Pair(finishX,finishY);
			Pad finish=new Pad(finishX,finishY);
			if(padMap.get(finishLocation)!=null)
			{	
				finish=padMap.get(finishLocation);
			}else {
				padMap.put(finishLocation, finish);
			}
			
			start.addDestination(finish);
			
		}

		for(Entry<Pair,Pad> e:padMap.entrySet() ) {
			padList.add(e.getValue());
		}
		}
		idx++;
		
		//hostage location
		
		ArrayList<Hostage> hostageList=new ArrayList();
		if(idx<arr.length&&!arr[idx].equals("")) {
		internalSt=new StringTokenizer(arr[idx],",");
		while(internalSt.hasMoreElements()) {
			int hostageX=Integer.parseInt(internalSt.nextToken());
			int hostageY=Integer.parseInt(internalSt.nextToken());
			int damage=Integer.parseInt(internalSt.nextToken());
			Hostage hostage=new Hostage(hostageX,hostageY,damage);
			hostageList.add(hostage);
		}
		}
		idx++;

//		System.out.println(m+" "+n);
//		System.out.println(neo);
//		System.out.println(tele);
//		System.out.println(agentList);
//		System.out.println(pillList);
//		System.out.println(padList);
//		System.out.println(hostageList);

		
		Cell[][] matrix= buildMatrix(m,n, tele, agentList, pillList, padList, hostageList);
		
//		System.out.println(visualize(matrix,neo));
		State state= new State(neo, c, hostageList, new ArrayList<Hostage>(), agentList, pillList,padList, tele, matrix,0,0,new ArrayList<>());
		return state;
	}

	public static Cell[][]  buildMatrix(int m, int n, TelephoneBooth tele, ArrayList<Agent> agentList,
			ArrayList<Pill> pillList, ArrayList<Pad> padList, ArrayList<Hostage> hostageList) {
		//filling the matrix object
		Cell[][] matrix=new Cell[m][n];

		for(Hostage hostage: hostageList) {
			matrix[hostage.x][hostage.y]=new Cell<Hostage>(hostage);
		}
		
		for(Agent agent: agentList) {
			matrix[agent.x][agent.y]=new Cell<Agent>(agent);
		}
		for(Pill pill: pillList) {
			matrix[pill.x][pill.y]=new Cell<Pill>(pill);
		}
		
		for(Pad pad: padList) {
			matrix[pad.x][pad.y]=new Cell<Pad>(pad);
		}
		
		matrix[tele.x][tele.y]=new Cell<TelephoneBooth>(tele);
		
		return matrix;
	}

	public static String visualize(Cell[][] matrix,Neo neo, ArrayList<Hostage>carriedHostages, ArrayList<Hostage>telephoneBoothHostages) {
//		String s="";
		int max=0;
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix[i].length;j++) {
				String s="";
				if(neo.x==i&&neo.y==j) {
					s+=neo.visualize()+", ";
				}
				if(matrix[i][j]==null)
					s+="  ";
				else {
					s+=matrix[i][j].visualize();
				}
				s=s.trim();
				if(s.length()>0&&s.charAt(s.length()-1)==',')
					s=s.substring(0,s.length()-1);
				
				max=Math.max(max, s.length());
				
				
				if(j<matrix[i].length-1)s+="|";
			}
			
		}
		String out="";
		max+=4;// extra 2 spaces at left and right
		for(int i=0;i<matrix.length;i++) {
			String row="";
			for(int j=0;j<matrix[i].length;j++) {
				String s="|";
				if(neo.x==i&&neo.y==j) {
					s+=neo.visualize()+", ";
				}
				if(matrix[i][j]==null)
					s+="  ";
				else {
					s+=matrix[i][j].visualize();
				}
				s=s.trim();
				if(s.length()>0&&s.charAt(s.length()-1)==',')
					s=s.substring(0,s.length()-1);
				
				row+=String.format("%-"+max+"s", s);
				
				if(j<matrix[i].length)row+="|";
			}
			
			
			
			out+=row+"\n"+row.replaceAll(".", "-")+"\n";
			
		}
		out+="Carried hostages: [";
		for(Hostage hostage: carriedHostages) {
			out+=hostage.visualize()+", ";
		}
		out+="]\n";
		
		out+="Telephone booth hostages: [";
		for(Hostage hostage: telephoneBoothHostages) {
			out+=hostage.visualize()+", ";
		}
		out+="]\n";
		
		
		return out;
	}
	
	public static <T extends CellContent> ArrayList<T> cloneList(ArrayList<T> list){
	ArrayList<T> l=new ArrayList<T>(list.stream().map((val)->(T)val.clone()).collect(Collectors.toList()));
	
	return l;
}
	
	
	public static class Pair{
		int x,y;

		public Pair(int x, int y) {
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
			Pair other = (Pair) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		
	}
	

public static void main(String[] args) {
		State s=parse("5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80");
		System.out.println(visualize(s.matrix, s.neo,s.carriedHostages,s.telephoneBoothHostages));
	}

}
