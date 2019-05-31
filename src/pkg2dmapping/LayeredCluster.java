//
package pkg2dmapping;

import java.util.*;

/**
 *
 * @author dhuant
 */
public class LayeredCluster extends ArrayList<Cluster> {

    //DM <-> DN
    //UM <-> UN
    Map<String, String> lateralJumps = new HashMap<String, String>();

    public void setup() {
        this.lateralJumps.put("DMtoDN", "Ulink");
        this.lateralJumps.put("DNtoDM", "Ulink");
        this.lateralJumps.put("UMtoUN", "Dlink");
        this.lateralJumps.put("UNtoUM", "Dlink");
        //what about 2 map jumps?

    }

    public LayeredCluster() {
        setup();
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
            //extra step, find a way so classify which kind of relation is going on and act
            //Map of strings saved as "NAMEtoNAME" would work, easy to error check...
            ///yeah lets do that
            String Goal = A_Layer.getName() + "to" + B_Layer.getName();
            if (lateralJumps.containsKey(Goal)) {
                Node commonPointA = A_Layer.getNode(lateralJumps.get(Goal));
                Node commonPointB = B_Layer.getNode(lateralJumps.get(Goal));
                ARoute = A_Layer.routeAstar(A, commonPointA);
                BRoute = B_Layer.routeAstar(commonPointB, B);
                //again these steps are garbage but they must remain, at least for testing
                B_Layer.remove(0);
                ARoute.addAll(B_Layer);
                return ARoute;
            } else {
                /*if(doubleJump){
               route bottom -> Dlink    OR     route top to -> Ulink 
               from DLink do normal common point staircase between the two
                Add all nodes together.
                
            }*/
                //I think the following method is deprecated
                //as with generation methods currently employed
                //it seems difficult to make the SAME node be in each rather than
                //a Node with the same name in each
                Node commonPoint = closestJump(A_Layer, B_Layer, A, B);
                ARoute = A_Layer.routeAstar(A, commonPoint);
                BRoute = B_Layer.routeAstar(commonPoint, B);
                //too much data is thrown away in the following steps.
                //what is likely to occur is LayeredCluster.route being completely replaced with
                //LayeredCluster.writeRoute,
                //including a new format of file i which first the x and y of the nodes are listed, THEN the
                //map from which the x and y references. This method of writing also has the potential to completly
                //remoe the need to have the maps standardize their coordinates
                B_Layer.remove(0);
                ARoute.addAll(B_Layer);
                return ARoute;
            }
        }
        //thx1138
    }

    /**
     * @param A the Cluster of Origin
     * @param B the Cluster of Destination
     * @param Origin the starting Node, located in A
     * @param Destination the ending Node, located in B
     */
    public Node closestJump(Cluster A, Cluster B, Node Origin, Node Destination) throws Exception {
        ArrayList<Node> Jumps = new ArrayList<>();
        if (A.size() >= B.size()) {
            for (Node N : B) {
                if (N.isJump() && A.contains(N)) {
                    Jumps.add(N);
                }
            }
        } else {
            for (Node N : A) {
                if (N.isJump() && B.contains(N)) {
                    Jumps.add(N);
                }
            }
        }

        if (Jumps.isEmpty()) {
            throw new Exception("Jumps was empty... look into this");
        }

        //accumulation
        double currMin = Integer.MAX_VALUE;
        double currVal;
        Node Best = new Node(-1, -1);
        for (Node W : Jumps) {
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
