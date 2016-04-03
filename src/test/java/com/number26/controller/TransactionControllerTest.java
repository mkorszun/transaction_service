package com.number26.controller;

import com.number26.model.Transaction;
import com.number26.service.TransactionService;
import com.number26.utils.HTTPClient;
import com.number26.utils.HTTPClientFactory;
import org.junit.AfterClass;
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

    @org.junit.Test
    public void shouldCreateTransaction() throws Exception {
        TransactionController.Response resp = client.create("1", new Transaction(100, "type1"));
        Transaction result = service.get(1);

        assertEquals("ok", resp.status);
        assertEquals(100d, result.getAmount());
        assertEquals("type1", result.getType());
        assertNull(result.getParentID());
    }

    @org.junit.Test
    public void shouldCreateTransactionLinkedToParent() throws Exception {
        service.put(1, new Transaction(100, "type1"));
        TransactionController.Response resp = client.create("2", new Transaction(100, "type2", 1));
        Transaction result = service.get(2);

        assertEquals("ok", resp.status);
        assertEquals(100d, result.getAmount());
        assertEquals("type2", result.getType());
        assertEquals(Long.valueOf(1), result.getParentID());
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
        client.create("1", new Transaction(100, "type2", 123));
    }

    @org.junit.Test
    public void shouldReturnTransaction() throws Exception {
        service.put(1, new Transaction(111, "type3"));
        Transaction transaction = client.read("1");

        assertEquals(111d, transaction.getAmount());
        assertEquals("type3", transaction.getType());
        assertNull(transaction.getParentID());
    }

    @org.junit.Test
    public void shouldReturnNullTransactionForNonExistingTransactionId() throws Exception {
        assertNull(client.read("12"));
    }

    @org.junit.Test
    public void shouldReturnListOfIdsForType() throws Exception {
        service.put(1, new Transaction(100, "myType"));
        service.put(2, new Transaction(100, "myType"));
        service.put(3, new Transaction(100, "myType"));

        assertEquals(asList(1, 2, 3), client.list("myType"));
    }

    @org.junit.Test
    public void shouldReturnEmptyListOfIdsForNonExistingType() throws Exception {
        assertEquals(asList(), client.list("ghost"));
    }

    @org.junit.Test
    public void shouldReturnSumLinkedToParentTransaction() throws Exception {
        service.put(1, new Transaction(10, "type1"));
        service.put(2, new Transaction(11, "type1"));
        service.put(3, new Transaction(12, "type1", 1));
        service.put(4, new Transaction(13, "type2", 1));
        service.put(5, new Transaction(14, "type3", 1));

        assertEquals(39d, client.sum("1").sum);
    }

    @org.junit.Test
    public void shouldFailToReturnSumWhenNonExistingParentTransaction() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("parent transaction not found");
        client.sum("456");
    }

    @org.junit.Test
    public void shouldFailToReturnSumWhenWrongParentTransactionIdFormat() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("transaction id should be long value");
        client.sum("abc");
    }
}