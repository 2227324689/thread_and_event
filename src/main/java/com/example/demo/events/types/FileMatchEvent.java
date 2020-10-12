package com.example.demo.events.types;

import com.example.demo.events.Event;

/**
 * 咕泡学院，只为更好的你
 * 咕泡学院-Mic: 2082233439
 * http://www.gupaoedu.com
 **/
public class FileMatchEvent extends Event{

    private String fileName;

    public FileMatchEvent() {
    }

    public FileMatchEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FileMatchEvent{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}
