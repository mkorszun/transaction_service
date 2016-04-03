package com.number26.service;

import com.number26.model.Transaction;
import com.number26.node.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TransactionService {

    private Set<Node> nodes = new HashSet<>();

    public List<Long> list(String type) {
        List<Node> found = new LinkedList<>();
        Node.find(type, nodes, found);
        return found.stream().map(p -> p.id).collect(Collectors.toList());
    }

    public Transaction get(long id) {
        Node node = Node.find(id, nodes);
        return node != null ? node.transaction : null;
    }

    public void put(long id, Transaction transaction) throws TransactionServiceException {

        if (Node.find(id, nodes) != null) throw new TransactionAlreadyExists();

        Node node = null;
        Long parentID = transaction.getParentID();
        Transaction parent = parentID != null ? get(parentID) : null;

        if (parentID == null || parent != null) {
            node = new Node(id, transaction);
        }

        if (parent != null) {
            Node.find(parentID, nodes).addChild(node);
        } else if (parentID == null && parent == null) {
            nodes.add(node);
        } else {
            throw new ParentTransactionNotFound();
        }
    }

    public double getChildSum(long id) throws ParentTransactionNotFound {
        Node parent = Node.find(id, nodes);

        if (parent != null) {
            List<Node> children = new ArrayList<>();
            Node.traverse(parent, children);
            return children.stream().mapToDouble(n -> n.transaction.getAmount()).sum();
        } else {
            throw new ParentTransactionNotFound();
        }
    }

    public void reset() {
        nodes.clear();
    }

    public static class TransactionServiceException extends Exception {
    }

    public static class ParentTransactionNotFound extends TransactionServiceException {
    }

    public static class TransactionAlreadyExists extends TransactionServiceException {
    }
}


