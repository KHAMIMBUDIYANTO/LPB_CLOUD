package com.alfastore.lpbpda.v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanSelisihActivity extends AppCompatActivity {
    DatabaseHandler db;
    LinearLayout ll011;
    Button BtnClear,BtnFinish;
    Context context;
    String tag = new PublicVariable().tag();
    String urlTablet = new PublicVariable().url_tablet();
    LinearLayout LLProgress;
    String jsonRes,srtJln="",fakturNoKutip="",kontainer="",DCId,storeId;

    /*StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
            .detectLeakedClosableObjects()
            .detectLeakedSqlLiteObjects()
            .penaltyDeath()
            .penaltyLog()
            .build();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_selisih);
        context = ScanSelisihActivity.this;
        ll011 = findViewById(R.id.ll011);
        BtnClear = findViewById(R.id.BtnClear);
        BtnFinish = findViewById(R.id.BtnFinish);
        LLProgress = findViewById(R.id.LLProgress);
        db = new DatabaseHandler(getApplicationContext());
        LLProgress.setVisibility(View.INVISIBLE);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new
                                                    ViewTreeObserver.OnScrollChangedListener() {
                                                        @Override
                                                        public void onScrollChanged() {

                                                            if (!scrollView.canScrollVertically(1)) {
                                                                // bottom of scroll view
//                                                                createLayoutpage();
                                                            }
                                                            if (!scrollView.canScrollVertically(-1)) {
                                                                // top of scroll view
                                                            }
                                                        }
                                                    });

        //dipake buat debug sqlite
        //StrictMode.setVmPolicy(policy);

        Intent intent = getIntent();
        jsonRes = ScanActivity.tmp_datares;
        srtJln = intent.getStringExtra("srtJln");
        fakturNoKutip = intent.getStringExtra("faktur");
        kontainer= intent.getStringExtra("kontainer");
        getStoreId();
        if(!jsonRes.equals("")){
            createLayout(jsonRes);
        }

        BtnClear.setOnClickListener(v -> {
            finish();
        });

        BtnFinish.setOnClickListener(v -> {
            //save_final
            //https://mobile-svc-stg-dot-sis-stagging.et.r.appspot.com/tablet/lpb/SaveFinal/?storeId=TD46&faktur=TC22023960,TC220239601,TC220239602&kontainer=K100&statCon=T
            try{
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                //OkHttpClient client = new OkHttpClient();
                OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
                String url = urlTablet + "SaveFinal/?storeId="+storeId+"&faktur="+fakturNoKutip + "&kontainer="+kontainer+"&statCon="+getStatCnt();
                Log.d("MD", "kiriman : " + url);
                JSONObject json = new JSONObject();
                json.put("stat_cnt", getStatCnt());

                RequestBody body = RequestBody.create(String.valueOf(json),JSON);
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
                                            if(kontainer.equals("ALL")){
                                                finish();
                                                Intent intent = new Intent(context, InputContainerActivity.class);
                                                intent.putExtra("srtJln", srtJln);
                                                intent.putExtra("faktur", fakturNoKutip);
                                                startActivity(intent);
                                            }else{
                                                finish();
                                                Intent intent = new Intent(context, ScanActivity.class);
                                                intent.putExtra("srtJln", srtJln);
                                                intent.putExtra("faktur", fakturNoKutip);
                                                startActivity(intent);
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
                                String message = obj.getString("errMsg");
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
        });
    }

    public void createLayout(String res){
        try{
            JSONArray dataResult = new JSONArray(res);
            int n = dataResult.length();
            String[] kSrtJln = new String[n];
            String[] k01 = new String[n];
            String[] k02 = new String[n];
            String[] k03 = new String[n];
            String[] k04 = new String[n];
            String[] k05 = new String[n];
            String[] k06 = new String[n];
            for (int i = 0; i < n; i++) {
                JSONObject data = dataResult.getJSONObject(i);

                kSrtJln[i] = srtJln;
                k01[i] = data.getString("kontainer");
                k02[i] = data.getString("faktur");
                k03[i] = data.getString("plu");
                k04[i] = data.getString("descp");
                k05[i] = data.getString("qty_scan");
                k06[i] = data.getString("qty_ship");

                final TextView rtvSrtJln = new TextView(context);
                final TextView rtv01 = new TextView(context);
                final TextView rtv02 = new TextView(context);
                final TextView rtv03 = new TextView(context);
                final TextView rtv04 = new TextView(context);
                final TextView rtv05 = new TextView(context);
                final TextView rtv06 = new TextView(context);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(convertDpToPixel(150), 90);
                LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams(convertDpToPixel(100), 90);
                LinearLayout.LayoutParams params_3 = new LinearLayout.LayoutParams(convertDpToPixel(70), 90);

                params.topMargin = 1;
                final LinearLayout llHor = new LinearLayout(context);
                llHor.setLayoutParams(params);
                llHor.setOrientation(LinearLayout.HORIZONTAL);

                rtvSrtJln.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtvSrtJln.setText(kSrtJln[i]);
                rtvSrtJln.setSingleLine();
                rtvSrtJln.setEllipsize(TextUtils.TruncateAt.END);
                rtvSrtJln.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtvSrtJln.setLayoutParams(params_1);
                llHor.addView(rtvSrtJln);

                rtv01.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv01.setText(k01[i]);
                rtv01.setSingleLine();
                rtv01.setEllipsize(TextUtils.TruncateAt.END);
                rtv01.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_2.setMarginEnd(convertDpToPixel(1));
                rtv01.setLayoutParams(params_2);
                llHor.addView(rtv01);

                rtv02.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv02.setText(k02[i]);
                rtv02.setEllipsize(TextUtils.TruncateAt.END);
                rtv02.setSingleLine();
                rtv02.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtv02.setLayoutParams(params_1);
                llHor.addView(rtv02);

                rtv03.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv03.setText(k03[i]);
                rtv03.setSingleLine();
                rtv03.setEllipsize(TextUtils.TruncateAt.END);
                rtv03.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtv03.setLayoutParams(params_1);
                llHor.addView(rtv03);

                rtv04.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv04.setText(k04[i]);
                rtv04.setSingleLine();
                rtv04.setEllipsize(TextUtils.TruncateAt.END);
                rtv04.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtv04.setLayoutParams(params_1);
                llHor.addView(rtv04);

                rtv05.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv05.setText(k05[i]);
                rtv05.setSingleLine();
                rtv05.setEllipsize(TextUtils.TruncateAt.END);
                rtv05.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_3.setMarginEnd(convertDpToPixel(1));
                rtv05.setLayoutParams(params_3);
                llHor.addView(rtv05);

                rtv06.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv06.setText(k06[i]);
                rtv06.setSingleLine();
                rtv06.setEllipsize(TextUtils.TruncateAt.END);
                rtv06.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_3.setMarginEnd(convertDpToPixel(1));
                rtv06.setLayoutParams(params_3);
                llHor.addView(rtv06);

                ll011.addView(llHor);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void createLayoutpage(){
        try{
            JSONArray dataResult = new JSONArray(jsonRes);
            int n = dataResult.length();
            String[] kSrtJln = new String[n];
            String[] k01 = new String[n];
            String[] k02 = new String[n];
            String[] k03 = new String[n];
            String[] k04 = new String[n];
            String[] k05 = new String[n];
            String[] k06 = new String[n];
            for (int i = 101; i < 200; i++) {
                JSONObject data = dataResult.getJSONObject(i);

                kSrtJln[i] = srtJln;
                k01[i] = data.getString("kontainer");
                k02[i] = data.getString("faktur");
                k03[i] = data.getString("plu");
                k04[i] = data.getString("descp");
                k05[i] = data.getString("qty_scan");
                k06[i] = data.getString("qty_ship");

                final TextView rtvSrtJln = new TextView(context);
                final TextView rtv01 = new TextView(context);
                final TextView rtv02 = new TextView(context);
                final TextView rtv03 = new TextView(context);
                final TextView rtv04 = new TextView(context);
                final TextView rtv05 = new TextView(context);
                final TextView rtv06 = new TextView(context);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(convertDpToPixel(150), 90);
                LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams(convertDpToPixel(100), 90);
                LinearLayout.LayoutParams params_3 = new LinearLayout.LayoutParams(convertDpToPixel(70), 90);

                params.topMargin = 1;
                final LinearLayout llHor = new LinearLayout(context);
                llHor.setLayoutParams(params);
                llHor.setOrientation(LinearLayout.HORIZONTAL);

                rtvSrtJln.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtvSrtJln.setText(kSrtJln[i]);
                rtvSrtJln.setSingleLine();
                rtvSrtJln.setEllipsize(TextUtils.TruncateAt.END);
                rtvSrtJln.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtvSrtJln.setLayoutParams(params_1);
                llHor.addView(rtvSrtJln);

                rtv01.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv01.setText(k01[i]);
                rtv01.setSingleLine();
                rtv01.setEllipsize(TextUtils.TruncateAt.END);
                rtv01.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_2.setMarginEnd(convertDpToPixel(1));
                rtv01.setLayoutParams(params_2);
                llHor.addView(rtv01);

                rtv02.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv02.setText(k02[i]);
                rtv02.setEllipsize(TextUtils.TruncateAt.END);
                rtv02.setSingleLine();
                rtv02.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtv02.setLayoutParams(params_1);
                llHor.addView(rtv02);

                rtv03.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv03.setText(k03[i]);
                rtv03.setSingleLine();
                rtv03.setEllipsize(TextUtils.TruncateAt.END);
                rtv03.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtv03.setLayoutParams(params_1);
                llHor.addView(rtv03);

                rtv04.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv04.setText(k04[i]);
                rtv04.setSingleLine();
                rtv04.setEllipsize(TextUtils.TruncateAt.END);
                rtv04.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtv04.setLayoutParams(params_1);
                llHor.addView(rtv04);

                rtv05.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv05.setText(k05[i]);
                rtv05.setSingleLine();
                rtv05.setEllipsize(TextUtils.TruncateAt.END);
                rtv05.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_3.setMarginEnd(convertDpToPixel(1));
                rtv05.setLayoutParams(params_3);
                llHor.addView(rtv05);

                rtv06.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv06.setText(k06[i]);
                rtv06.setSingleLine();
                rtv06.setEllipsize(TextUtils.TruncateAt.END);
                rtv06.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_3.setMarginEnd(convertDpToPixel(1));
                rtv06.setLayoutParams(params_3);
                llHor.addView(rtv06);

                ll011.addView(llHor);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
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

    public String getStatCnt(){
        return  ((PublicVariable) this.getApplication()).getStatCnt();
    }
}