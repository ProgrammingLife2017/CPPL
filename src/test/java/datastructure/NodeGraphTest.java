package datastructure;

import java.util.Iterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import org.mockito.Mock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by 101010.
 */
public class NodeGraphTest {
    /**
     * The NodeGraph used to test.
     */
    private NodeGraph nodeGraph = null;

    @Mock
    ArrayList<Node> nodes;

    @Mock
    LinkedList<DrawNode> drawNodes;

    @Mock
    SegmentDB segmentDB;

    @Mock
    Node node;

    @Mock
    LinkedList<DummyNode> dummyNodes;

    @Mock
    DrawNode drawNode;

    @Mock
    Iterator<DrawNode> iterator;

    /**
     * Before each test we set the nodeGraph to a new NodeGraph.
     */
    @Before
    public void setUp() {
        nodes = mock(new ArrayList<Node>().getClass());
        drawNodes = mock(new LinkedList<DrawNode>().getClass());
        segmentDB = mock(SegmentDB.class);
        node = mock(Node.class);
        drawNode = mock(DrawNode.class);
        iterator = mock(Iterator.class);
        dummyNodes = mock(new LinkedList<DummyNode>().getClass());
        when(node.getIncomingEdges()).thenReturn(new int[0]);
        when(node.getOutgoingEdges()).thenReturn(new int[0]);
        when(segmentDB.getSegment(anyInt())).thenReturn("Segment");
        when(nodes.get(anyInt())).thenReturn(node);
        when(drawNodes.get(anyInt())).thenReturn(drawNode);
        when(drawNode.getIndex()).thenReturn(0);
        when(drawNodes.iterator()).thenReturn(iterator);
        when(iterator.next()).thenReturn(drawNode);
        when(iterator.hasNext()).thenReturn(true);
        nodeGraph = new NodeGraph(nodes, segmentDB, drawNodes, dummyNodes);
    }

    /**
     * After each test we set the nodeGraph back to null.
     */
    @After
    public void tearDown() {
        nodeGraph.setCurrentInstance(null);
        nodeGraph = null;
        nodes = null;
        drawNodes = null;
        segmentDB = null;
        node = null;
    }

    @Test
    public void addNode() {
        nodeGraph = new NodeGraph(new ArrayList<Node>(0), segmentDB, drawNodes, new LinkedList<>());
        nodeGraph.addNode(100, node);
        assertEquals(101, nodeGraph.getSize());
        assertEquals(node, nodeGraph.getNode(100));
        verify(node, times(1)).computeLength();
        verify(node, times(1)).setIncomingEdges(any());
    }

    @Test
    public void addNodeCache() {
        nodeGraph = new NodeGraph(new ArrayList<>(0), segmentDB, drawNodes, new LinkedList<>());
        nodeGraph.addNodeCache(100, node);
        assertEquals(101, nodeGraph.getSize());
        assertEquals(node, nodeGraph.getNode(100));
        verify(node, never()).computeLength();
        verify(node, never()).setIncomingEdges(any());
    }

    @Test
    public void addEdge() {
        nodeGraph = new NodeGraph(new ArrayList<>(0), segmentDB, drawNodes, new LinkedList<>());
        Node node2 = mock(Node.class);

        nodeGraph.addNode(0, node);
        nodeGraph.addNode(1, node2);
        nodeGraph.addEdge(0, 1);

        verify(node, times(1)).addOutgoingEdge(1);
        verify(node, never()).addIncomingEdge(anyInt());
        verify(node2, never()).addOutgoingEdge(anyInt());
        verify(node2, times(1)).addIncomingEdge(0);
    }

    @Test
    public void getSegment() {
        int r = new Random().nextInt();
        assertEquals("Segment", nodeGraph.getSegment(r));
        verify(segmentDB, times(1)).getSegment(r);
    }

    @Test
    public void getNode() {
        assertEquals(node, nodeGraph.getNode(0));
        verify(nodes, times(1)).get(0);
    }

    @Test
    public void getCurrentInstance() {
        assertNull(nodeGraph.getCurrentInstance());
    }

    @Test
    public void setCurrentInstance() {
        nodeGraph.setCurrentInstance(nodeGraph);
        assertEquals(nodeGraph.getDrawNodes(), nodeGraph.getCurrentInstance().getDrawNodes());
        assertEquals(nodeGraph.getNodes(), nodeGraph.getCurrentInstance().getNodes());
        assertEquals(nodeGraph.getSegment(0), nodeGraph.getCurrentInstance().getSegment(0));
    }

    @Test
    public void getSize() {
        assertEquals(nodes.size(), nodeGraph.getSize());
        verify(nodes, atLeastOnce()).size();
    }

    @Test
    public void setSegmentDB() {
        SegmentDB s = mock(SegmentDB.class);
        when(s.getSegment(anyInt())).thenReturn("otherSegment");
        assertEquals("Segment", nodeGraph.getSegment(0));
        nodeGraph.setSegmentDB(s);
        assertEquals("otherSegment", nodeGraph.getSegment(0));
    }

    @Test
    public void getNodes() {
        assertEquals(nodes, nodeGraph.getNodes());
    }

    @Test
    public void generateDrawNodes() {
        ArrayList<Node> nodes2 = mock(new ArrayList<Node>().getClass());
        when(nodes2.size()).thenReturn(1);
        when(nodes2.get(anyInt())).thenReturn(node);
        nodeGraph = new NodeGraph(nodes2, segmentDB, new LinkedList<>(), new LinkedList<>());
        nodeGraph.generateDrawNodes(0, 500);
        assertEquals(1, nodeGraph.getDrawNodes().size());
        verify(node, atLeastOnce()).getIncomingEdges();
        verify(node, atLeastOnce()).getOutgoingEdges();
        assertEquals(0, nodeGraph.getDrawNodes().get(0).getIndex());
    }

    @Test
    public void getDrawNodes() {
        assertEquals(drawNodes, nodeGraph.getDrawNodes());
    }


    @Test
    public void getDrawNode() {
        assertEquals(drawNode, nodeGraph.getDrawNode(0));
    }

    @Test
    public void getDummyNodes() {
        assertEquals(dummyNodes, nodeGraph.getDummyNodes());
    }

    @Test
    public void addEdges() {

    }

    @Test
    public void topoSort() {

    }

    @Test
    public void topoSortUtil() {

    }

    @Test
    public void assignLayers() {

    }

    @Test
    public void computeDummyNodes() {

    }

    @Test
    public void verticalSpacing() {

    }

    @Test
    public void retrieveEdgeNodes() {

    }

    @Test
    public void retrieveDrawNodes() {

    }

    @Test
    public void retrieveDummies() {

    }

}
