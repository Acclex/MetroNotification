package com.traffic.locationremind.manager.bean;


public class LineInfo {
    public static String ID = "id";
    public static String LINEID = "lineid";
    public static String LINENAME = "linename";
    public static String LINEINFO = "lineinfo";

    public String lineid;//线路id
    public String linename;//线路名
    public String lineinfo;//线路信息

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }

    public String getLinename() {
        return linename;
    }

    public void setLinename(String linename) {
        this.linename = linename;
    }

    public String getLineinfo() {
        return lineinfo;
    }

    public void setLineinfo(String lineinfo) {
        this.lineinfo = lineinfo;
    }

}
