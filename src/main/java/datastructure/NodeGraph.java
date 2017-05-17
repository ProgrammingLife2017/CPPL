package datastructure;

import java.util.ArrayList;

/**
 * Created by 101010.
 */
public class NodeGraph {
    /**
     * List of nodes.
     */
    private ArrayList<Node> nodes;

    /**
     * Database containing the segments of the nodes.
     */
    private SegmentDB segments;

    /**
     * Instance of the current graph.
     */
    private static NodeGraph currentNodeGraph;

    /**
     * Empty constructor for NodeGraph.
     */
    public NodeGraph() {
        this.nodes = new ArrayList<>(0);
        segments = new SegmentDB();
    }

    /**
     * Constructor for NodeGraph.
     * @param nodes
     *        The list of nodes for the graph.
     * @param segments
     *        The database containing the segments of the nodes.
     */
    public NodeGraph(final ArrayList<Node> nodes, final SegmentDB segments) {
        this.nodes = nodes;
        this.segments = segments;
    }

    /**
     * Adds a node to the graph.
     * @param id The id of the node.
     * @param node The node that gets added.
     * @param segment The segment of the node.
     */
    public void addNode(final int id, Node node, String segment) {
        while (nodes.size() <= id) {
            nodes.add(new Node());
        }

        int[] temp = nodes.get(id).getIncomingEdges();
        nodes.set(id, node);
        nodes.get(id).setIncomingEdges(temp);
        segments.addSegment(id, segment);
    }

    /**
     * Adds an edge to the graph.
     * @param from Origin node of the edge.
     * @param to Destination node of the edge.
     */
    public void addEdge(final int from, final int to) {
        while (nodes.size() <= from || nodes.size() <= to) {
            nodes.add(new Node());
        }

        nodes.get(from).addOutgoingEdge(to);
        nodes.get(to).addIncomingEdge(from);
    }

    /**
     * Retrieves a segment of a node
     * from the database.
     * @param id The id of the node.
     * @return The segment of the node.
     */
    public String getSegment(int id) {
        return segments.getSegment(id);
    }

    /**
     * Returns the node corresponding to the provided id.
     * @param id The id of the node.
     * @return The node.
     */
    public Node getNode(int id) {
        return nodes.get(id);
    }

    /**
     * Getter for the instance of this graph.
     * @return The graph we are workig with at this moment.
     */
    public static NodeGraph getCurrentInstance() {
        return currentNodeGraph;
    }

    /**
     * Setter for the current instance of graph.
     * @param g the graph we want to be the current graph.
     */
    public static void setCurrentInstance(NodeGraph g) {
        currentNodeGraph = g;
    }

    /**
     * Returns the index of a node.
     * @param n the node we want the index of.
     * @return the index of this node.
     */
    public int indexOf(Node n) {
        for (int i = 0; i < getCurrentInstance().nodes.size(); i++) {
            if (getCurrentInstance().nodes.get(i) == n) {
                return i;
            }
        }
        return -1;
    }
}
