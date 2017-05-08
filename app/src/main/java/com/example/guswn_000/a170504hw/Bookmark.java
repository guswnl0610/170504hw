package com.example.guswn_000.a170504hw;

/**
 * Created by guswn_000 on 2017-05-08.
 */

public class Bookmark
{
    private String name;
    private String url;

    public Bookmark(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
