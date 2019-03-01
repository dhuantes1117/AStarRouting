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
        Cluster Map = generateMap();
        
        ArrayList<Node> Route = new ArrayList<Node>();
        Cluster Grid = generateMap();
        Route = Grid.routeAstar(Grid.get(0), Grid.get(9));
        System.out.println(Grid.routeString(Route));
        Route = Grid.routeAstar(Grid.closest(0, 0), Grid.closest(12, 3));
        System.out.println(Grid.routeString(Route));
        Cluster Grid2 = new Cluster("/home/dhuant/NetBeansProjects/2DMapping/maps/reagan_maps/");
        //Cluster Grid2 = new Cluster("C:\\Users\\cnewby5283\\Documents\\NetBeansProjects\\AStarRouting\\maps\\reagan_maps"); //or 2DMapping
        
        
        //Methods to get a start and end node
        //System.out.println(Grid2.getStartNode("COUNSELOR"));
        //System.out.println(Grid2.getDestNode("KY132"));
        //Grid2.print(Grid2);
        Route = Grid2.routeAstar(Grid2.getStartNode("COUNSELOR"), Grid2.getDestNode("LIBRARY"));
        
        Grid2.drawRoute(Route);
        
        
        System.out.println(Grid2.routeString(Route));
        int test = Grid2.closest(40.0, 80.0).h();
        int test2 = Grid2.closest(40.0, 90.0).h();
        int test3 = Grid2.closest(40.0, 80.0).g();
        int test4 = Grid2.closest(40.0, 90.0).g();
        int test5 = Grid2.closest(40.0, 80.0).f();
        int test6 = Grid2.closest(40.0, 90.0).f();
        int test7 = Grid2.closest(70.0, 110.0).g();
        int test8 = Grid2.closest(70.0, 110.0).h();
        System.out.println("h(4, 8) = " + test);
        System.out.println("h(4, 9) = " +test2);
        System.out.println("g(4, 8) = " +test3);
        System.out.println("g(4, 9) = " +test4);
        System.out.println("f(4, 8) = " +test5);
        System.out.println("f(4, 9) = " +test6);
        System.out.println("g(7, 11) = " +test7);
        System.out.println("h(7, 11) = " +test8);
        Scanner S = new Scanner(System.in);
        while (true) {
            double x;
            double y;
            System.out.println("Enter an x:");
            x = S.nextInt() * 10.0;
            System.out.println("Enter a y:");
            y = S.nextInt() * 10.0;
            Node N = Grid2.closest(x, y);
            System.out.println(N.getRoomName() + "'s");
            System.out.println("g: " + N.g());
            System.out.println("h: " + N.h());
            System.out.println("f: " + N.f());
        }
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
