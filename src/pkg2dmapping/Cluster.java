package pkg2dmapping;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
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
    private File F;
    private BufferedImage Map;
    //0, 1 -> CN214
    
    
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
//        if (Multiple ties) {
//            while   still a tie
//            Astar(Open.poll(), Dest)
//        }
        Astar(OPEN.poll(), Dest);
    }
    
    public void AstarB1 (Node Best, Node Dest){
        CLOSED.add(Best);
        openViable(Best, Dest);
    }
    
    public void AstarBORING (Node Best, Node Dest){
        int counter = 0;
        AstarB1(Best, Dest);
        while(!OPEN.peek().equals(Dest)) {
            counter++;
            //tie Protocol???
            //What is the desired outcome? currently Open Nodes with identical
            //f costs without going forward with each of them ONLY POLL IF TIES EXIST
            Node N = OPEN.poll();
            //tieBreaker(N, Dest);
            AstarB1(N, Dest);
            if (counter > 500) {
                Dest.setParent(N);
                return;
            }
        }
        CLOSED.add(OPEN.poll());
        
    }
    
    public ArrayList<Node> routeAstar (Node Start, Node Dest){
        Start.setOrigin(true);
        Start.setParent(Start);
        AstarBORING(Start, Dest);
        ArrayList<Node> Retable = new ArrayList<Node>();
        Retable.add(Dest);
        while(!Retable.contains(Start)) {  
            Node Last = Retable.get(0);
            Retable.add(0, Last.getParent());
        }
        reset();
        return Retable;
    }
    
    public void tieBreaker(Node First, Node Dest) {
        try {
            if (First.f() < OPEN.peek().f()) {
                return;
            } else if (First.f() == OPEN.peek().f()) {
                Node N;
                N = OPEN.poll();
                AstarB1(N, Dest);
                tieBreaker(N, Dest);
            }
        } catch (NullPointerException e) {
            return;
        } catch (Exception e) {
            System.out.println("Tiebreaker broke");
        }
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
    
    public void connectAdjacent(Node N, Node[][] tempGrid, int i, int j){
        for (int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
                try {
                    if (!N.equals(tempGrid[k + i][j + l])) {
                        this.connect(N, tempGrid[k + i][j + l]);
                    }
                } catch (Exception e) {
                }
            }
        }
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
    
    public double distance(Node A, Node B){
        return Math.hypot(Math.abs(A.x() - B.x()), Math.abs(A.y() - B.y()));
    }
    
    public void closestStairs() {
        ArrayList<Node> Parsable = new ArrayList<Node>();
        for (Node N : this) {
            if (N.isWormhole()) {
                Parsable.add(N);
            }
        }
        int currMin = Integer.MAX_VALUE;
        //accumulation of distance
    }
    
    private BufferedImage generateBuferredImage (String fileLoc) throws Exception {
        BufferedImage img = null;
        File f = null;

        //read image
        try {
            f = new File(fileLoc);
            for (File map : f.listFiles()) {
                img = ImageIO.read(map);
                this.setF(map);
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
        this.setMap(img);
        return img;
    }
    
    public Node[][] generateNodeArray (BufferedImage img) throws Exception{ //
        String[][] classLocation = new String[26][17];
        classLocation[9][20] = "IND119";
        classLocation[9][21] = "IND120";
        classLocation[9][24] = "CUSTODIAN";
        classLocation[9][25] = "IND124";
        classLocation[10][5] = "VA143";
        classLocation[10][6] = "VA141";
        classLocation[10][7] = "VA139";
        classLocation[10][9] = "VA137";
        classLocation[11][3] = "BACKHALL";
        classLocation[12][3] = "TX145";
        classLocation[12][5] = "VA144";
        classLocation[12][6] = "VA142";
        classLocation[12][7] = "VA140";
        classLocation[12][9] = "VA138";
        classLocation[12][11] = "CLERKCOPY";
        classLocation[12][17] = "LA135";
        classLocation[12][22] = "HI125";
        classLocation[12][24] = "HI126";
        classLocation[13][11] = "LA135";
        classLocation[14][3] = "TX146";
        classLocation[14][5] = "OK148";
        classLocation[14][6] = "OK150";
        classLocation[14][7] = "OK152";
        classLocation[14][9] = "OK154";
        classLocation[14][11] = "NM156";
        classLocation[14][15] = "LA134";
        classLocation[14][17] = "KY132";
        classLocation[14][18] = "KY130";
        classLocation[14][21] = "KY128";
        classLocation[15][3] = "TX147";
        classLocation[15][11] = "NM155";
        classLocation[15][15] = "LA133";
        classLocation[15][24] = "HI127";
        classLocation[16][5] = "OK149";
        classLocation[16][6] = "OK151";
        classLocation[16][7] = "OK153";
        classLocation[16][18] = "STUCO";
        classLocation[16][19] = "KY131";
        classLocation[16][20] = "KYSTORAGE";
        classLocation[16][21] = "KY129";

        
        int rgb;
        int a;
        int r;
        int g;
        int b;
        int height = img.getHeight();
        int width = img.getWidth();
        System.out.println("Height is " + height + "\nWidth is " + width);
        boolean started = false;
        boolean ended = false;
        Node[][] retable = new Node[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rgb = img.getRGB(i, j);
                a = (rgb>>24) & 0xff;
                r = (rgb>>16) & 0xff;
                g = (rgb>>8) & 0xff;
                b = rgb & 0xff;
                if ((r + g + b) == (255*3)) { //white hallway
                    retable[i][j] = new Node("TNLA (" + i + "," + j + ")"/*collection.(i,j)*/, i * 10, j * 10);
                } 
                else if (((r + b) == 0) && (g == 255) && !started){ // green staircase
                    retable[i][j] = new Node("Start", i * 10, j * 10);
                    started = true;
                } 
                else if (((r + g) == 0) && (b == 255) && !started){ // blue classroom
                    if(classLocation[i][j] != null){
                        retable[i][j] = new Node(classLocation[i][j], i * 10, j * 10);
                    }
                    started = true;
                }
            }
        }
        if (!started) {
            throw new Exception("No node was indicated as start");
        } else if (!ended) {
            throw new Exception("No node was indicated as end");
        }
        return retable;
    }
    
    private ArrayList<Node> generateMap (Node[][] tempGrid) throws Exception{
        ArrayList<Node> retable = new ArrayList<>();
        boolean hit = false;
        for (int i = 0; i < tempGrid.length; i++) {
            for (int j = 0; j < tempGrid[i].length; j++) {
                Node N = tempGrid[i][j];
                if (N == null) {
                    continue;
                }
                this.connectAdjacent(N, tempGrid, i, j);
                retable.add(N);
                hit = true;
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
    
    public Node getStart(){
        for (Node N : this) {
            if (N.getRoomName() == "Start") {
                return N;
            }
        }
        return null;
    }
    
    public Node getDest(){
        for (Node N : this) {
            if (N.getRoomName() == "Dest") {
                return N;
            }
        }
        return null;
    }

    /**
     * @return the F
     */
    public File getF() {
        return F;
    }

    /**
     * @param F the F to set
     */
    public void setF(File F) {
        this.F = F;
    }

    /**
     * @return the Map
     */
    public BufferedImage getMap() {
        return Map;
    }

    /**
     * @param Map the Map to set
     */
    public void setMap(BufferedImage Map) {
        this.Map = Map;
    }
    
    public void drawRoute(ArrayList<Node> Route) throws IOException{
        for (Node N : Route) {
            if (N.getRoomName() == "Dest") {
                break;
            }
            int a = 0;
            int r = 255;
            int g = 175;
            int b = 0;
            int p = (a<<24) | (r<<16) | (g<<8) | b;
            if (N.x()/10 == 1) {
                if (N.y()/10 == 2) {
                    //Start isn't included in Route (PERHAPS) and so it's xy isnt correct but its never come up
                    //EXCEPT When drawing the route when the ghost pixel is actual where Start preports to be
                    System.out.println("What is your problem " + N.getRoomName().hashCode());
                    System.out.println("What is your problem " + this.getStart().getRoomName().hashCode());
                }
            }
            Map.setRGB(N.x()/10, N.y()/10, p);
        }
        File drawn = new File("C:\\Users\\jsebasco5721\\Documents\\NetBeansProjects\\astar\\maps\\drawnMaps" + F.getName().replaceAll(".png", "") + "DrawnRoute.png");
        ImageIO.write(Map, "png", drawn);
    }
    
}
