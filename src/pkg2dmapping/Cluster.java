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
    
    public void Astar (PriorityQueue<Node> O, HashSet<Node> C, Node Best, Node Dest){
        if (Best.equals(Dest)){
            C.add(Best);
            CLOSED = C;
            return;
        }
        C.add(Best);
        O = OpenViable(O, C, Best, Dest);
        System.out.println("The best node from Best {" + Best.getRoomName() + "} to get to Dest {" + Dest.getRoomName() + "} is " + O.peek().getRoomName());
        System.out.println(O.peek().getRoomName() + "'s parent is [" + O.peek().getParent().getRoomName() + "] parent!");
        Astar(O, C, O.peek(), Dest);
    }
    
    public ArrayList<Node> RouteAstar (Node Start, Node Dest){
        Astar(OPEN, CLOSED, Start, Dest);
        //Iterate through closed getting Dest's parent
        ArrayList<Node> Retable = new ArrayList<Node>();
        Retable.add(Dest);
        while(!Retable.contains(Start)) {  
            Node Last = Retable.get(Retable.size() - 1);
            try {
                Retable.add(Last.getParent());
            } catch (NullPointerException e) {
                System.out.println(Retable);
                System.out.println(Retable.contains(Start));
                System.out.println(Start.getParent().getRoomName());
                System.out.println(Last.getRoomName());
            }    
        }
        return Retable;
    }
    
    public void tieBreaker(Edge First, Edge Second, Node Dest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public PriorityQueue<Node> OpenViable(PriorityQueue<Node> O, HashSet<Node> C, Node Curr, Node Dest) {
        PriorityQueue<Node> O1 = new PriorityQueue<>(O);
        ArrayList<Node> NodeList = new ArrayList<>();
        for (int i = 0; i < O1.size(); i++) {
            NodeList.add(O1.poll());
        }
        NodeList.addAll(Curr.getNeighborNodes());
        int a = NodeList.size() - 1;
        for (int i = 0; i < a; i++) {
            System.out.println(i);
            Node N = NodeList.get(i);
            N.updateg();
            if (O.contains(N) && (N.updateg(Curr) < N.g())) {
                System.out.println("Removing " + N.getRoomName() + " because\n"+
                        "The updated cost from " + Curr.getRoomName() + 
                        ", {" + N.updateg(Curr) + "} is lower than the original cost of " + N.g());
                NodeList.remove(N);
            }
            N.setParent(Curr);
            N.updateg();
            N.seth(Dest);
            NodeList.add(N);
        }
        OPEN = new PriorityQueue<Node>(c);
        OPEN.addAll(NodeList);
        return OPEN;
    }
}
