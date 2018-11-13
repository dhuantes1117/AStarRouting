package pkg2dmapping;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;

class Node {

    private String RoomName;
    private String ClassName;
    private ArrayList<Edge> Neighbors;
    private Node Parent;
    private final Comparator<Edge> c = new Comparator<Edge>() {
        @Override
        public int compare(Edge i, Edge j) {
            //calculate f(n) = g(n) + h(n)
            //should make it so edge with lowest cost is chosen
            //shouldn't have to directly compare Nodes, just Edges (no g ohterwise)
            //include ISDESTINATION!!!
            int fi = i.g() + i.getConnection().h();
            int fj = j.g() + j.getConnection().h();
            if (fi > fj) {
                return 1;
            } else if (fj > fi) {
                return -1;
            } else {
                String namei = i.getConnection().getRoomName();
                String namej = j.getConnection().getRoomName();
                return 0;
            }
        }
    };
    private int x;
    private int y;
    private int g;
    private int h;
    private boolean destination;

    public Node(String RoomName, String ClassName, int x, int y, int h, ArrayList<Edge> Neighbors, boolean destination) {
        this.RoomName = RoomName;
        this.ClassName = ClassName;
        this.x = x;
        this.y = y;
        this.h = h;
        this.Neighbors = Neighbors;
        this.Neighbors.addAll(Neighbors);
        this.destination = destination;
    }

    public Node(String RoomName, int x, int y, ArrayList<Edge> Neighbors, boolean destination) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.Neighbors = Neighbors;
        this.Neighbors.addAll(Neighbors);
        this.destination = destination;
    }

    public Node(String RoomName, int x, int y, boolean destination) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.destination = destination;
    }

    public Node(String RoomName, int x, int y) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.destination = false;
    }

    public Node(int x, int y, boolean destination, ArrayList<Edge> Neighbors) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.destination = destination;
        this.Neighbors = Neighbors;
        this.Neighbors.addAll(Neighbors);
    }

    public Node(int x, int y, ArrayList<Edge> Neighbors) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.destination = destination;
        this.Neighbors = Neighbors;
        this.Neighbors.addAll(Neighbors);
    }

    public Node(int x, int y, boolean destination) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.destination = destination;
    }

    public Node(int x, int y) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.destination = false;
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
    public ArrayList<Edge> getNeighbors() {
        return Neighbors;
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
     * @return the destination
     */
    public boolean isDestination() {
        return destination;
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
        this.Neighbors = Neighbors;
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
        if (Parent.equals(this)) {
            this.setg(0);
            return;
        }
        boolean flag = false;
        for (Edge Neighbor : Neighbors) {
            if (Neighbor.getConnection().equals(Parent)) {
                flag = true;
                this.setg(Parent.g + Neighbor.g());
            }
        }
        if (!flag) {
            System.out.println("Lazy Error Control, Update g doesn't work");
        }
    }
    
    public int updateg(Node N) {
        for (Edge Neighbor : Neighbors) {
            System.out.println(Neighbor.getConnection().getRoomName() + ", is " + N.getRoomName()+ "?");
            if (Neighbor.getConnection().equals(N)) {
                System.out.println("Yes");
                return N.g + Neighbor.g();
            }
            System.out.println("No");
        }
        System.out.println("Lazy Error Control, Update g (Node) doesn't work");
        return -1;
    }

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
}
