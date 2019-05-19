package com.zixuan.elf;

public class Music {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuther() {
        return author;
    }

    public void setAuther(String auther) {
        this.author = auther;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    Music(long id,String name ,String author,String picUrl){
        this.author = author;
        this.id = id;
        this.name = name;
        this.picUrl = picUrl;
    }
    private long id = 0;
    private String name = null;
    private String author = null;
    private String picUrl = null;
}
