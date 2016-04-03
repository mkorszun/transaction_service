package com.number26.controller;

import com.number26.model.Transaction;
import com.number26.service.TransactionService;
import com.number26.utils.HTTPClient;
import com.number26.utils.HTTPClientFactory;
import junitx.framework.ListAssert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static spark.Spark.awaitInitialization;
import static spark.Spark.port;
import static spark.Spark.stop;

public class TransactionControllerTest {

    private static HTTPClient client = HTTPClientFactory.build();
    private static TransactionService service = new TransactionService();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass public static void setupCls() {
        port(HTTPClientFactory.TEST_PORT);
        new TransactionController(service);
        awaitInitialization();
    }

    @AfterClass public static void cleanCls() {
        stop();
    }

    @Before public void setup() throws TransactionService.TransactionServiceException {
        service.put(1, new Transaction(10, "type1"));
        service.put(2, new Transaction(11, "type1"));
        service.put(3, new Transaction(12, "type1", 1));
        service.put(4, new Transaction(13, "type2", 1));
        service.put(5, new Transaction(14, "type3", 1));
        service.put(6, new Transaction(14, "type3", 3));
    }

    @After public void clean() {
        service.reset();
    }

    //================================================================================================================//
    // Create transaction
    //================================================================================================================//

    @org.junit.Test
    public void shouldCreateTransaction() throws Exception {
        TransactionController.Response resp = client.create("101", new Transaction(100, "type101"));
        Transaction result = service.get(101);

        assertEquals("ok", resp.status);
        assertEquals(100d, result.getAmount());
        assertEquals("type101", result.getType());
        assertNull(result.getParentID());
    }

    @org.junit.Test
    public void shouldCreateTransactionLinkedToParent() throws Exception {
        TransactionController.Response resp = client.create("202", new Transaction(100, "type202", 2));
        Transaction result = service.get(202);

        assertEquals("ok", resp.status);
        assertEquals(100d, result.getAmount());
        assertEquals("type202", result.getType());
        assertEquals(Long.valueOf(2), result.getParentID());
    }

    @org.junit.Test
    public void shouldFailToCreateTransactionWhenWrongTransactionIdFormat() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("transaction id should be long value");
        client.create("abc", new Transaction(100, "type2"));
    }

    @org.junit.Test
    public void shouldFailToCreateTransactionWhenNonExistingParentTransactionId() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("parent transaction not found");
        client.create("101", new Transaction(100, "type2", 123));
    }

    @org.junit.Test
    public void shouldFailToCreateTransactionWhenTransactionIdAlreadyPresent() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("transaction with such an id already exists");
        client.create("1", new Transaction(100, "type2"));
    }

    //================================================================================================================//
    // Get transaction
    //================================================================================================================//

    @org.junit.Test
    public void shouldReturnTransaction() throws Exception {
        Transaction transaction = client.read("1");

        assertEquals(10d, transaction.getAmount());
        assertEquals("type1", transaction.getType());
        assertNull(transaction.getParentID());
    }

    @org.junit.Test
    public void shouldReturnNullTransactionForNonExistingTransactionId() throws Exception {
        assertNull(client.read("12"));
    }

    //================================================================================================================//
    // List transactions
    //================================================================================================================//

    @org.junit.Test
    public void shouldReturnTransactionListForType() throws Exception {
        ListAssert.assertEquals(asList(1, 2, 3), client.list("type1"));
    }

    @org.junit.Test
    public void shouldReturnEmptyListForNonExistingType() throws Exception {
        assertEquals(asList(), client.list("ghost"));
    }

    //================================================================================================================//
    // Sum linked transactions
    //================================================================================================================//

    @org.junit.Test
    public void shouldReturnTransactionSumLinkedToParentTransaction() throws Exception {
        assertEquals(53d, client.sum("1").sum);
    }

    @org.junit.Test
    public void shouldFailToReturnTransactionSumWhenNonExistingParentTransaction() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("parent transaction not found");
        client.sum("456");
    }

    @org.junit.Test
    public void shouldFailToReturnTransactionSumWhenWrongParentTransactionIdFormat() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("transaction id should be long value");
        client.sum("abc");
    }

    //================================================================================================================//
    //================================================================================================================//
    //================================================================================================================//
}