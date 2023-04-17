package com.alfastore.lpbpda.v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FakturActivity extends AppCompatActivity {
    RecyclerView RVSrtJln;
    EditText ETCariSrtJln;
    Button BtnSrtJln;
    Context context;
    String tag = new PublicVariable().tag();
    String urlTablet = new PublicVariable().url_tablet();
    DatabaseHandler db;
    LinearLayout LLProgress,LLFakturDet;
    String storeId,DCId;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faktur);
        RVSrtJln =  findViewById(R.id.RVSrtJln);
        ETCariSrtJln = findViewById(R.id.ETCariSrtJln);
        BtnSrtJln = findViewById(R.id.BtnSrtJln);
        db = new DatabaseHandler(getApplicationContext());
        LLProgress = findViewById(R.id.LLProgress);

        context = FakturActivity.this;

        LLProgress.setVisibility(View.INVISIBLE);
        db.DeleteAllSrtJln();
        db.DeleteAllFakturDet();
        getStoreId();
        getFaktur();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("fakturDet"));

        BtnSrtJln.setOnClickListener(v -> {
            if(ETCariSrtJln.getText().toString().equals("")){
                List<ORMSrtJln> srtJlns = db.disSrtJln();
                if(srtJlns.size() >0){
                    AdapterSuratJalan adapter = new AdapterSuratJalan(srtJlns);
                    RVSrtJln.setAdapter(adapter);
                    RVSrtJln.setLayoutManager(new LinearLayoutManager(context));
                }
            }else{
                List<ORMSrtJln> srtJlns = db.getSrtJln2(ETCariSrtJln.getText().toString());
                if(srtJlns.size() >0){
                    AdapterSuratJalan adapter = new AdapterSuratJalan(srtJlns);
                    RVSrtJln.setAdapter(adapter);
                    RVSrtJln.setLayoutManager(new LinearLayoutManager(context));
                }else{
                    Toast.makeText(context, "Surat jalan tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan BACK sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void getStoreId(){
        try{
            List<ORMMsStore> listMSStore = db.GetStore();
            for (ORMMsStore b : listMSStore) {
                DCId = b.getDcId();
                storeId = b.getStoreId();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag, "onResume FakturActivity");
        getFaktur();

        /*List<ORMSrtJln> srtJlns = db.disSrtJln();
        if(srtJlns.size() >0){
            AdapterSuratJalan adapter = new AdapterSuratJalan(srtJlns);
            RVSrtJln.setAdapter(adapter);
            RVSrtJln.setLayoutManager(new LinearLayoutManager(context));
        }*/
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String faktur = intent.getStringExtra("faktur");
            getFakturDet(faktur);
        }
    };

    public void getFakturDet(String faktur){
        try{
            //final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
            String url = urlTablet + "TotalFaktur/?storeId="+storeId+"&faktur="+faktur;
            Log.d("MD", "kiriman : " + url);
            Request request = new Request.Builder()
                    .url(url)
                    //.post(body)
                    .build();
            LLProgress.setVisibility(View.VISIBLE);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                    runOnUiThread(() ->  LLProgress.setVisibility(View.INVISIBLE));
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myResponse = Objects.requireNonNull(response.body()).string();
                        Log.d(tag, myResponse);
                        try{
                            if(myResponse.isEmpty()){
                                runOnUiThread(() -> Toast.makeText(context, "EMPTY RESPONSE", Toast.LENGTH_LONG).show());
                            }else{
                                runOnUiThread(() -> {
                                    try{
                                        android.app.AlertDialog.Builder Layout = new android.app.AlertDialog.Builder(context);
                                        LayoutInflater inflater = FakturActivity.this.getLayoutInflater();
                                        final android.app.AlertDialog alertDialog = Layout.create();
                                        final ViewGroup nullParent = null;
                                        alertDialog.setView(inflater.inflate(R.layout.lay_surat_jalan_det, nullParent), 0, 0, 0, 0);
                                        alertDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertDialog.setCancelable(true);
                                        alertDialog.show();
                                        LLFakturDet = alertDialog.findViewById(R.id.LLFakturDet);
                                        LLFakturDet.removeAllViews();
                                        JSONArray dataResult = new JSONArray(myResponse);
                                        int n = dataResult.length();
                                        String[] k01 = new String[n];
                                        String[] k02 = new String[n];
                                        for (int i = 0; i < n; i++) {
                                            JSONObject data = dataResult.getJSONObject(i);

                                            k01[i] = data.getString("faktur");
                                            k02[i] = "Rp. " + currencyFormat(data.getDouble("total_faktur"));

                                            final TextView rtv01 = new TextView(FakturActivity.this);
                                            final TextView rtv02 = new TextView(FakturActivity.this);

                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(0,90,1);

                                            params.topMargin=1;
                                            final LinearLayout llHor = new LinearLayout(FakturActivity.this);
                                            llHor.setLayoutParams(params);
                                            llHor.setOrientation(LinearLayout.HORIZONTAL);

                                            rtv01.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                                            rtv01.setText(k01[i]);
                                            rtv01.setSingleLine();
                                            rtv01.setHorizontallyScrolling(true);
                                            rtv01.setEllipsize(TextUtils.TruncateAt.END);
                                            rtv01.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                                            params_1.setMarginEnd(1);
                                            rtv01.setLayoutParams(params_1);
                                            llHor.addView(rtv01);

                                            rtv02.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                                            rtv02.setText(k02[i]);
                                            rtv02.setHorizontallyScrolling(true);
                                            rtv02.setEllipsize(TextUtils.TruncateAt.END);
                                            rtv02.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                                            rtv02.setSingleLine();
                                            rtv02.setLayoutParams(params_1);
                                            llHor.addView(rtv02);

                                            LLFakturDet.addView(llHor);
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                        }
                        runOnUiThread(() ->  LLProgress.setVisibility(View.INVISIBLE));
                    }else{
                        try{
                            final String myResponse = Objects.requireNonNull(response.body()).string();
                            Log.d(tag, myResponse);
                            JSONObject obj = new JSONObject(myResponse);
                            String message = obj.getString("message");
                            runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
                        }catch (Exception e){
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                            runOnUiThread(() ->  LLProgress.setVisibility(View.INVISIBLE));
                        }
                    }
                    runOnUiThread(() ->  LLProgress.setVisibility(View.INVISIBLE));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
        }
    }

    public void getFaktur(){
        try{
            //final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
            String url = urlTablet + "get_faktur/?storeId="+storeId+"&filter=";
            /*JSONObject json = new JSONObject();
            json.put("storeid", "TD46");
            json.put("filter", "");*/
            Log.d("MD", "kiriman : " + url);
            //RequestBody body = RequestBody.create(String.valueOf(json), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    //.post(body)
                    .build();
            LLProgress.setVisibility(View.VISIBLE);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                    runOnUiThread(() ->  LLProgress.setVisibility(View.INVISIBLE));
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myResponse = Objects.requireNonNull(response.body()).string();
                        Log.d(tag, myResponse);
                        try{
                            if(myResponse.isEmpty()){
                                runOnUiThread(() -> Toast.makeText(context, "EMPTY RESPONSE", Toast.LENGTH_LONG).show());
                            }else{
                                db.DeleteAllSrtJln();
                                JSONArray dataResult = new JSONArray(myResponse);
                                int n = dataResult.length();
                                for (int i = 0; i < n; i++) {
                                    JSONObject data = dataResult.getJSONObject(i);
                                    db.InsertSrtJln(new ORMSrtJln(data.getString("faktur"),data.getString("faktur_sji"),data.getString("order_date"),data.getString("status_proses")));
                                }
                                runOnUiThread(() -> {
                                    List<ORMSrtJln> srtJlns = db.disSrtJln();
                                    //List<ORMSrtJln> srtJlns = db.GetSrtJln();
                                    //Log.d(tag, "SIZE : " + srtJlns.size());
                                    if(srtJlns.size() >0){
                                        AdapterSuratJalan adapter = new AdapterSuratJalan(srtJlns);
                                        RVSrtJln.setAdapter(adapter);
                                        RVSrtJln.setLayoutManager(new LinearLayoutManager(context));
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                        }
                        runOnUiThread(() ->  LLProgress.setVisibility(View.INVISIBLE));
                    }else{
                        try{
                            final String myResponse = Objects.requireNonNull(response.body()).string();
                            Log.d(tag, myResponse);
                            JSONObject obj = new JSONObject(myResponse);
                            String message = obj.getString("message");
                            runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
                        }catch (Exception e){
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                            runOnUiThread(() ->  LLProgress.setVisibility(View.INVISIBLE));
                        }
                    }
                    runOnUiThread(() ->  LLProgress.setVisibility(View.INVISIBLE));
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
        }
    }

    public String currencyFormat(Double rp){
        NumberFormat formatter = new DecimalFormat("#,###");
        return formatter.format(rp);
    }
}