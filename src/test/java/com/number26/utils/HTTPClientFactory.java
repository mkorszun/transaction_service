package com.number26.utils;

import retrofit.RestAdapter;

public class HTTPClientFactory {

    public static final int TEST_PORT = 4141;

    public static HTTPClient build() {
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setErrorHandler(new HTTPErrorHandler())
            .setEndpoint("http://localhost:" + TEST_PORT)
            .build();

        return restAdapter.create(HTTPClient.class);
    }
}
