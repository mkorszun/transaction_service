package com.number26.controller;

import com.google.gson.annotations.SerializedName;
import com.number26.model.Transaction;
import com.number26.service.TransactionService;
import com.number26.transformer.JsonTransformer;
import java.io.Serializable;
import spark.Request;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.put;

public class TransactionController {

    private final JsonTransformer jsonTransformer = new JsonTransformer();

    public TransactionController() {
        this(new TransactionService());
    }

    public TransactionController(TransactionService transactionService) {

        put("/transactionservice/transaction/:transaction_id", (request, response) -> {
            Transaction tr = jsonTransformer.fromJson(Transaction.class, request.body());
            transactionService.put(getTransactionID(request), tr);
            return new ResponseOK();
        }, jsonTransformer);

        get("/transactionservice/transaction/:transaction_id",
            (request, response) -> transactionService.get(getTransactionID(request)), jsonTransformer);

        get("/transactionservice/types/:type", (request, response) -> {
            String type = request.params(":type");
            return transactionService.list(type);
        }, jsonTransformer);

        get("/transactionservice/sum/:transaction_id",
            (request, response) -> new Sum(transactionService.getChildSum(getTransactionID(request))), jsonTransformer);

        exception(TransactionService.ParentTransactionNotFound.class, (e, request, response) -> {
            response.status(404);
            response.body(jsonTransformer.render(new ResponseNOK("parent transaction not found")));
        });

        exception(TransactionService.TransactionServiceException.class, (e, request, response) -> {
            response.status(503);
            response.body(jsonTransformer.render(new ResponseNOK("transaction service not available")));
        });

        exception(InvalidTransactionIdException.class, (e, request, response) -> {
            response.status(400);
            response.body(jsonTransformer.render(new ResponseNOK("transaction id should be long value")));
        });

        exception(TransactionService.TransactionAlreadyExists.class, (e, request, response) -> {
            response.status(409);
            response.body(jsonTransformer.render(new ResponseNOK("transaction with such an id already exists")));
        });

        exception(Exception.class, (e, request, response) -> {
            response.status(500);
            response.body(jsonTransformer.render(new ResponseNOK("internal error")));
        });
    }

    private long getTransactionID(Request request) throws InvalidTransactionIdException {
        try {
            return Long.parseLong(request.params(":transaction_id"));
        } catch (NumberFormatException e) {
            throw new InvalidTransactionIdException();
        }
    }

    public class Sum implements Serializable {
        @SerializedName("sum") protected double sum;

        public Sum(double sum) {
            this.sum = sum;
        }
    }

    public class Response implements Serializable {
        @SerializedName("status") protected String status;
    }

    public class ResponseOK extends Response {
        public ResponseOK() {
            this.status = "ok";
        }
    }

    public class ResponseNOK extends Response {
        @SerializedName("reason") public String reason;

        public ResponseNOK(String reason) {
            this.status = "error";
            this.reason = reason;
        }
    }

    class InvalidTransactionIdException extends Exception {
    }
}

