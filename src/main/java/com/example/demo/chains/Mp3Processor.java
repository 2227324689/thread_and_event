package com.example.demo.chains;

import com.example.demo.events.NotifyCenter;
import com.example.demo.events.types.FileMatchEvent;
import com.example.demo.utils.FileUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 风骚的Mic 老师
 * create-date: 2020/5/27-22:10
 * 保存数据
 */
public class Mp3Processor extends Thread implements RequestProcessor{

    RequestProcessor nextProcessor;

    //存储请求数据
    BlockingQueue<Request> requests=new LinkedBlockingDeque<>();

    volatile boolean finished=false;

    private static final String PROCESSOR_MP3_FILE_EXT=".mp3";

    public Mp3Processor(RequestProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void run() {
        while(!finished||!Thread.currentThread().isInterrupted()){
            try {
                Request request=requests.take(); //阻塞式的获取请求
                if(FileUtils.isExtension(request.getFileName(),PROCESSOR_MP3_FILE_EXT)){
                    NotifyCenter.publishEvent(new FileMatchEvent(request.getFileName()));
                }
                nextProcessor.processRequest(request); //传递给下一个处理器
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processRequest(Request request) {
        requests.add(request); //生产消息
    }

    @Override
    public void shutdown() {
        //
        System.out.println("Mp3Processor begin shutdown");
        finished=true;
        requests.clear();
        if(nextProcessor!=null) {
            nextProcessor.shutdown();
        }
    }
}
