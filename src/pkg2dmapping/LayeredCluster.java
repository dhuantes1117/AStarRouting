//
package pkg2dmapping;

import java.util.*;

/**
 *
 * @author dhuant
 */
public class LayeredCluster extends ArrayList<Cluster>{
    public LayeredCluster() {
        
    }
    
    public LayeredCluster(List<Cluster> Areas) {
        this.addAll(Areas);
    }
    
    public ArrayList<Node> Route(Node A, Node B) throws Exception {
        //find the floor they're on -> save index of Cluster, do not generalize
        ArrayList<Node> ARoute = new ArrayList<Node>();
        ArrayList<Node> BRoute = new ArrayList<Node>();
        ArrayList<Node> Retable = new ArrayList<Node>();
        Cluster A_Layer = new Cluster();
        Cluster B_Layer = new Cluster();
        for (Cluster Layer : this) {
            if (Layer.contains(A)) {
                A_Layer = Layer;
            } else if (Layer.contains(B)) {
                B_Layer = Layer;
            }
        }
        if (A_Layer == B_Layer) {
            return A_Layer.routeAstar(A, B);
        } else {
            Node commonPoint = closestWormhole(A_Layer, B_Layer, A, B);
            ARoute = A_Layer.routeAstar(A, commonPoint);
            BRoute = B_Layer.routeAstar(commonPoint, B);
            B_Layer.remove(0);
            ARoute.addAll(B_Layer);
            return ARoute;
        }
        //thx1138
    }
    
    /**
     * @param A the Cluster of Origin
     * @param B the Cluster of Destination
     * @param Origin the starting Node, located in A
     * @param Destination the ending Node, located in B
     */
    public Node closestWormhole(Cluster A, Cluster B, Node Origin, Node Destination) throws Exception {
        ArrayList<Node> Wormholes = new ArrayList<>();
        if (A.size() >= B.size()) {
            for (Node N : B) {
                if (N.isWormhole() && A.contains(N)) {
                    Wormholes.add(N);
                }
            }
        } else {
            for (Node N : A) {
                if (N.isWormhole() && B.contains(N)) {
                    Wormholes.add(N);
                }
            }
        }
        
        if (Wormholes.isEmpty()) {
            throw new Exception("Wormholes was empty... look into this");
        }
        
        //accumulation
        double currMin = Integer.MAX_VALUE;
        double currVal;
        Node Best = new Node(-1, -1);
        for (Node W : Wormholes) {
            currVal = A.distance(W, Origin) + B.distance(W, Destination);
            if (currVal < currMin) {
                currMin = currVal;
                Best = W;
            }
        }
        if (Best.x() == -1 && Best.y() == -1) {
            throw new Exception("The two Areas do not contain a wormhole in common");
        }
        return Best;
    }
}
