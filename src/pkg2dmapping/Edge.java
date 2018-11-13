        package pkg2dmapping;

public class Edge {

    private final int g;
    private boolean Parent;
    private final Node connection;
    
    
    public Edge(Node connection, int g) {
        this.connection = connection;
        this.g = g;
    }

    /**
     * @return the g
     */
    public int g() {
        return g;
    }

    /**
     * @return the connection
     */
    public Node getConnection() {
        return connection;
    }

    /**
     * @return the Parent
     */
    public boolean isParent() {
        return Parent;
    }

    /**
     * @param Parent the Parent to set
     */
    public void setParent(boolean Parent) {
        this.Parent = Parent;
    }

}
