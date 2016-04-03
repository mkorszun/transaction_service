package com.number26.service;

import com.number26.model.Transaction;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionService {

    private Map<Long, Transaction> transactions = new ConcurrentHashMap<>();

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

    public void put(long id, Transaction transaction) throws ParentTransactionNotFound {
        Long parentID = transaction.getParentID();
        Transaction parent = parentID != null ? get(parentID) : null;

        if (parentID == null || parent != null) {
            transactions.put(id, transaction);
        }

        if (parent != null) {
            parent.addChildAmount(transaction.getAmount());
        }

        if (parentID != null && parent == null) {
            throw new ParentTransactionNotFound();
        }
    }

    public double getChildSum(long id) throws ParentTransactionNotFound {
        Transaction parent = get(id);

        if (parent != null) {
            return parent.getChildAmount();
        } else {
            throw new ParentTransactionNotFound();
        }
    }

    public static class TransactionServiceException extends Exception {
    }

    public static class ParentTransactionNotFound extends TransactionServiceException {
    }
}


