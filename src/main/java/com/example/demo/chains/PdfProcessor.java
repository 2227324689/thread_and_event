package com.example.demo.chains;

import com.example.demo.events.NotifyCenter;
import com.example.demo.events.types.FileMatchEvent;
import com.example.demo.utils.FileUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 咕泡学院，只为更好的你
 * 咕泡学院-Mic: 2082233439
 * http://www.gupaoedu.com
 **/
public class PdfProcessor extends Thread implements RequestProcessor{
    RequestProcessor nextProcessor;

    //存储请求数据
    BlockingQueue<Request> requests=new LinkedBlockingDeque<>();

    volatile boolean finished=false;

    private static final String PROCESSOR_DOC_FILE_EXT=".pdf";

    public PdfProcessor(RequestProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void run() {
        while(!finished||!Thread.currentThread().isInterrupted()){
            try {
                Request request=requests.take();
                if(FileUtils.isExtension(request.getFileName(),PROCESSOR_DOC_FILE_EXT)){
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
