package com.number26.service;

import com.number26.model.Transaction;
import com.number26.node.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionService {

    private Map<Long, Transaction> transactions = new ConcurrentHashMap<>();
    private Set<Node> nodes = new HashSet<>();

    public List<Long> list(String type) {
        return transactions.entrySet()
            .stream()
            .filter(p -> type.equals(p.getValue().getType()))
            .map(p -> p.getKey())
            .collect(Collectors.toList());
    }

    public Transaction get(long id) {
        return transactions.get(id);
    }

    public void put(long id, Transaction transaction) throws TransactionServiceException {

        if (transactions.containsKey(id)) throw new TransactionAlreadyExists();

        Node node = null;
        Long parentID = transaction.getParentID();
        Transaction parent = parentID != null ? get(parentID) : null;

        if (parentID == null || parent != null) {
            transactions.put(id, transaction);
            node = new Node(id, transaction.getAmount());
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
            return children.stream().mapToDouble(n -> n.amount).sum();
        } else {
            throw new ParentTransactionNotFound();
        }
    }

    public void reset() {
        transactions.clear();
        nodes.clear();
    }

    public static class TransactionServiceException extends Exception {
    }

    public static class ParentTransactionNotFound extends TransactionServiceException {
    }

    public static class TransactionAlreadyExists extends TransactionServiceException {
    }
}


