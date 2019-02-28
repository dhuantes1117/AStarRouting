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
    Predicate<Node> isClassroom = new Predicate<Node>(){
        @Override
        public boolean test(Node N){
            return N.isClassroom();
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
        //Cole code for end node
        System.out.println("End:");
    }
    
    public String routeString(ArrayList<Node> Route){
        String A = "Begin:\n";
        for (int i = 0; i < Route.size(); i++) {
            A = A.concat("[" + Route.get(i).getRoomName() + "]");
        }
        //Cole code for end node*****
        A = A.concat("\nEnd:\n");
        int cc = Route.size() - 1;
            A = A.concat("[" + Route.get(cc).getRoomName() + "]");
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
        System.out.println(Dest.getRoomName());
        AstarB1(Best, Dest);
        System.out.println(OPEN.size());
        while(!OPEN.peek().equals(Dest)) {
            counter++;
            //tie Protocol???
            //What is the desired outcome? currently Open Nodes with identical
            //f costs without going forward with each of them ONLY POLL IF TIES EXIST
            Node N = OPEN.poll();
            if (N.getRoomName().equals("TNLA (3,8)")) {
                System.out.println("wE MADE IT");
            }
            System.out.println(N.getRoomName());
            AstarB1(N, Dest);
//            if (OPEN.size() > 3) {
//                tieBreaker(N, Dest);
//            }
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
        Start.updateg();
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
        //try {
        //without weighted h or g, these costs are identical but why? because there's a ton of ties?
        //why doesn't getting rid of the recursive part make it work? who knows
            int a = First.f(Dest);
            int b = OPEN.peek().f(Dest);
            System.out.println("a: " + a);
            System.out.println("b: " + b);
            if ((a < b) || (a > b)) {
                return;
            } else if (a == b) {
                Node N = OPEN.poll();
                AstarB1(N, Dest);
//                if (OPEN.size() != 0) {
//                    tieBreaker(N, Dest);
//                }
            }
        /*} catch (NullPointerException e) {
            return;
        } catch (Exception e) {
            System.out.println("Tiebreaker broke");
        }*/
    }

    public void openViable(Node Curr, Node Dest) {
        ArrayList<Node> ApplicableNeighbors = Curr.getNeighborNodes();
        //System.out.println("Curr is " + Curr.getRoomName());
        //System.out.println(Curr.getNeighbors().size());
        ApplicableNeighbors.removeIf(inCLOSED);
        for (int i = 0; i < ApplicableNeighbors.size(); i++) {
            Node N = ApplicableNeighbors.get(i);
            if ((N.isWormhole() || N.isClassroom()) && !N.equals(Dest)) {//not checked with different floors
                ApplicableNeighbors.remove(i);
                i--;
            }
        }
        for (Node N : ApplicableNeighbors) {
            //N.updateg();
            if (OPEN.contains(N)) {
                if (N.g(Curr) <= N.g()) {
                    OPEN.remove(N);
                } else if (N.g(Curr) > N.g()) {
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
            classLocation[9][0] = "FL110";
            classLocation[3][1] = "CA106";
            classLocation[4][1] = "CA107";
            classLocation[5][1] = "CA108";
            classLocation[6][1] = "CA109";
            classLocation[7][1] = "MDR";
            classLocation[9][2] = "FL111";
            classLocation[5][3] = "CA105";
            classLocation[6][3] = "STORAGE";
            classLocation[7][3] = "WDR";
            classLocation[14][3] = "LIBRARY";
            classLocation[21][3] = "GA122";
            classLocation[23][3] = "GA123";
            classLocation[9][4] = "FL112";
            classLocation[0][5] = "COUNSELOR";
            classLocation[11][5] = "IND114";
            classLocation[21][5] = "GA121";
            classLocation[23][5] = "CTDO";
            classLocation[15][6] = "STORE";
            classLocation[4][7] = "IND101";
            classLocation[5][7] = "IND102";
            classLocation[6][7] = "IND103";
            classLocation[7][7] = "IND104";
            classLocation[9][7] = "FL113";
            classLocation[17][7] = "IND116";
            classLocation[19][7] = "IND118";
            classLocation[1][9] = "OFFICE";
            classLocation[3][9] = "NURSE";
            classLocation[11][9] = "ACR";
            classLocation[12][9] = "AO";
            classLocation[15][9] = "IND115";
            classLocation[17][9] = "SOED";
            classLocation[18][9] = "IND117";
            classLocation[19][9] = "IND117B";
            classLocation[20][9] = "IND119";
            classLocation[21][9] = "IND120";
            classLocation[24][9] = "CUSTODIAN";
            classLocation[25][9] = "IND124";
            classLocation[5][10] = "VA143";
            classLocation[6][10] = "VA141";
            classLocation[7][10] = "VA139";
            classLocation[9][10] = "VA137";
            classLocation[3][11] = "BACKHALL";
            classLocation[3][12] = "TX145";
            classLocation[5][12] = "VA144";
            classLocation[6][12] = "VA142";
            classLocation[7][12] = "VA140";
            classLocation[9][12] = "VA138";
            classLocation[11][12] = "CLERKCOPY";
            classLocation[17][12] = "LA135";
            classLocation[22][12] = "HI125";
            classLocation[24][12] = "HI126";
            classLocation[11][13] = "LA135";
            classLocation[3][14] = "TX146";
            classLocation[5][14] = "OK148";
            classLocation[6][14] = "OK150";
            classLocation[7][14] = "OK152";
            classLocation[9][14] = "OK154";
            classLocation[11][14] = "NM156";
            classLocation[15][14] = "LA134";
            classLocation[17][14] = "KY132";
            classLocation[18][14] = "KY130";
            classLocation[21][14] = "KY128";
            classLocation[3][15] = "TX147";
            classLocation[11][15] = "NM155";
            classLocation[15][15] = "LA133";
            classLocation[24][15] = "HI127";
            classLocation[5][16] = "OK149";
            classLocation[6][16] = "OK151";
            classLocation[7][16] = "OK153";
            classLocation[18][16] = "STUCO";
            classLocation[19][16] = "KY131";
            classLocation[20][16] = "KYSTORAGE";
            classLocation[21][16] = "KY129";

        
        int rgb;
        int a;
        int r;
        int g;
        int b;
        int height = img.getHeight();
        int width = img.getWidth();
        //System.out.println("Height is " + height + "\nWidth is " + width);
        boolean started = false;
        boolean ended = false;
        Node[][] retable = new Node[width][height];
        //Cole new code for end node
        //these lines define beginning and end of each step
        //need to write code to define overall beginning and end
        
        
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
                else if ((b == 0) && (g == 100) && (r == 100)){ // yellow staircase
                    retable[i][j] = new Node("Staircase At (" + i + "," + j + ")", i * 10, j * 10, false, true);
                    started = true;
                } 
                else if (((r + g) == 0) && (b == 255)){ // blue classroom
                    try {
                        if(classLocation[i][j] != null){
                            retable[i][j] = new Node(classLocation[i][j], i * 10, j * 10, true, 'a');
                        }
                        started = true;
                    } catch (Exception e) {
                    }
                }
            }
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
        File drawn = new File("C:\\Users\\cnewby5283\\Documents\\NetBeansProjects\\AStarRouting\\maps\\reagan_maps" + F.getName().replaceAll(".png", "") + "DrawnRoute.png");
        ImageIO.write(Map, "png", drawn);
    }
    boolean foundStart;
    public Node getStartNode(String desiredStartRoom){
        Node A =  new Node("Empty Room", 1, 1);;
        for (Node N : this) {
            if (N.getRoomName().equals(desiredStartRoom)) {
                A = N;
            }
        }
        return A;
    }
    boolean foundEnd;
    public Node getDestNode(String desiredEndRoom){
        Node A =  new Node("Empty Room", 1, 1);;
        for (Node N : this) {
            if (N.getRoomName().equals(desiredEndRoom)) {
                A = N;
            }
        }
        return A;
    }
    
}
