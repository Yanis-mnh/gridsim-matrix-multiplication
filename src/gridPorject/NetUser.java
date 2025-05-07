package gridPorject;

import java.lang.reflect.Array;

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

import java.util.*;
import gridsim.*;
import gridsim.net.*;
import gridsim.util.SimReport;


/**
 * This class basically creates Gridlets and submits them to a
 * particular GridResources in a network topology.
 */
class NetUser extends GridSim
{
    private int myId_;      // my entity ID
    private String name_;   // my entity name
    private GridletList list_;          // list of submitted Gridlets
    private GridletList receiveList_;   // list of received Gridlets
    private SimReport report_;  // logs every events
    private Matrice matriceA, matriceB;


    NetUser(String name,Matrice matriceA , Matrice matriceB, int totalGridlet, double baud_rate, double delay,
            int MTU, boolean trace_flag  ) throws Exception
    {
        super( name, new SimpleLink(name+"_link",baud_rate,delay, MTU) );

        this.name_ = name;
        this.receiveList_ = new GridletList();
        this.list_ = new GridletList();
        this.matriceA = matriceA;
        this.matriceB = matriceB;
        // creates a report file
        if (trace_flag == true) {
            report_ = new SimReport(name);
        }

        // Gets an ID for this entity
        this.myId_ = super.getEntityId(name);
        write("Creating a grid user entity with name = " +
              name + ", and id = " + this.myId_);

        // Creates a list of Gridlets or Tasks for this grid user
        write(name + ":Creating " + totalGridlet +" Gridlets");
        this.createGridlet(myId_, totalGridlet,matriceA,matriceB);
    }

    /**
     * The core method that handles communications among GridSim entities.
     */
    public void body()
    {
        
        super.gridSimHold(3.0);
        LinkedList<?> resList = GridSim.getGridResourceList();
        
        // initialises all the containers
        int totalResource = resList.size();
        int resourceID[] = new int[totalResource];
        String resourceName[] = new String[totalResource];

        // a loop to get all the resources available
        int i = 0;
        for (i = 0; i < totalResource; i++)
        {
            // Resource list contains list of resource IDs
            resourceID[i] = ( (Integer) resList.get(i) ).intValue();

            // get their names as well
            resourceName[i] = GridSim.getEntityName( resourceID[i] );
        }

        ////////////////////////////////////////////////
        // SUBMIT Gridlets
        // determines which GridResource to send to
        if (totalResource == 0) {
            System.err.println("Aucune ressource disponible !");
            return;
        }
        int index = myId_ % totalResource;


        // sends all the Gridlets
        MyGridlet gl = null;
        for (i = 0; i < list_.size(); i++)
        {
            gl = (MyGridlet) list_.get(i);
            write(name_ + "Sending Gridlet #" + i + " to " + resourceName[index]);
            send(resourceID[index], GridSimTags.SCHEDULE_NOW, MyRessource.MY_GRIDLET_TAG, gl);
        }

        ////////////////////////////////////////////////////////
        // RECEIVES Gridlets back

        // hold for few period - few seconds since the Gridlets length are
        // quite huge for a small bandwidth
        //super.gridSimHold(5);

        // receives the gridlet back
        MyGridlet glR;
        ArrayList<MyGridlet> listResult = new ArrayList<MyGridlet>();
        for (i = 0; i < list_.size(); i++)
        {
            glR =  (MyGridlet) super.receiveEventObject();  // gets the Gridlet
            receiveList_.add(glR);   
            listResult.add(glR);
            write(name_ + ": Receiving Gridlet #" +
                  glR.getGridletID() + " at time = " + GridSim.clock() );
            
        }
        ///////////////////////////////////////   afficher la matrice //////////////////////////////
        System.out.print("---------- Donnees de la MATRICE Resultat ---------");
        for(int k=0;k<listResult.size();k++) {
        	if(k % matriceB.getNbrCol() ==  0 ) System.out.println("");
        	System.out.print( String.format("%.2f \t",listResult.get(k).getResult()));
        	
        	
        }
        System.out.println("");



        
        // shut down I/O ports
        shutdownUserEntity();
        terminateIOEntities();

        // don't forget to close the file
        if (report_ != null) {
            report_.finalWrite();
        }

        write(this.name_ + ": sending and receiving of Gridlets" +
              " complete at " + GridSim.clock() );
    }

    /**
     * Gets a list of received Gridlets
     * @return a list of received/completed Gridlets
     */
    public GridletList getGridletList() {
        return receiveList_;
    }

    /**
     * This method will show you how to create Gridlets
     * @param userID        owner ID of a Gridlet
     * @param numGridlet    number of Gridlet to be created
     */
    private void createGridlet(int userID, int numGridlet, Matrice A , Matrice B)
    {
        int id = 0;
        for (int i = 0; i < A.getNbrLigne(); i++)
        {
        	for (int j =0;j< B.getNbrCol();j++) {
        		 // Creates a Gridlet
        		ArrayList<Float> matAL = matriceA.getLigne(i);
        		ArrayList<Float> matBC= matriceB.getColonne(j);
                MyGridlet gl = new MyGridlet(id, (matriceA.getNbrLigne()*2)-1,  matAL.size() * matBC.size(), 1, matAL, matBC);
                gl.setUserID(userID);

                // add this gridlet into a list
                this.list_.add(gl);
                id++;
        	}
           
        }
    }

    /** 
     * Prints out the given message into stdout.
     * In addition, writes it into a file.
     * @param msg   a message
     */
    private void write(String msg)
    {
        System.out.println(msg);
        if (report_ != null) {
            report_.write(msg);
        }
    }

} // end class

