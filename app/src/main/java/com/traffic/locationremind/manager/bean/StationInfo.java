package com.traffic.locationremind.manager.bean;


public class StationInfo {

    public static String ID = "id";
    public static String LINEID = "lineid";
    public static String PM = "pm";
    public static String CNAME = "cname";
    public static String PNAME = "pname";
    public static String ANAME = "aname";
    public static String LOT = "lot";
    public static String LAT = "lat";

    public int id;
    public String lineid;//线路id
    public String pm;
    public String cname;//站台名
    public String pname;//站台英文名
    public String aname;//站台名简称
    public double lot;//进度
    public double lat;//纬度

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public double getLot() {
        return lot;
    }

    public void setLot(double lot) {
        this.lot = lot;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
