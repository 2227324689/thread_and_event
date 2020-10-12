package com.example.demo.chains;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 风骚的Mic 老师
 * create-date: 2020/5/27-22:11
 */
public class FinalRequestProcessor implements RequestProcessor{

    @Override
    public void processRequest(Request request) {
//        System.out.println("End the Processor");
    }

    @Override
    public void shutdown() {
        System.out.println("shutdown of request processor complete");
    }
}
