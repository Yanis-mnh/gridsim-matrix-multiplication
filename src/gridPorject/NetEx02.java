package gridPorject;

/*
 * Author: Anthony Sulistio
 * Date: November 2004
 * Description: A simple program to demonstrate of how to use GridSim
 *              network extension package.
 *              This example shows how to create user and resource
 *              entities connected via a network topology, using link
 *              and router.
 *
 */

import gridsim.*;
import gridsim.net.*;
import java.util.*;


/**
 * Test Driver class for this example
 */
public class NetEx02
{
    /**
     * Creates main() to run this example
     */
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("(1) pour cree une matrice\n(2) use example\n(3)random matrice :");
        int choose = scanner.nextInt();
        System.out.println(choose);
    	Matrice matriceA = new Matrice();
    	Matrice matriceB = new Matrice(); 
        if (choose == 1) {
        	matriceA = new Matrice("A");
        	matriceB = new Matrice("B"); 
        	while(!matriceA.canMultiply(matriceB)){
        		System.err.println("nbr de ligne de la matrice A doit etre egale a nbr de column de a la matrice B");
        		matriceA = new Matrice("A");
            	matriceB = new Matrice("B");
        	}
        }
        else if(choose == 2 ){
            ArrayList<ArrayList<Float>> dataA = new ArrayList<>();
            ArrayList<Float> rowA1 = new ArrayList<>();
            rowA1.add(1.0f);
            rowA1.add(2.0f);
            ArrayList<Float> rowA2 = new ArrayList<>();
            rowA2.add(3.0f);
            rowA2.add(4.0f);
            dataA.add(rowA1);
            dataA.add(rowA2);
            matriceA = new Matrice("A", dataA);

            ArrayList<ArrayList<Float>> dataB = new ArrayList<>();
            ArrayList<Float> rowB1 = new ArrayList<>();
            rowB1.add(5.0f);
            rowB1.add(6.0f);
            ArrayList<Float> rowB2 = new ArrayList<>();
            rowB2.add(7.0f);
            rowB2.add(8.0f);
            dataB.add(rowB1);
            dataB.add(rowB2);
            matriceB = new Matrice("B", dataB);
        }else {
        	 Random rng = new Random();

             // pick dimensions:
             // n in [3,10] 
             int n = rng.nextInt(10 - 3 + 1) + 3;
             // m in [3,10]
             int m = rng.nextInt(10 - 3 + 1) + 3;
             // p in [1,4]
             int p = rng.nextInt(4 - 1 + 1) + 1;

             System.out.printf("Building A of size %dx%d and B of size %dx%d%n", n, m, m, p);

             // fill A with random floats 0–1 (or scale to any [min,max)):
             ArrayList<ArrayList<Float>> dataA = new ArrayList<>();
             for (int i = 0; i < n; i++) {
                 ArrayList<Float> row = new ArrayList<>();
                 for (int j = 0; j <m; j++) {
                     row.add(rng.nextFloat(10 - 3 + 1) + 3);
                 }
                 dataA.add(row);
             }
             matriceA = new Matrice("A", dataA);

             // fill B of size m x p
             ArrayList<ArrayList<Float>> dataB = new ArrayList<>();
             for (int i = 0; i < m; i++) {
                 ArrayList<Float> row = new ArrayList<>();
                 for (int j = 0; j < p; j++) {
                     row.add(rng.nextFloat(10 - 3 + 1) + 3);
                 }
                 dataB.add(row);
             }
             matriceB = new Matrice("B", dataB);
         }
        
    	
    	
    	
    	
    	System.out.println(matriceA.toString());
    	System.out.println(matriceB.toString());


