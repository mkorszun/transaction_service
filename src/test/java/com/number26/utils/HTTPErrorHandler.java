package com.number26.utils;

import com.number26.controller.TransactionController;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class HTTPErrorHandler implements ErrorHandler {

    @Override public Throwable handleError(RetrofitError cause) {
        try {
            TransactionController.ResponseNOK error =
                (TransactionController.ResponseNOK) cause.getBodyAs(TransactionController.ResponseNOK.class);
            return new Exception(error.reason);
        } catch (Exception e) {
            return new Exception("unknown error");
        }
    }
}
