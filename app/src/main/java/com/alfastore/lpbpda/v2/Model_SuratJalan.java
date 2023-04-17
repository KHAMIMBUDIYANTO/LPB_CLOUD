package com.alfastore.lpbpda.v2;

import java.util.ArrayList;

public class Model_SuratJalan {
    private String msuratJalan,mfaktur,mtotalFaktur,mtglPo,mstatus;

    public Model_SuratJalan(String suratJalan, String faktur, String totalFaktur, String tglPo,String status) {
        msuratJalan = suratJalan;
        mfaktur = faktur;
        mtotalFaktur = totalFaktur;
        mtglPo = tglPo;
        mstatus = status;
    }

    public String getSuratJalan() {
        return msuratJalan;
    }

    public String getFaktur() {
        return mfaktur;
    }

    public String getTotalFaktur() {
        return mtotalFaktur;
    }

    public String getTglPo() {
        return mtglPo;
    }

    public String getStatus() {
        return mstatus;
    }

    private static int lastContactId = 0;

    public static ArrayList<Model_SuratJalan>createSrtJln(){
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Model_SuratJalan> createContactsList(int numContacts) {

        ArrayList<Model_SuratJalan> contacts = new ArrayList<Model_SuratJalan>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new Model_SuratJalan("SJ- " + ++lastContactId, "F-" + ++lastContactId, "200.000", "2022-03-01", "F"));
        }

        return contacts;
    }
}
