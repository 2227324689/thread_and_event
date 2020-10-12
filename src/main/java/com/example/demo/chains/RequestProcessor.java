package com.example.demo.chains;


public interface RequestProcessor {

    void processRequest(Request request);

    void shutdown();
}
