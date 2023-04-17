package com.alfastore.lpbpda.v2;

import java.util.ArrayList;

public class ORMSrtJln {
    private String srtJln;
    private String faktur;
    private String orderDate;
    private String statusProses;

    public ORMSrtJln(){}

    public ORMSrtJln(String srtJln,String faktur,String orderDate,String statusProses){
        this.srtJln = srtJln;
        this.faktur = faktur;
        this.orderDate = orderDate;
        this.statusProses = statusProses;
    }

    public String getSrtJln(){
        return srtJln;
    }

    public void setSrtJln(String srtJln){
        this.srtJln = srtJln;
    }

    public String getFaktur(){
        return faktur;
    }

    public void setFaktur(String faktur){
        this.faktur = faktur;
    }

    public String getOrderDate(){
        return orderDate;
    }

    public void setOrderDate(String orderDate){
        this.orderDate = orderDate;
    }

    public String getStatusProses(){
        return statusProses;
    }

    public void setStatusProses(String statusProses){
        this.statusProses = statusProses;
    }


    private static int lastContactId = 0;

    public static ArrayList<ORMSrtJln> createContactsList(int numContacts) {

        ArrayList<ORMSrtJln> contacts = new ArrayList<ORMSrtJln>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new ORMSrtJln("SJ- " + ++lastContactId, "F-" + ++lastContactId,  "2022-03-01", "F"));
        }

        return contacts;
    }
}
