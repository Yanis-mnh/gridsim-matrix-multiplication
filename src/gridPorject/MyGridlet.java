package gridPorject;

import java.util.ArrayList;

import gridsim.Gridlet;

public class MyGridlet extends Gridlet {
	private ArrayList<Float> ligne;
	private ArrayList<Float> column;
	private double result;
	public MyGridlet(
			int id ,
			int length,
			int input,
			int out ,
			ArrayList<Float> ligne,
			ArrayList<Float> column
			) {
		super(id,length,input,out);
		this.ligne = ligne;
		this.column = column;
		
	}
	

	public void setResult(double result2) {
		this.result =result2; 
	}
	public double getResult() {
		return this.result;
	}
	
	public ArrayList<Float> get_ligne() {
		return this.ligne;
	}
	public ArrayList<Float> get_column() {
		return this.column;
	}
	
}
