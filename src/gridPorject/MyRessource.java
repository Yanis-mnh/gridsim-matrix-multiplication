package gridPorject;

import java.util.ArrayList;
import java.util.LinkedList;

import eduni.simjava.Sim_event;
import eduni.simjava.Sim_system;
import gridsim.*;
import gridsim.net.SimpleLink;

public class MyRessource extends GridResource {
	public static final int MY_GRIDLET_TAG = 30000;
	static int id = 0;
    public MyRessource(String name, double baudRate, ResourceCharacteristics resChar, ResourceCalendar calendar) throws Exception {
        super(name, baudRate, resChar, calendar);
    }

    public MyRessource(String name, SimpleLink link, long seed, ResourceCharacteristics resChar,
                       double peakLoad, double offPeakLoad, double holidayLoad,
                       LinkedList<?> weekends, LinkedList<?> holidays) throws Exception {
        super(name, link, seed, resChar, peakLoad, offPeakLoad, holidayLoad, weekends, holidays);
    }

	
	public void body() {
	    super.body();  // Très important : appelle le corps de GridResource

	    System.out.printf("[%.2fs] [RES: %s] Fin d'exécution.\n",
	            GridSim.clock(), get_name());

	}


	private void processGridlet(MyGridlet gridlet) {
		System.out.println("=============================================");
	    ArrayList<Float> row = gridlet.get_ligne();
	    ArrayList<Float> col = gridlet.get_column();

	    double result = 0.0;

	    if (row.size() != col.size()) {
	        System.out.println("ERROR: Row and column vectors are not the same size.");
	        return; 
	    }
	    for (int i = 0; i < row.size(); i++) {
	        result += row.get(i) * col.get(i);
	    }

	    System.out.printf("[%.2fs] [RES: %s] Calculé Gridlet #%d → %.2f\n",
	            GridSim.clock(), super.get_name(), gridlet.getGridletID(), result);

	    // Store the result in a MyGridlet object
	    gridlet.setResult(result);

	    // Send the result back to the user
	    IO_data data = new IO_data(gridlet, gridlet.getGridletFileSize(), gridlet.getUserID());
	    super.send(super.output, GridSimTags.SCHEDULE_NOW, GridSimTags.GRIDLET_RETURN, data);
	    System.out.println("=============================================");
	}

	

	@Override
	protected void processOtherEvent(Sim_event ev) {
		int tag = ev.get_tag();

		if (tag == MY_GRIDLET_TAG) {
			MyGridlet gridlet = (MyGridlet) ev.get_data();
			processGridlet(gridlet);
		} else {
			System.err.printf("  Tag inconnu reçu : [%s]\n", get_name());
		}
	}

} // end class
