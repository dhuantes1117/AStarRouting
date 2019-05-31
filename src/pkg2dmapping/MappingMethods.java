/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2dmapping;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dhuant
 */
public class MappingMethods {
    //first figure how to represent school as nodes
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        UnitTest Nevada = new UnitTest();
        if(Nevada.runAllTests()){
            return;
        }
        ArrayList<Node> Route = new ArrayList<Node>();
        LayeredCluster School = new LayeredCluster();
        
        
        Cluster DownstairsMain = new Cluster("maps/writeOnlyMaps/reaganFloorPlanDM.png", 
                "classLoc/downstairsMCL.txt", 
                "nodeHashes/nodeHashDM.txt", 
                "maps/reagan_maps/downstairsM.png");
        DownstairsMain.setName("DM");
        DownstairsMain.getNode("TNLA (0,11)").setRoomName("Dlink");
        DownstairsMain.getNode("TNLA (0,11)").setJump(true);
        
//        System.out.println("size " + DownstairsMain.size());
//        
//        DownstairsMain.print(DownstairsMain.routeAstar(DownstairsMain.getNode("FL110"), DownstairsMain.getNode("HI126")));
//        
//        Cluster DownstairsNew = new Cluster("maps/writeOnlyMaps/reaganFloorPlanDN.png", 
//                "classLoc/downstairsNCL.txt", 
//                "nodeHashes/nodeHashDN.txt", 
//                "maps/reagan_maps/downstairsN.png");
//        DownstairsNew.setName("DN");
//        DownstairsNew.getNode("TNLA (25,8)").setRoomName("Dlink");
//        DownstairsNew.getNode("TNLA (25,8)").setJump(true);
//        
//        
//        School.add(DownstairsNew);
//        School.add(DownstairsMain);//Dlink
        
        Route = DownstairsMain.routeAstar(DownstairsMain.getNode(args[0]), DownstairsMain.getNode(args[1]));
        
        //Route = School.Route(DownstairsMain.getNode("NM155"), DownstairsNew.getNode("MS301"));//NM155, MS301
        
        //DownstairsMain.print(Route);
        
        DownstairsMain.writeRoute(Route);
    }
    
}
