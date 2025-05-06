package gridPorject;

import gridsim.Gridlet;

public class MyGridLetResult extends Gridlet {
	int[] position = new int[2];
	float result;
	String error = "";
	public MyGridLetResult(
			int id ,
			int length,
			int input,
			int out,
			int positionX ,
			int positionY,
			float result,
			String error) {
		super(id,length,input,out);
		this.position[0] = positionX;
		this.position[1] = positionY;
		this.result = result;
		this.error = error;
	}
	public float getResult() {
		return result;
	}
	
}
