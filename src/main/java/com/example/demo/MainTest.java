package com.example.demo;

import com.example.demo.chains.*;
import com.example.demo.events.EventProcessor;

import java.io.File;

/**
 * 咕泡学院，只为更好的你
 * 咕泡学院-Mic: 2082233439
 * http://www.gupaoedu.com
 **/
public class MainTest {
    private RequestProcessor firstProcessor;

    private void setupRequestProcessor(){
        RequestProcessor finalProcessor=new FinalRequestProcessor();
        RequestProcessor mp3Processor=new Mp3Processor(finalProcessor);
        ((Mp3Processor)mp3Processor).start();
        RequestProcessor docProcessor=new PdfProcessor(mp3Processor);
        ((PdfProcessor)docProcessor).start();
        firstProcessor=new TxtProcessor(docProcessor);
        ((TxtProcessor)firstProcessor).start();
    }
    public void startup(){
        setupRequestProcessor();
    }
    public void shutdown(){
        firstProcessor.shutdown();
    }


    public static void main(String[] args) throws InterruptedException {
        EventProcessor eventProcessor=new EventProcessor();
        //读取文件
        MainTest mainTest=new MainTest();
        mainTest.startup();
        //读取数据
        File file = new File("D:\\电子书籍");
        File[] fs = file.listFiles();
        for(File f:fs){
            if(!f.isDirectory()){
                Request request=new Request(f.getName());
                mainTest.firstProcessor.processRequest(request);
            }
        }
    }
}