        try
        {
            //////////////////////////////////////////
            // First step: Initialize the GridSim package. It should be called
            // before creating any entities. We can't run this example without
            // initializing GridSim first. We will get run-time exception
            // error.
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();

            // a flag that denotes whether to trace GridSim events or not.
            boolean trace_flag = true;

            // Initialize the GridSim package
            System.out.println("Initializing GridSim package");
            GridSim.init(num_user, calendar, trace_flag);

            //////////////////////////////////////////
            // Second step: Creates one or more GridResource entities

            double baud_rate = 20000; // bits/sec
            double propDelay = 10;   // propagation delay in millisecond
            int mtu = 1500;          // max. transmission unit in byte
            int i = 0;

            // more resources can be created by
            // setting totalResource to an appropriate value
            int totalResource = 1;
            ArrayList<MyRessource> resList = new ArrayList<MyRessource>(totalResource);
            for (i = 0; i < totalResource; i++)
            {
            	
                MyRessource res =  createGridResource("Res_"+i, baud_rate,
                                                      propDelay, mtu);

                // add a resource into a list
                resList.add(res);
            }

            //////////////////////////////////////////
            // Third step: Creates one or more grid user entities

            // number of Gridlets that will be sent to the resource
            
            
            ///// do calculation to know ch7al kayen mn gridlet w (line*column)
            int totalGridlet = matriceA.getNbrLigne()*matriceB.getNbrCol();

            // create users
            ArrayList<NetUser> userList = new ArrayList<NetUser>(num_user);
            for (i = 0; i < num_user; i++)
            {
                // if trace_flag is set to "true", then this experiment will
                // create User_i.csv where i = 0 ... (num_user-1)
                NetUser user = new NetUser("User_"+i,matriceA,matriceB, totalGridlet, baud_rate,
                                           propDelay, mtu, trace_flag);

                // add a user into a list
                userList.add(user);
            }

            //////////////////////////////////////////
            // Fourth step: Builds the network topology among entities.

            // In this example, the topology is:
            // user(s) --1Mb/s-- r1 --10Mb/s-- r2 --1Mb/s-- GridResource(s)

            // create the routers.
            // If trace_flag is set to "true", then this experiment will create
            // the following files (apart from sim_trace and sim_report):
            // - router1_report.csv
            // - router2_report.csv
            Router r1 = new RIPRouter("router1", trace_flag);   // router 1
            Router r2 = new RIPRouter("router2", trace_flag);   // router 2

            // connect all user entities with r1 with 1Mb/s connection
            // For each host, specify which PacketScheduler entity to use.
            NetUser obj = null;
            for (i = 0; i < userList.size(); i++)
            {
                // A First In First Out Scheduler is being used here.
                // SCFQScheduler can be used for more fairness
                FIFOScheduler userSched = new FIFOScheduler("NetUserSched_"+i);
                obj = userList.get(i);
                r1.attachHost(obj, userSched);
            }

            // connect all resource entities with r2 with 1Mb/s connection
            // For each host, specify which PacketScheduler entity to use.
            MyRessource resObj = null;
            for (i = 0; i < resList.size(); i++)
            {
                FIFOScheduler resSched = new FIFOScheduler("GridResSched_"+i);
                resObj = resList.get(i);
                r2.attachHost(resObj, resSched);
            }

            // then connect r1 to r2 with 10Mb/s connection
            // For each host, specify which PacketScheduler entity to use.
            baud_rate = 20000;
            Link link = new SimpleLink("r1_r2_link", baud_rate, propDelay, mtu);
            FIFOScheduler r1Sched = new FIFOScheduler("r1_Sched");
            FIFOScheduler r2Sched = new FIFOScheduler("r2_Sched");

            // attach r2 to r1
            r1.attachRouter(r2, link, r1Sched, r2Sched);

            //////////////////////////////////////////
            // Fifth step: Starts the simulation
            GridSim.startGridSimulation();



            for (i = 0; i < userList.size(); i++)
            {
                obj = userList.get(i);
            }

            System.out.println("\nFinish network example ...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Unwanted errors happen");
        }
    }

    /**
     * Creates one Grid resource. A Grid resource contains one or more
     * Machines. Similarly, a Machine contains one or more PEs (Processing
     * Elements or CPUs).
     * <p>
     * In this simple example, we are simulating one Grid resource with three
     * Machines that contains one or more PEs.
     * @param name          a Grid Resource name
     * @param baud_rate     the bandwidth of this entity
     * @param delay         the propagation delay
     * @param MTU           Maximum Transmission Unit
     * @return a GridResource object
     */
    private static MyRessource createGridResource(String name,
                double baud_rate, double delay, int MTU)
    {
        System.out.println();
        System.out.println("Starting to create one Grid resource with " +
                "3 Machines");

        //to store machines
        MachineList mList = new MachineList();


        int mipsRating = 377;
        
        mList.add( new Machine(0, 4, mipsRating));   // First Machine
        mList.add( new Machine(1, 4, mipsRating));   // Second Machine
        mList.add( new Machine(2, 2, mipsRating));   // Third Machine

        // 4. Create a ResourceCharacteristics object that stores the

        String arch = "Sun Ultra";      
        String os = "Solaris";          
        double time_zone = 9.0;         
        double cost = 3.0;              

        ResourceCharacteristics resConfig = new ResourceCharacteristics(
                arch, os, mList, ResourceCharacteristics.TIME_SHARED,
                time_zone, cost);

      

        // 5. Finally, we need to create a GridResource object.
        long seed = 11L*13*17*19*23+1;
        double peakLoad = 0.0;        // the resource load during peak hour
        double offPeakLoad = 0.0;     // the resource load during off-peak hr
        double holidayLoad = 0.0;     // the resource load during holiday

        // incorporates weekends so the grid resource is on 7 days a week
        LinkedList<Integer> Weekends = new LinkedList<Integer>();
        Weekends.add(new Integer(Calendar.SATURDAY));
        Weekends.add(new Integer(Calendar.SUNDAY));

        // incorporates holidays. However, no holidays are set in this example
        LinkedList<Integer> Holidays = new LinkedList<>();
        MyRessource gridRes = null;
        try
        {
            // creates a GridResource with a link

            gridRes = new MyRessource(name,
                new SimpleLink(name + "_link", baud_rate, delay, MTU),
                seed, resConfig, peakLoad, offPeakLoad, holidayLoad,
                Weekends, Holidays);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Finally, creates one Grid resource (name: " + name +
                " - id: " + gridRes.get_id() + ")");
        System.out.println();

        return gridRes;
    }

    /**
     * Prints the Gridlet objects
     */
  

} // end class

