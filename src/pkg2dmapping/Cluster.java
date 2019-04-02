package pkg2dmapping;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
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
    private File classMap;
    private Map<Dimension, Dimension> coordMap;
    String[][] classLocation = new String[26][17];//size will vary -- populate using generateClassLocations
    private File imageFile;
    private BufferedImage writableEnvironment;
    private File ClassLocations;
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
    
    public Cluster(String imageLoc, String classLoc, String mappingLoc) throws IOException{
        this.imageFile = new File(imageLoc);
        this.ClassLocations = new File (classLoc);
        this.classMap = new File(mappingLoc);
        
        this.generateWritableEnvironment(imageFile);
        this.generateCoordinateMapping(classMap);
        this.generateClassLocations(ClassLocations);
        
        //what else???
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
            if (OPEN.size() == 1) {
                AstarB1(OPEN.poll(), Dest);
                continue;
            }
            Node N = OPEN.poll();
            if (OPEN.size() > 0 && N.f() == OPEN.peek().f()) {
                tieBreaker(N, Dest);
            } else {
                AstarB1(N, Dest);
            }
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
        Dest.seth(Dest);
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
        Node Second = OPEN.poll();
        boolean flagf = First.getNeighborNodes().contains(Dest);
        boolean flags = Second.getNeighborNodes().contains(Dest);
        if (flagf ^ flags) {
            if (flagf) {
                AstarB1(First, Dest);
            } else {
                AstarB1(Second, Dest);
            }
        } else if (flagf && flags) {
            AstarB1(First, Dest);
        } else if (!flagf && !flags) {
            AstarB1(First, Dest);
            AstarB1(Second, Dest);
        }
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
    
    private void generateClassLocations(File f) throws FileNotFoundException{
        Scanner s = new Scanner(f);
        while(s.hasNextLine()){
            String[] temp = s.nextLine().split(",");
            if (temp.length != 3) {
                throw new FileNotFoundException("File was not correctly formatted");
            }
            int i = Integer.parseInt(temp[0]);
            int j = Integer.parseInt(temp[1]);
            classLocation[i][j] = temp[2];
        }
    }
    
    private BufferedImage generateWritableEnvironment(File imgFile) throws IOException{
        this.writableEnvironment = ImageIO.read(imgFile);
        return this.writableEnvironment;
    }
    
    private void drawRoute(ArrayList<Node> Route) throws IOException{
        int[][] route = new int[Route.size()][2];
        Graphics2D canvas = this.writableEnvironment.createGraphics();
        for (int i = 0; i < Route.size(); i++) {
            Node N = Route.get(i);
            Dimension D = coordMap.get(new Dimension(N.x(), N.y()));
            route[i][0] = N.x();
            route[i][1] = N.y();
        }
        canvas.setColor(Color.orange);
        for (int i = 0; i < route.length - 1; i++) {
            canvas.drawLine(route[i][0], route[i][1], route[i + 1][0], route[i + 1][1]);
        }
        File drawn = new File(imageFile.getAbsolutePath() + File.pathSeparator + "drawnMaps" + File.pathSeparator + imageFile.getName().replace(".png", "DrawnRoute.png"));
        ImageIO.write(writableEnvironment, "png", drawn);
    }
    
    private void generateCoordinateMapping(File classMapFile) throws FileNotFoundException{
        coordMap = new HashMap<Dimension, Dimension>();
        Scanner s = new Scanner(classMapFile);
        while(s.hasNextLine()){
            String[] temp = s.nextLine().split(",");
            int[] coords = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
                coords[i] = Integer.parseInt(temp[i]);
            }
            Dimension a = new Dimension(coords[0], coords[1]);
            Dimension b = new Dimension(coords[2], coords[3]);
            coordMap.put(a, b);
        }
            
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
    
    public Node[][] generateNodeArray (BufferedImage img) throws Exception{
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
    
    @Deprecated
    public void drawRoute(ArrayList<Node> Route, Object useless) throws IOException{
        for (Node N : Route) {
            if (N.getRoomName() == "Dest") {
                break;
            }
            int a = 255;
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
        File drawn = new File("H:\\AStarRouting-master\\maps\\drawnMaps\\" + F.getName().replaceAll(".png", "") + "DrawnRoute.png");
        //File drawn = new File("C:\\Users\\cnewby5283\\Documents\\NetBeansProjects\\AStarRouting\\maps\\drawnMaps\\" + F.getName().replaceAll(".png", "") + "DrawnRoute.png");
        ImageIO.write(Map, "png", drawn);
    }
    
    public Node getNode(String desiredStartRoom){
        Node A =  new Node("Empty Room", 1, 1);;
        for (Node N : this) {
            if (N.getRoomName().equals(desiredStartRoom)) {
                A = N;
            }
        }
        return A;
    }
    
}
