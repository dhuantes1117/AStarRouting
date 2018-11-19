/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2dmapping;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Predicate;

/**
 *
 * @author dhuant
 */

public class Cluster extends ArrayList<Node>{
    
    private final Comparator<Node> c = new Comparator<Node>() {
        @Override
        public int compare(Node i, Node j) {
            //calculate f(n) = g(n) + h(n)
            //should make it so edge with lowest cost is chosen
            //shouldn't have to directly compare Nodes, just Edges (no g ohterwise)
            //include ISDESTINATION!!!
            int fi = i.f();
            int fj = j.f();
            if (fi > fj) {
                return 1;
            } else if (fj > fi) {
                return -1;
            } else {
                String namei = i.getRoomName();
                String namej = j.getRoomName();
                return 0;
            }
        }
    };
    PriorityQueue<Node> OPEN = new PriorityQueue<>(c);
    HashSet<Node> CLOSED = new HashSet<>();
    
    
    public Cluster(){
        
    }
    
    public Cluster(Node a){
        this.add(a);
    }
    
    public Cluster(Collection<Node> a){
        this.addAll(a);
    }
    
    public void print(ArrayList<Node> Route){
        System.out.println("Begin:");
        for (int i = 0; i < Route.size(); i++) {
            System.out.println("[" + Route.get(i).getRoomName() + "]");
        }
        System.out.println("End");
    }
    
    public String routeString(ArrayList<Node> Route){
        String A = "Begin:\n";
        for (int i = 0; i < Route.size(); i++) {
            A = A.concat("[" + Route.get(i).getRoomName() + "]");
        }
        A = A.concat("\nEnd");
        return A;
    }
    
    public void Astar (Node Best, Node Dest){
        if (Best.equals(Dest)){
            CLOSED.add(Best);
            return;
        }
        CLOSED.add(Best);
        OpenViable(Best, Dest);Astar(OPEN.poll(), Dest);
    }
    
    public ArrayList<Node> RouteAstar (Node Start, Node Dest){
        //Start.getNeighborNodes().forEach((N) -> System.out.print(N.getRoomName() + " - "));
        Start.setParent(Start);
        Astar(Start, Dest);
        //Iterate through closed getting Dest's parent
        ArrayList<Node> Retable = new ArrayList<Node>();
        Retable.add(Dest);
        while(!Retable.contains(Start)) {  
            Node Last = Retable.get(0);
            Retable.add(0, Last.getParent());
        }
        return Retable;
    }
    
    public void tieBreaker(Edge First, Edge Second, Node Dest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void OpenViable(Node Curr, Node Dest) {
        PriorityQueue<Node> O1 = new PriorityQueue<>(OPEN);
        ArrayList<Node> NodeList = new ArrayList<>();
        for (int i = 0; i < O1.size(); i++) {
            NodeList.add(O1.poll());
        }
        ArrayList<Node> ApplicableNeighbors = Curr.getNeighborNodes();
        ApplicableNeighbors.removeIf(new Predicate<Node>() {
            @Override
            public boolean test(Node N) {
                return CLOSED.contains(N);
            }
        });
        for (Node N : ApplicableNeighbors) {
            N.updateg();
            if (OPEN.contains(N) && (N.updateg(Curr) < N.g())) {
                NodeList.remove(N);
            }
            N.setParent(Curr);
            N.updateg();
            N.seth(Dest);
            NodeList.add(N);
        }
        OPEN.clear();
        OPEN.addAll(NodeList);
    }
}
