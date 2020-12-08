package com.example.note;
/*
 *
 * Name: Leo Yao
 * ID: 16287261
 *
 * */

//note class
public class NoteObj {
    private long id;
    private  String content;
    private String path;
    private String time;


    public NoteObj() {
    }

    public NoteObj(String content, String time, String path) {
        this.content = content;
        this.time = time;
        this.path=path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path+"\n"+content+"\n"+time.substring(5,16)+" "+id;
    }
}
