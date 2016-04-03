package com.number26.utils;

import com.number26.controller.TransactionController;
import com.number26.model.Transaction;
import java.util.List;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface HTTPClient {

    @PUT("/transactionservice/transaction/{transaction_id}") TransactionController.Response create(
        @Path("transaction_id") String id,
        @Body Transaction event) throws Exception;

    @GET("/transactionservice/transaction/{transaction_id}") Transaction read(
        @Path("transaction_id") String id) throws Exception;

    @GET("/transactionservice/types/{type}") List<Integer> list(@Path("type") String type) throws Exception;

    @GET("/transactionservice/sum/{transaction_id}") TransactionController.Sum sum(@Path("transaction_id") String id)
        throws Exception;
}
