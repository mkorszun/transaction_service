package com.number26;

import com.number26.controller.TransactionController;

import static spark.Spark.get;

public class Main {

    public static void main(String[] args) {
        get("/", (request, response) -> "OK");
        new TransactionController();
    }
}