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
    
    //Astar will only work for Nodes with enstantiated Edges
    //Base case: if we're done, add it to route and return
    //if there's a tie fix the PriorityQueue
    //add head of Queue to Route
    //Preform Astar with head
    /*public ArrayList<Node> Astar(Node Curr, ArrayList<Node> Route, Node Dest){
        try {
            if(Curr.equals(Dest)){
                 Route.add(Dest);
                 return Route;
             }
             Node Shovable;
             //Tie handling if has been traveled to, no implemented removal of Destinations
             if(!Route.contains(Curr.getNeighbors().peek().getConnection())){
                Shovable = Curr.getNeighbors().peek().getConnection(); 
             } else {
                 PriorityQueue<Edge> temp1 = Curr.getNeighbors();
                 temp1.remove();
                 if (temp1.isEmpty()) {
                     throw new StackOverflowError("Astar reached an unnavigable point");
                 }
                 Shovable = temp1.peek().getConnection();
             }
             Route.add(Shovable);
             Astar(Shovable, Route, Dest);
             return Route;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }*/
    
    /*public ArrayList<Node> Astar(Node Curr, ArrayList<Node> Route, Node Dest){
        try {
            if(Curr.equals(Dest)){
                 return Route;
            }
            
            Node Shovable = this.getBestNode(Curr, Route, Dest);
            Route.add(Shovable);
            
            Astar(Shovable, Route, Dest);
            
            return Route;
            
        } catch (NullPointerException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public ArrayList<Node> Astar(int beg, ArrayList<Node> Route, int dest){
        ArrayList<Node> retable = new ArrayList<Node>();
        Node Curr = this.get(beg);
        Node Dest = this.get(dest);
        retable.add(Curr);
        return Astar(Curr, retable, Dest);
    }
    */
    public void Astar (PriorityQueue<Node> O, HashSet<Node> C, Node Best, Node Dest){
        if (Best.equals(Dest)){
            C.add(Best);
            return;
        }
        C.add(Best);
        OpenViable(O, C, Best, Dest);
        Astar(O, C, O.peek(), Dest);
    }
    
    public ArrayList<Node> RouteAstar (Node Start, Node Dest){
        ArrayList<Node> Retable = new ArrayList<>();
        Start.setg(0);
        Astar(OPEN, CLOSED, Start, Dest);
        Retable.add(Dest);
        while(!Retable.contains(Start)) {  
            Node Last = Retable.get(Retable.size() - 1);
            Retable.add(Last.getParent());     
        }
        return Retable;
    }
    
    /*public Node getBestNode (Node Curr, ArrayList<Node> Route, Node Dest) {
        PriorityQueue<Edge> Options = this.QAdjuster(Curr, Route, Dest);
        if (Options.isEmpty()) {
            System.out.println("The following Node has no remaining connections:\n"+
                    Curr.getRoomName());
            throw new NullPointerException();
        }
        return Options.peek().getConnection();
    }
    
    public PriorityQueue<Edge> QAdjuster(Node Curr, ArrayList<Node> Route, Node Dest){
        //Not coded for true ties
        PriorityQueue<Edge> retable = Curr.getNeighbors();
        retable.removeIf(new Predicate<Edge>() {
            @Override
            public boolean test(Edge t) {
                return (Route.contains(t.getConnection()) || (t.getConnection().isDestination() && t.getConnection() != Dest));
            }
        });
        return retable;
    }
    */
    public void tieBreaker(Edge First, Edge Second, Node Dest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void OpenViable(PriorityQueue<Node> O, HashSet<Node> C, Node Best, Node Dest) {
        PriorityQueue<Node> O1 = new PriorityQueue<>(O);
        for (Node N : O) {
            if (O1.contains(N) && (N.updateg(Best) < N.g())) {
                O1.remove(N);
            }
            N.setParent(Best);
            N.updateg();
            N.seth(Dest);
            O1.add(N);
        }
        OPEN = O1;
    }
}
