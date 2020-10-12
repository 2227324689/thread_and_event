package com.example.demo.events;

import com.example.demo.events.listener.Subscriber;
import com.example.demo.events.types.FileMatchEvent;
import com.example.demo.utils.FileUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 咕泡学院，只为更好的你
 * 咕泡学院-Mic: 2082233439
 * http://www.gupaoedu.com
 * 处理订阅事件
 **/
public class EventProcessor {

    public static Map<String,AtomicInteger> FILE_CATEGORY=new ConcurrentHashMap<>();

    public EventProcessor(){
        NotifyCenter.registerToPublisher(FileMatchEvent.class, NotifyCenter.ringBufferSize);

        NotifyCenter.registerSubscriber(new Subscriber() {
            @Override
            public void onEvent(Event event) {
                //处理监听事件,记录总的处理文件数量
                FileMatchEvent fileMatchEvent=(FileMatchEvent)event;
                String ext=FileUtils.getExtension(fileMatchEvent.getFileName());
                AtomicInteger fileRecords=FILE_CATEGORY.get(ext);
                if(fileRecords==null){
                    AtomicInteger atomicInteger=new AtomicInteger(1);
                    FILE_CATEGORY.putIfAbsent(ext,atomicInteger);
                }else{
                    fileRecords.getAndIncrement();
                }
            }
            @Override
            public Class<? extends Event> subscribeType() {
                return FileMatchEvent.class;
            }
        });
    }
}
