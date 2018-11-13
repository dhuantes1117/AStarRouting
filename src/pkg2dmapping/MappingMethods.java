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
    public static void main(String[] args) throws IOException {
        
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
        *//*
        Cluster Map = generateMap();
        
        ArrayList<Node> Route = new ArrayList<Node>();
        Route.add(Map.get(0));
        
        Route = Map.Astar(0, Route, 9);
        Map.print(Route);
        Cluster Grid = generateMap();
        ArrayList<Node> Route = Grid.RouteAstar(Grid.get(0), Grid.get(9));
        for (int i = Route.size() -1; i > 0; i--) {
            System.out.println(Route.get(i).getRoomName());
        }*/
        
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
        
        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();
        ArrayList<Edge> Ce = new ArrayList<Edge>();
        ArrayList<Edge> De = new ArrayList<Edge>();
        ArrayList<Edge> Ee = new ArrayList<Edge>();
        ArrayList<Edge> Fe = new ArrayList<Edge>();
        ArrayList<Edge> Ge = new ArrayList<Edge>();
        ArrayList<Edge> He = new ArrayList<Edge>();
        ArrayList<Edge> Ie = new ArrayList<Edge>();
        ArrayList<Edge> Je = new ArrayList<Edge>();
        ArrayList<Edge> Ke = new ArrayList<Edge>();
        
        ArrayList<Node> Map = new ArrayList<Node>(Arrays.asList(A, B, C, D, E, F, G, H, I, J, K));
        
        for (int i = 0; i < Map.size(); i++) {
            Map.get(i).seth(E);
        }
        //Edge constructor adds a node by value not reference, manhattan must be set prior
        Ae.add(new Edge(C, 1));
        
        Be.add(new Edge(D, 1));
        Be.add(new Edge(F, 1));
        
        Ce.add(new Edge(A, 1));
        Ce.add(new Edge(D, 1));
        
        De.add(new Edge(B, 1));
        De.add(new Edge(C, 1));
        De.add(new Edge(E, 1));
        
        Ee.add(new Edge(D, 1));
        Ee.add(new Edge(F, 1));
        Ee.add(new Edge(G, 4));
        Ee.add(new Edge(H, 5));
        
        Fe.add(new Edge(E, 1));
        Fe.add(new Edge(B, 1));
        
        Ge.add(new Edge(E, 5));
        Ge.add(new Edge(H, 3));
        Ge.add(new Edge(I, 2));
        
        He.add(new Edge(G, 3));
        He.add(new Edge(E, 4));
        He.add(new Edge(K, 1));
        
        Ie.add(new Edge(G, 2));
        Ie.add(new Edge(J, 4));
        
        Je.add(new Edge(I, 4));
        
        Ke.add(new Edge(H, 1));
        
        A.setNeighbors(Ae);
        B.setNeighbors(Be);
        C.setNeighbors(Ce);
        D.setNeighbors(De);
        E.setNeighbors(Ee);
        F.setNeighbors(Fe);
        G.setNeighbors(Ge);
        H.setNeighbors(He);
        I.setNeighbors(Ie);
        J.setNeighbors(Je);
        K.setNeighbors(Ke);
        
        Cluster retable = new Cluster();
        retable.addAll(Map);
        return retable;
    }
}
