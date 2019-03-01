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
        Cluster Grid2 = new Cluster("/home/dhuant/NetBeansProjects/2DMapping/maps/reagan_maps/");
        Route = Grid2.routeAstar(Grid2.getStartNode("COUNSELOR"), Grid2.getDestNode("LIBRARY"));
        
        Grid2.drawRoute(Route);
    }
    
}
