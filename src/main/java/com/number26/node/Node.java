package com.number26.node;

import com.number26.model.Transaction;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Node {

    public long id;
    public Transaction transaction;
    Set<Node> children = new LinkedHashSet<>();

    public Node(long id, Transaction transaction) {
        this.id = id;
        this.transaction = transaction;
    }

    public void addChild(Node... nodes) {
        for (Node n : nodes) {
            children.add(n);
        }
    }

    public static Node find(long id, Set<Node> nodes) {
        for (Node n : nodes) {
            if (id == n.id) return n;
            Node node = find(id, n.children);
            if (node != null) return node;
        }
        return null;
    }

    public static void find(String type, Set<Node> nodes, List<Node> found) {
        for (Node n : nodes) {
            if (type.equals(n.transaction.getType())) found.add(n);
            find(type, n.children, found);
        }
    }

    public static void traverse(Node n, List<Node> visited) {
        for (Node child : n.children) {
            visited.add(child);
            traverse(child, visited);
        }
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node n = (Node) obj;
            return n.id == this.id;
        }
        return false;
    }
}
