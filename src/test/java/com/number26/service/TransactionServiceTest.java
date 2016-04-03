package com.number26.service;

import com.number26.model.Transaction;
import junitx.framework.ListAssert;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TransactionServiceTest {

    //================================================================================================================//
    // Create transaction
    //================================================================================================================//

    @org.junit.Test
    public void shouldCreateNewTransactionWithoutParentTransaction() throws Exception {
        TransactionService service = new TransactionService();
        service.put(1, new Transaction(10, "type1"));

        Transaction result = service.get(1);
        assertEquals(10d, result.getAmount());
        assertEquals("type1", result.getType());
        assertNull(result.getParentID());
    }

    @org.junit.Test
    public void shouldCreateNewTransactionLinkedToParentTransaction() throws Exception {
        TransactionService service = new TransactionService();
        service.put(1, new Transaction(10, "type1"));
        service.put(2, new Transaction(10, "type1", 1));

        Transaction result = service.get(2);
        assertEquals(10d, result.getAmount());
        assertEquals("type1", result.getType());
        assertEquals(Long.valueOf(1), result.getParentID());
    }

    @org.junit.Test(expected = TransactionService.ParentTransactionNotFound.class)
    public void shouldThrowExceptionWhenCreatingNewTransactionWithNonExistingParentId() throws Exception {
        TransactionService service = new TransactionService();
        service.put(1, new Transaction(10, "type1", 123));
    }

    @org.junit.Test(expected = TransactionService.TransactionAlreadyExists.class)
    public void shouldThrowExceptionWhenCreatingNewTransactionWithExistingId() throws Exception {
        TransactionService service = new TransactionService();
        service.put(1, new Transaction(10, "type1"));
        service.put(1, new Transaction(10, "type1"));
    }

    //================================================================================================================//
    // Get transaction
    //================================================================================================================//

    @org.junit.Test
    public void shouldReturnTransaction() throws Exception {
        TransactionService service = new TransactionService();
        service.put(4, new Transaction(13, "type2"));
        service.put(5, new Transaction(14, "type3", 4));

        Transaction result = service.get(5);

        assertEquals(result.getAmount(), 14d);
        assertEquals(result.getType(), "type3");
        assertEquals(result.getParentID(), Long.valueOf(4));
    }

    @org.junit.Test
    public void shouldReturnNullTransactionForNonExistingTransactionId() throws Exception {
        TransactionService service = new TransactionService();
        assertNull(service.get(101));
    }

    //================================================================================================================//
    // List transactions
    //================================================================================================================//

    @org.junit.Test
    public void shouldReturnTransactionListForType() throws Exception {
        TransactionService service = new TransactionService();
        service.put(1, new Transaction(10, "type1"));
        service.put(2, new Transaction(11, "type1"));
        service.put(3, new Transaction(12, "type1"));
        service.put(4, new Transaction(13, "type2"));
        service.put(5, new Transaction(14, "type3"));

        ListAssert.assertEquals(service.list("type1"), asList(1L, 2L, 3L));
        assertEquals(service.list("type2"), asList(4L));
        assertEquals(service.list("type3"), asList(5L));
    }

    @org.junit.Test
    public void shouldReturnEmptyListForNonExistingType() throws Exception {
        TransactionService service = new TransactionService();
        service.put(4, new Transaction(13, "type2"));
        service.put(5, new Transaction(14, "type3"));
        assertEquals(service.list("type0"), asList());
    }

    @org.junit.Test
    public void shouldReturnEmptyListForEmptyType() throws Exception {
        TransactionService service = new TransactionService();
        service.put(4, new Transaction(13, "type2"));
        service.put(5, new Transaction(14, "type3"));
        assertEquals(service.list(""), asList());
    }

    @org.junit.Test
    public void shouldReturnEmptyListForEmptyDatabase() throws Exception {
        TransactionService service = new TransactionService();
        assertEquals(service.list("type0"), asList());
    }

    //================================================================================================================//
    // Sum linked transactions
    //================================================================================================================//

    @org.junit.Test
    public void shouldReturnTransactionSumLinkedToParentTransaction() throws Exception {
        TransactionService service = new TransactionService();
        service.put(1, new Transaction(10, "type1"));
        service.put(2, new Transaction(11, "type1"));
        service.put(3, new Transaction(12, "type1", 1));
        service.put(4, new Transaction(13, "type2", 1));
        service.put(5, new Transaction(14, "type3", 1));
        service.put(6, new Transaction(14, "type3", 3));

        assertEquals(53.0, service.getChildSum(1));
        assertEquals(0.0, service.getChildSum(2));
    }

    @org.junit.Test(expected = TransactionService.ParentTransactionNotFound.class)
    public void shouldThrowExceptionWhenNonExistingParentTransaction() throws Exception {
        TransactionService service = new TransactionService();
        service.getChildSum(101);
    }

    //================================================================================================================//
    //================================================================================================================//
    //================================================================================================================//
}