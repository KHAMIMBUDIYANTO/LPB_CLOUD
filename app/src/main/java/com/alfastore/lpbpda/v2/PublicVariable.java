package com.alfastore.lpbpda.v2;

import android.app.Application;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class PublicVariable extends Application {

    public String statCnt, nik,faktur;

    public String tag(){
        return "IR";
    }

    public String url_sis(){
        //stagging
        return "https://store-api-gateway-dot-sis-stagging.et.r.appspot.com/api/sis/";
        //production
//        return "https://api.alfastore.co.id/api/mob";
    }


    public String url_tablet(){
        //stagging
        return "https://mobile-svc-stg-dot-sis-stagging.et.r.appspot.com/tablet/lpb/";

        //production
//        return "https://mobile-svc-dot-sis-pos-android-sat-production.et.r.appspot.com/tablet/lpb/";
//        return "https://api.alfastore.co.id/api/mob/tablet/lpb/";
    }

    public String getStatCnt(){
        return statCnt;
    }

    public void setStatCnt(String statCnt){
        this.statCnt = statCnt;
    }

    public String getNik(){return  nik;}

    public void setNik(String nik){this.nik=nik;}


    public static  String removeLeadingZeroes(String str) {
        String strPattern = "^0+(?!$)";
        str = str.replaceAll(strPattern, "");
        return str;
    }

    public static String hapusbelakangbarcode(String barcode){
        return barcode.substring(0,barcode.length()-1);
    }

}
