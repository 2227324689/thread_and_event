package com.example.demo.chains;

import com.example.demo.events.NotifyCenter;
import com.example.demo.events.types.FileMatchEvent;
import com.example.demo.utils.FileUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 风骚的Mic 老师
 * create-date: 2020/5/27-22:11
 * 保存数据
 */
public class TxtProcessor extends Thread implements RequestProcessor {
    RequestProcessor nextProcessor;

    //存储请求数据
    BlockingQueue<Request> requests=new LinkedBlockingDeque<>();

    volatile boolean finished=false;

    private static final String PROCESSOR_TXT_FILE_EXT=".md";

    public TxtProcessor(RequestProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void run() {
         while(!finished){
             try {
                 Request request=requests.take();
                 if(FileUtils.isExtension(request.getFileName(),PROCESSOR_TXT_FILE_EXT)){
                    //发布事件
                     NotifyCenter.publishEvent(new FileMatchEvent(request.getFileName()));
                 }
                 nextProcessor.processRequest(request);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
    }
    @Override
    public void processRequest(Request request) {
        requests.add(request); //添加到阻塞队列
    }

    @Override
    public void shutdown() {
        System.out.println("TxtProcessor begin shutdown");
        finished=true;
        requests.clear();
        if(nextProcessor!=null) {
            nextProcessor.shutdown();
        }
    }
}
