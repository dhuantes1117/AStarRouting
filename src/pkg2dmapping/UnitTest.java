/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2dmapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 *
 * @author dhuant
 */

public class UnitTest {
    public UnitTest(){
        
    }
    
    public boolean runAllTests(){
        try {
            manhattanCorrect();
            gCostCorrect();
            chooseLowerf();
            //adjustQueue();
            //tieBreaker();
            intendedRoute();
            //anUnreachableDestination();
        } catch (Exception e) {
            System.out.println("UnitTest has encountered an error:\n" + e.getMessage());
            e.printStackTrace();
            return true;
        }
        System.out.println("Unit Test has succesfully completed, no errors were detected");
        return false;
    }
    
    public void manhattanCorrect() throws Exception{
        Node A = new Node("51", 0, 0, true);
        Node B = new Node("57", 3, 4);
        A.sethManhattan(B);
        if(!(A.h() == 7)){
            throw new Exception("Manhattan distance is not being calculated correctly");
        }
    }
    
    public void gCostCorrect() throws Exception{
        Node A = new Node("A", 0, 0, true);
        Node B = new Node("B", 2, 0);
        Node C = new Node("C", 2, 2);
        
        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();
        ArrayList<Edge> Ce = new ArrayList<Edge>();
        
        Ae.add(new Edge(B, 2));
        Be.add(new Edge(A, 2));
        Be.add(new Edge(C, 2));
        Ce.add(new Edge(B, 2));
        
        A.setNeighbors(Ae);
        B.setNeighbors(Be);
        C.setNeighbors(Ce);
        
        A.setParent(A);
        B.setParent(A);
        C.setParent(B);
        
        A.updateg();
        B.updateg();
        C.updateg();
        
        if(!(C.g() == 4)){
            System.out.println(A.g());
            System.out.println(B.g());
            System.out.println(C.g());
            throw new Exception("g Cost is not being calculated correctly");
        }
    }
    
    public void chooseLowerf() throws Exception {
        Node A = new Node("Origin", 0, 0, true);
        Node B = new Node("Better", 1, 0);
        Node C = new Node("Worse", 0, 1);
        Node D = new Node("Destination", 3, 0);
        
        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();
        ArrayList<Edge> Ce = new ArrayList<Edge>();
        
        A.seth(D);
        B.seth(D);
        C.seth(D);
        
        Ae.add(new Edge(B, 1));
        Ae.add(new Edge(C, 1));
        Be.add(new Edge(A, 1));
        Ce.add(new Edge(A, 1));
        
        A.setNeighbors(Ae);
        B.setNeighbors(Be);
        C.setNeighbors(Ce);
        
        Cluster Grid = new Cluster(Arrays.asList(A, B, C, D));
        Grid.OPEN.add(A);
        Grid.OpenViable(A, D);
        if (!Grid.OPEN.peek().equals(B)) {
            throw new Exception("issue with f(N) cost not correctly calculated\n"+
                    "Program decided '" + Grid.OPEN.peek().getRoomName() +"' had lowest cost");
        }
    }
    
    public void intendedRoute() throws Exception{
        Node A = new Node("A", 0, 1, true);     //    D               H
        Node B = new Node("B", 0, 2);           //    |               |
        Node C = new Node("C", 1, 1);           //A---C---E--- --- ---G---I
        Node D = new Node("D", 1, 0);           //|       |
        Node E = new Node("E", 2, 1);           //B       F
        Node F = new Node("F", 2, 2);
        Node G = new Node("G", 5, 1);
        Node H = new Node("H", 5, 0);
        Node I = new Node("I", 6, 1);

        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();
        ArrayList<Edge> Ce = new ArrayList<Edge>();
        ArrayList<Edge> De = new ArrayList<Edge>();
        ArrayList<Edge> Ee = new ArrayList<Edge>();
        ArrayList<Edge> Fe = new ArrayList<Edge>();
        ArrayList<Edge> Ge = new ArrayList<Edge>();
        ArrayList<Edge> He = new ArrayList<Edge>();
        ArrayList<Edge> Ie = new ArrayList<Edge>();

        ArrayList<Node> Map = new ArrayList<Node>();
        ArrayList<Node> RouteTheo = new ArrayList<Node>();
        ArrayList<Node> RouteActual = new ArrayList<Node>();

        Cluster Grid;

        A.seth(I);
        B.seth(I);
        C.seth(I);
        D.seth(I);
        E.seth(I);
        F.seth(I);
        G.seth(I);
        H.seth(I);
        I.seth(I);

        Ae.add(new Edge(B, 1));
        Ae.add(new Edge(C, 1));
        Be.add(new Edge(A, 1));
        Ce.add(new Edge(A, 1));
        Ce.add(new Edge(D, 1));
        Ce.add(new Edge(E, 1));
        De.add(new Edge(C, 1));
        Ee.add(new Edge(C, 1));
        Ee.add(new Edge(F, 1));
        Ee.add(new Edge(G, 3));
        Fe.add(new Edge(E, 1));
        Ge.add(new Edge(E, 3));
        Ge.add(new Edge(H, 1));
        Ge.add(new Edge(I, 1));
        He.add(new Edge(G, 1));
        Ie.add(new Edge(G, 1));


        A.setNeighbors(Ae);
        B.setNeighbors(Be);
        C.setNeighbors(Ce);
        D.setNeighbors(De);
        E.setNeighbors(Ee);
        F.setNeighbors(Fe);
        G.setNeighbors(Ge);
        H.setNeighbors(He);
        I.setNeighbors(Ie);

        Map.add(A);
        Map.add(B);
        Map.add(C);
        Map.add(D);
        Map.add(E);
        Map.add(F);
        Map.add(G);
        Map.add(H);
        Map.add(I);

        Grid = new Cluster(Map);
        
        RouteTheo.add(A);
        RouteTheo.add(C);
        RouteTheo.add(E);
        RouteTheo.add(G);
        RouteTheo.add(I);
        
        try{
            RouteActual = Grid.RouteAstar(Grid.get(0), Grid.get(8));
            if (!RouteTheo.equals(RouteActual)) {
                throw new Exception("Anticipated Route was not taken\nRoute taken was:\n" + Grid.routeString(RouteActual));
            }

        } catch (StackOverflowError e) {
            throw new Exception("Inteded Destination was never reached");
        }
    }
    
    public void anUnreachableDestination() throws Exception{
        Node A = new Node("Origin", 0, 0, true);
        Node B = new Node("Step", 1, 0);    
        Node C = new Node("Destination", 2, 0);
        
        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();
        
        ArrayList<Node> Map = new ArrayList<Node>();
        ArrayList<Node> Route = new ArrayList<Node>();
        Cluster Grid;
        
        A.seth(C);
        B.seth(C);
        C.seth(C);
        
        Ae.add(new Edge(B, 1));
        Be.add(new Edge(A, 1));

        
        A.setNeighbors(Ae);
        B.setNeighbors(Be);
        
        Route.add(A);
        
        Grid = new Cluster();
        Grid.addAll(Arrays.asList(A, B, C));
        
        try {
            Route = Grid.Astar(0, Route, 2);
        } catch (NullPointerException e) {
            return;
        }
        throw new Exception("No issue was caused by an unreachable destination");
    }
}

