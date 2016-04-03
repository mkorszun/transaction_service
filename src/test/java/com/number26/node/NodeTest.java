package com.number26.node;

import com.number26.model.Transaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class NodeTest {

    @org.junit.Test
    public void shouldReturnNode() throws Exception {
        Set<Node> forest = buildForest();
        assertEquals(4L, Node.find(4, forest).id);
        assertEquals(5L, Node.find(5, forest).id);
    }

    @org.junit.Test
    public void shouldReturnNullWhenNodeNotPresent() throws Exception {
        Set<Node> forest = buildForest();
        assertNull(Node.find(44, forest));
        assertNull(Node.find(55, forest));
        assertNull(Node.find(55, Collections.<Node>emptySet()));
    }

    @org.junit.Test
    public void shouldReturnChildNodes() throws Exception {
        List<Node> traversed1 = new ArrayList();
        List<Node> traversed2 = new ArrayList();
        Node.traverse(buildTree1(), traversed1);
        Node.traverse(buildTree2(), traversed2);

        assertEquals(6, traversed1.size());
        assertEquals(2, traversed1.get(0).id);
        assertEquals(3, traversed1.get(1).id);
        assertEquals(4, traversed1.get(2).id);
        assertEquals(5, traversed1.get(3).id);
        assertEquals(6, traversed1.get(4).id);
        assertEquals(7, traversed1.get(5).id);
        assertEquals(1, traversed2.size());
        assertEquals(9, traversed2.get(0).id);
    }

    @org.junit.Test
    public void shouldReturnNodesForType() throws Exception {
        List<Node> found1 = new ArrayList();
        List<Node> found2 = new ArrayList();

        Node.find("type1", buildForest(), found1);
        Node.find("type3", buildForest(), found2);

        assertEquals(2, found1.size());
        assertEquals(1, found1.get(0).id);
        assertEquals(2, found1.get(1).id);
        assertEquals(3, found2.size());
        assertEquals(5, found2.get(0).id);
        assertEquals(6, found2.get(1).id);
        assertEquals(7, found2.get(2).id);
    }

    //================================================================================================================//
    // Helpers
    //================================================================================================================//

    private static final Node buildTree1() {
        Node n1 = new Node(1, new Transaction(10, "type1"));
        Node n2 = new Node(2, new Transaction(11, "type1"));
        Node n3 = new Node(3, new Transaction(12, "type2"));
        Node n4 = new Node(4, new Transaction(13, "type2"));
        Node n5 = new Node(5, new Transaction(14, "type3"));
        Node n6 = new Node(6, new Transaction(15, "type3"));
        Node n7 = new Node(7, new Transaction(16, "type3"));
        n4.addChild(n5, n6, n7);
        n3.addChild(n4);
        n1.addChild(n2, n3);
        return n1;
    }

    private static final Node buildTree2() {
        Node n8 = new Node(8, new Transaction(17, "type4"));
        Node n9 = new Node(9, new Transaction(18, "type4"));
        n8.addChild(n9);
        return n8;
    }

    private static final HashSet<Node> buildForest() {
        return new HashSet(asList(buildTree1(), buildTree2()));
    }

    //================================================================================================================//
    //================================================================================================================//
    //================================================================================================================//
}