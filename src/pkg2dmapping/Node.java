package pkg2dmapping;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class Node{
    
    private int x;
    private int y;
    private int g;
    private int h;
    private boolean origin;
    private boolean classroom;
    private boolean wormhole;
    private String RoomName;
    private String ClassName;
    private Node Parent;
    private final Comparator<Edge> c = new Comparator<Edge>() {
        @Override
        public int compare(Edge i, Edge j) {
            //calculate f(n) = g(n) + h(n)
            //should make it so edge with lowest cost is chosen
            //shouldn't have to directly compare Nodes, just Edges (no g ohterwise)
            //include ISDESTINATION!!!
            int fi = i.g();
            int fj = j.g();
            if (!(i.isParent() && j.isParent()) && (i.isParent() || j.isParent())) {
                if (i.isParent()) {
                    return -1;
                } else if (j.isParent()) {
                    return 1;
                }
            }
            if (fi > fj) {
                return 1;
            } else if (fj > fi) {
                return -1;
            } else {
                return 0;
            }
        }
    };
    private PriorityQueue<Edge> Neighbors = new PriorityQueue<>(c);

    public Node(String RoomName, String ClassName, int x, int y, int h, ArrayList<Edge> Neighbors, boolean destination) {
        this.RoomName = RoomName;
        this.ClassName = ClassName;
        this.x = x;
        this.y = y;
        this.h = h;
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
        this.origin = destination;
    }

    public Node(String RoomName, int x, int y, ArrayList<Edge> Neighbors, boolean destination) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
        this.origin = destination;
    }

    public Node(String RoomName, int x, int y, boolean destination) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = destination;
    }
    
    public Node(String RoomName, int x, int y, boolean destination, boolean wormhole) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = destination;
        this.wormhole = wormhole;
    }

    public Node(String RoomName, int x, int y) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = false;
    }

    public Node(int x, int y, boolean destination, ArrayList<Edge> Neighbors) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = destination;
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
    }

    public Node(int x, int y, ArrayList<Edge> Neighbors) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = origin;
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
    }

    public Node(int x, int y, boolean destination) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = destination;
    }

    public Node(int x, int y) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = false;
    }
    
    public Node(Node N) {
        this.x = N.x();
        this.y = N.y();
        this.g = N.g();
        this.h = N.h();
        this.origin = N.origin;
        this.classroom = N.classroom;
        this.RoomName = N.RoomName;
        this.ClassName = N.ClassName;
        this.Parent = N.Parent;
        this.Neighbors = N.getNeighbors();
    }

    /**
     * @return the RoomName
     */
    public String getRoomName() {
        return RoomName;
    }

    /**
     * @return the ClassName
     */
    public String getClassName() {
        return ClassName;
    }

    /**
     * @return the Neighbors
     */
    public PriorityQueue<Edge> getNeighbors() {
        return Neighbors;
    }
    
    public ArrayList<Node> getNeighborNodes(){
        ArrayList<Node> Retable = new ArrayList<>();
        for (Edge edge : Neighbors) {
            Retable.add(edge.getConnection());
        }
        return Retable;
    }

    /**
     * @return the h
     */
    public int h() {
        return h;
    }

    /**
     * @param h the h to set
     */
    private void seth(int h) {
        this.h = h;
    }

    /**
     * @return the origin
     */
    public boolean isOrigin() {
        return origin;
    }

    /**
     * @param h the h to set
     */
    public void sethManhattan(Node Dest) {
        seth(Math.abs(Dest.x() - this.x()) + Math.abs(Dest.y() - this.y()));
    }
    
    public void seth(Node Dest) {
        sethManhattan(Dest);
    }

    public int f(Node Dest) {
        return this.g() + this.h();
    }

    /**
     * @return the x
     */
    public int x() {
        return x;
    }

    /**
     * @return the y
     */
    public int y() {
        return y;
    }

    /**
     * @param Neighbors the Neighbors to set
     */
    public void setNeighbors(ArrayList<Edge> Neighbors) {
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
    }

    /**
     * @param RoomName the RoomName to set
     */
    public void setRoomName(String RoomName) {
        this.RoomName = RoomName;
    }

    /**
     * @param ClassName the ClassName to set
     */
    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public int g() {
        return this.g;
    }

    public void updateg() {
        //isOrigin add? or use isDestination to get around
        //the beginning not being able to be it's own parent
        //bc it's not in it's own neighbor's queue
        if (origin) {
            this.setg(0);
            return;
        }
        Edge ParentEdge = this.Neighbors.peek();
        this.setg(ParentEdge.getConnection().g + ParentEdge.g());
    }
    
    public int updateg(Node N) {
        //give g if parameter was parent
        //return N.g + Neighbors.peek().g();
        for (Edge Neighbor : Neighbors) {
            if (Neighbor.getConnection().equals(N)) {
                return N.g + Neighbor.g();
            }
        }
        return -1;
    }
    //Watch Forever Amazon Prime
    //t(t-4)^(1/3)

    /**
     * @return the Parent
     */
    public Node getParent() {
        return Parent;
    }

    /**
     * @param Parent the Parent to set
     */
    public void setParent(Node Parent) {
        this.Parent = Parent;
    }

    /**
     * @param g the g to set
     */
    public void setg(int g) {
        this.g = g;
    }
    
    public int f() {
        return this.g + this.h;
    }

    /**
     * @return the classroom
     */
    public boolean isClassroom() {
        return classroom;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(boolean origin) {
        this.origin = origin;
    }

    /**
     * @param classroom the classroom to set
     */
    public void setClassroom(boolean classroom) {
        this.classroom = classroom;
    }

    /**
     * @return the wormhole
     */
    public boolean isWormhole() {
        return wormhole;
    }

    /**
     * @param wormhole the wormhole to set
     */
    public void setWormhole(boolean wormhole) {
        this.wormhole = wormhole;
    }
}
