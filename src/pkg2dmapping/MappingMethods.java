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
        /*
        Remember: While the school will be hardcoded, the destination will change
        using the constructor to set up a destination is only for
        
        Code Pragmatically a way to break ties without writing the actual meat
        and potatoes of the method. Once the noded map of reagan is available
        it will become what types of ties are possible and impossible, including
        ones A* will prevent. While necessary for error checking of a more
        moddable program, extensive tiebreaking maybe not even an issue
        */
        /*File writableTextFile = new File("writeTo.txt");
        BufferedWriter Writer = new BufferedWriter(new FileWriter(writableTextFile));
        */
        Cluster Map = generateMap();
        
        ArrayList<Node> Route = new ArrayList<Node>();
        Cluster Grid = generateMap();
        Route = Grid.routeAstar(Grid.get(0), Grid.get(9));
        System.out.println(Grid.routeString(Route));
        Route = Grid.routeAstar(Grid.closest(0, 0), Grid.closest(12, 3));
        System.out.println(Grid.routeString(Route));
        Cluster Grid2 = new Cluster("/home/dhuant/NetBeansProjects/Ruby/2DMapping/maps");
        System.out.println(Grid2.getStart().x() + ", " + Grid2.getStart().y());
        System.out.println(Grid2.getDest().x() + ", " + Grid2.getDest().y());
        Route = Grid2.routeAstar(Grid2.getStart(), Grid2.getDest());
        System.out.println(Grid2.routeString(Route));
    }
    //recurTHIS(Node curr, ArrayList<Node>/Set<Node> Stack, Node destination)
    //Keep multiple stacks open for true A*, abstraction, I do not believe it is needed
    //for the initial constaints of the nodes upon the school
    //Traveled to and non traveled to
    //Integer max is locked in
    
    public static Cluster generateMap(){
        //Create Nodes
        //^^Choose one to be origin, remember for later
        //Create AList Edges
        //setManhattan distance for each
        //Set Neighbors
        /*Node One = new Node("Name", int x, int y);
        *ArrayList<Edge> OneEdges = new ArrayList<Edge>();
        *Add Edges to listAe
        *One.setNeighbors(OneEdges);
        *Create Cluster and add One
        */
        Node A = new Node("A", 0, 0, true);
        Node B = new Node("B", 1, 0);
        Node C = new Node("C", 0, 1);
        Node D = new Node("D", 1, 1);
        Node E = new Node("E", 2, 1); //Destination
        Node F = new Node("F", 2, 0);
        Node G = new Node("G", 6, 4);
        Node H = new Node("H", 6, 1);
        Node I = new Node("I", 8, 4);
        Node J = new Node("J", 8, 0, true);
        Node K = new Node("K", 7, 1);
        
        Cluster Map = new Cluster(new ArrayList<Node>(Arrays.asList(A, B, C, D, E, F, G, H, I, J, K)));
        
        for (int i = 0; i < Map.size(); i++) {
            Map.get(i).seth(E);
        }
        
        Cluster retable = new Cluster(Map);
        
        retable.connect(A, C);
        retable.connect(B, D);
        retable.connect(B, F);
        retable.connect(C, D);
        retable.connect(D, E);
        retable.connect(E, F);
        retable.connect(E, G);
        retable.connect(E, H);
        retable.connect(G, H);
        retable.connect(G, I);
        retable.connect(H, K);
        retable.connect(I, J);
        
        return retable;
        
    }
}
