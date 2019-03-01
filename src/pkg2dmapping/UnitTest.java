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

    public UnitTest() {

    }

    public boolean runAllTests() {
        try {
            manhattanCorrect();
            gCostCorrect();
            chooseLowerf();
            assignParents();
            tieBreaker();
            intendedRoute();
            anUnreachableDestination();
            //teleportCorrect();
            //classCheck();
        } catch (Exception e) {
            System.out.println("UnitTest has encountered an error:\n" + e.getMessage());
            e.printStackTrace();
            return true;
        }
        System.out.println("Unit Test has succesfully completed, no errors were detected");
        return false;
    }

    public void manhattanCorrect() throws Exception {
        Node A = new Node("51", 0, 0, true);
        Node B = new Node("57", 3, 4);
        A.sethManhattan(B);
        if (!(A.h() == 7)) {
            throw new Exception("Manhattan distance is not being calculated correctly");
        }
    }

    public void gCostCorrect() throws Exception {
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

        if (!(C.g() == 4)) {
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
        Grid.CLOSED.add(A);
        Grid.openViable(A, D);
        if (!Grid.OPEN.peek().equals(B)) {
            throw new Exception("issue with f(N) cost not correctly calculated\n"
                    + "Program decided '" + Grid.OPEN.peek().getRoomName() + "' had lowest cost");
        }
    }

    public void assignParents() throws Exception {
        boolean fA, fB, fC, fD;
        //Make a line of 4 nodes, preform Astar
        Node A = new Node("A", 0, 1, true);     //                 
        Node B = new Node("B", 1, 1);           //A---B---C--- --- ---D
        Node C = new Node("C", 2, 1);           //       
        Node D = new Node("D", 5, 1);

        A.seth(D);
        B.seth(D);
        C.seth(D);
        D.seth(D);

        Cluster Grid = new Cluster(Arrays.asList(A, B, C, D));

        Grid.connect(A, B);
        Grid.connect(B, C);
        Grid.connect(C, D);

        A.setParent(A);
        Grid.AstarBORING(A, D);

        fA = (A.getParent().equals(A));
        fB = (B.getParent().equals(A));
        fC = C.getParent().equals(B);
        fD = (D.getParent().equals(C));
        if (!(fA && fB && fC && fD)) {
            throw new Exception("Parents were not assigned correctly\nA's parent was " + A.getParent().getRoomName()
                    + "\nB's parent was " + B.getParent().getRoomName() + "\nC's parent was " + C.getParent().getRoomName()
                    + "\nD's parent was " + D.getParent().getRoomName());
        }
    }

    public void intendedRoute() throws Exception {
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

        try {
            RouteActual = Grid.routeAstar(A, I);
            if (!RouteTheo.equals(RouteActual)) {
                throw new Exception("Anticipated Route was not taken\nRoute taken was:\n" + Grid.routeString(RouteActual));
            }

        } catch (StackOverflowError e) {
            throw new Exception("Inteded Destination was never reached");
        }
    }

    public void tieBreaker() throws Exception {
        //work with comparator in OPEN priorityQueue to preform openviable (and maybe something else?)
        //on everything that ties
        Node A = new Node("A", 0, 1, true);     //B---C---D---E
        Node B = new Node("B", 0, 0);           //|           |
        Node C = new Node("C", 1, 0);           //A           F
        Node D = new Node("D", 2, 0);           //|       
        Node E = new Node("E", 3, 0);           //G---H---I---J
        Node F = new Node("F", 3, 1);
        Node G = new Node("G", 0, 2);
        Node H = new Node("H", 1, 2);
        Node I = new Node("I", 2, 2);
        Node J = new Node("J", 3, 2);

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

        ArrayList<Node> Map = new ArrayList<Node>();
        ArrayList<Node> Route = new ArrayList<Node>();
        ArrayList<Node> RouteTheo = new ArrayList<Node>();

        Cluster Grid;

        A.seth(F);
        B.seth(F);
        C.seth(F);
        D.seth(F);
        E.seth(F);
        F.seth(F);
        G.seth(F);
        H.seth(F);
        I.seth(F);
        J.seth(F);

        Ae.add(new Edge(B, 1));
        Ae.add(new Edge(G, 1));
        Be.add(new Edge(A, 1));
        Be.add(new Edge(C, 1));
        Ce.add(new Edge(B, 1));
        Ce.add(new Edge(D, 1));
        De.add(new Edge(C, 1));
        De.add(new Edge(E, 1));
        Ee.add(new Edge(D, 1));
        Ee.add(new Edge(F, 1));
        Fe.add(new Edge(E, 1));
        Ge.add(new Edge(A, 1));
        Ge.add(new Edge(H, 1));
        He.add(new Edge(G, 1));
        He.add(new Edge(I, 1));
        Ie.add(new Edge(H, 1));
        Ie.add(new Edge(J, 1));
        Je.add(new Edge(I, 1));

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

        Map.add(A);
        Map.add(B);
        Map.add(C);
        Map.add(D);
        Map.add(E);
        Map.add(F);
        Map.add(G);
        Map.add(H);
        Map.add(I);
        Map.add(J);

        RouteTheo.add(A);
        RouteTheo.add(B);
        RouteTheo.add(C);
        RouteTheo.add(D);
        RouteTheo.add(E);
        RouteTheo.add(F);

        Grid = new Cluster(Map);

//        try {
            Route = Grid.routeAstar(A, F);
//        } catch (Exception e) {
//            System.out.println("An error was caused by a long tie");
//        }

        if (!Route.equals(RouteTheo)) {
            Grid.print(Route);
            //why is this route empty
            throw new Exception("tieBreaker resulted in a nonintended route being taken");
        }

    }

    public void anUnreachableDestination() throws Exception {
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
            Route = Grid.routeAstar(A, C);
        } catch (NullPointerException e) {
            return;
        }
        throw new Exception("No issue was caused by an unreachable destination");
    }

    public void teleportCorrect() throws Exception {
        Node A = new Node("A", 0, 0);
        Node B = new Node("B", 0, 1);
        Node C = new Node("C", 56, 42, false, true);
        Node D = new Node("D", 48, 50);

        A.seth(C);
        B.seth(C);
        C.seth(C);

        C.seth(D);
        D.seth(D);

        Cluster ZoneA = new Cluster(Arrays.asList(A, B, C));
        Cluster ZoneB = new Cluster(Arrays.asList(C, D));

        ZoneA.connect(A, B);
        ZoneA.connect(B, C);
        ZoneB.connect(C, D);

        LayeredCluster Universe = new LayeredCluster(Arrays.asList(ZoneA, ZoneB));

        ArrayList<Node> InterdimensionalTraversal = new ArrayList<Node>();
        ArrayList<Node> Actual = new ArrayList<Node>();
        InterdimensionalTraversal.addAll(Arrays.asList(A, B, C, D));
        Actual = Universe.Route(A, D);
        if (!Actual.equals(InterdimensionalTraversal)) {
            throw new Exception("InterdimensionalTraversal took an unexpected route!\nRoute taken was: " + ZoneA.routeString(Actual));
        }
    }

    public void classCheck() throws Exception {
        Cluster mapLoc = new Cluster("C:\\Users\\cnewby5283\\Documents\\NetBeansProjects\\AStarRouting\\maps\\reagan_maps");
        if (!mapLoc.closest(90, 140).getRoomName().equals("OK154")) {
            throw new Exception("jacked jacked it up");
        }
    }
}
