/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2dmapping;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
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
    Predicate<Node> inCLOSED = new Predicate<Node>() {
            @Override
            public boolean test(Node N) {
                return CLOSED.contains(N);
            }
        };
    
    
    public Cluster(){
        
    }
    
    public Cluster(Node a){
        this.add(a);
    }
    
    public Cluster(Collection<Node> a){
        this.addAll(a);
    }
    
    public Cluster(String fileLoc) throws Exception{
        this.generate(fileLoc);
    }
    
    public Cluster(BufferedImage img) throws Exception{
        this.generate(img);
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
        openViable(Best, Dest);
        Astar(OPEN.poll(), Dest);
    }
    
    public ArrayList<Node> routeAstar (Node Start, Node Dest){
        //Start.getNeighborNodes().forEach((N) -> System.out.print(N.getRoomName() + " - "));
        Start.setOrigin(true);
        Start.setParent(Start);
        Astar(Start, Dest);
        //Iterate through closed getting Dest's parent
        ArrayList<Node> Retable = new ArrayList<Node>();
        Retable.add(Dest);
        while(!Retable.contains(Start)) {  
            Node Last = Retable.get(0);
            Retable.add(0, Last.getParent());
        }
        reset();
        return Retable;
    }
    
    public void tieBreaker(Edge First, Edge Second, Node Dest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void openViable(Node Curr, Node Dest) {
        ArrayList<Node> ApplicableNeighbors = Curr.getNeighborNodes();
        ApplicableNeighbors.removeIf(inCLOSED);
        for (Node N : ApplicableNeighbors) {
            N.updateg();
            if (OPEN.contains(N)) {
                if (N.updateg(Curr) < N.g()) {
                    OPEN.remove(N);
                } else if (N.updateg(Curr) > N.g()) {
                    continue;
                }
            }
            N.setParent(Curr);
            N.updateg();
            N.seth(Dest);
            OPEN.add(N);
        }
    }
    
    public void connect(Node A, Node B){
        if (!A.getNeighborNodes().contains(B) && !B.getNeighborNodes().contains(A)) {
            int distance = (int) Math.round(Math.hypot(B.x() - A.x(), B.y() - A.y()));
            A.getNeighbors().add(new Edge(B, distance));
            B.getNeighbors().add(new Edge(A, distance));
        } else if (!A.getNeighborNodes().contains(B) && B.getNeighborNodes().contains(A)) {
            System.out.println("Yikes");
        } else if (A.getNeighborNodes().contains(B) && !B.getNeighborNodes().contains(A)) {
            System.out.println("Yikes (Pt. 2)");
        }
        //Lazy error control is ok for early implementation
    }

    public void reset() {
        this.forEach((N) -> N.setOrigin(false));
        OPEN = new PriorityQueue<>(c);
        CLOSED = new HashSet<>();
    }
    
    public Node closest(double x, double y){
        double min = Double.MAX_VALUE;
        double dist;
        Node retable = this.get(0);
        for (int i = 0; i < this.size(); i++) {
            Node N = this.get(i);
            dist = (Math.abs(N.x() - x)+Math.abs(N.y() - y));
            if (dist < min) {
                min = dist;
                retable = N;
            }
        }
        return retable;
    }
    
    private BufferedImage generateBuferredImage (String fileLoc) throws Exception {
        BufferedImage img = null;
        File f = null;

        //read image
        try {
            f = new File(fileLoc);
            img = ImageIO.read(f);
        } catch (IOException e) {
            throw new Exception("File was unreadable");
        }
        return img;
    }
    
    private Node[][] generateNodeArray (BufferedImage img){
        int rgb;
        int a;
        int r;
        int g;
        int b;
        int height = img.getHeight();
        int width = img.getWidth();
        boolean started = false;
        boolean ended = true;
        Node[][] retable = new Node[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rgb = img.getRGB(i, j);
                a = (rgb>>24) & 0xff;
                r = (rgb>>16) & 0xff;
                g = (rgb>>8) & 0xff;
                b = rgb & 0xff;
                if ((r + g + b) == 0){
                    retable[i][j] = new Node("TNLA (" + i + "," + j + ")", i * 10, j * 10);
                } else if (((g + b) == 0) && (r == 255) && !ended){
                    retable[i][j] = new Node("Dest", i, j);
                    ended = true;
                } else if (((r + b) == 0) && (g == 255) && !started){
                    retable[i][j] = new Node("Start", i, j);
                    started = false;
                }
            }
        }
        return retable;
    }
    
    private ArrayList<Node> generateMap (Node[][] tempGrid) throws Exception{
        ArrayList<Node> retable = new ArrayList<>();
        boolean hit = false;
        for (int i = 0; i < tempGrid.length; i++) {
            for (int j = 0; j < tempGrid.length; j++) {
                try {
                    Node N = tempGrid[i][j];
                    this.connect(N, tempGrid[i - 1][j - 1]);
                    this.connect(N, tempGrid[i - 1][j]);
                    this.connect(N, tempGrid[i - 1][j + 1]);
                    this.connect(N, tempGrid[i + 1][j - 1]);
                    this.connect(N, tempGrid[i + 1][j]);
                    this.connect(N, tempGrid[i + 1][j + 1]);
                    this.connect(N, tempGrid[i][j + 1]);
                    this.connect(N, tempGrid[i][j - 1]);
                    retable.add(N);
                    hit = true;
                } catch (NullPointerException e) {
                }
            }
        }
        if (hit) {
            return retable;
        } else {
           throw new Exception("generateMap couldn't make a map"); 
        }
    }
    
    private void generate(BufferedImage img) throws Exception {
        this.addAll(generateMap(generateNodeArray(img)));
    }
    
    private void generate(String fileLoc) throws Exception {
        this.addAll(generateMap(generateNodeArray(generateBuferredImage(fileLoc))));
    }
}
