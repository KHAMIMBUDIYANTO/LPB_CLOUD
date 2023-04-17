package com.alfastore.lpbpda.v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InputContainerFinalActivity extends AppCompatActivity {
    DatabaseHandler db;
    Context context;
    String tag = new PublicVariable().tag();
    TextView qtyPoKuning,qtyInKuning,qtyPoMerah,qtyInMerah,qtyPoMini,qtyInMini,TVSrtJln;
    Button BtnClear,BtnFinish,BtnOk;
    LinearLayout LLProgress,LLTotRp;
    String srtJln="",fakturNoKutip="",DCId,storeId;
    String urlTablet = new PublicVariable().url_tablet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_container_final);
        context = InputContainerFinalActivity.this;
        qtyPoKuning = findViewById(R.id.qtyPoKuning);
        qtyInKuning = findViewById(R.id.qtyInKuning);
        qtyPoMerah = findViewById(R.id.qtyPoMerah);
        qtyInMerah = findViewById(R.id.qtyInMerah);
        qtyPoMini = findViewById(R.id.qtyPoMini);
        qtyInMini = findViewById(R.id.qtyInMini);
        BtnClear = findViewById(R.id.BtnClear);
        BtnFinish = findViewById(R.id.BtnFinish);
        db = new DatabaseHandler(getApplicationContext());
        LLProgress = findViewById(R.id.LLProgress);

        LLProgress.setVisibility(View.INVISIBLE);

        getStoreId();

        try{
            Intent intent = getIntent();
            srtJln = intent.getStringExtra("srtJln");
            fakturNoKutip = intent.getStringExtra("faktur");
            qtyInMerah.setText(intent.getStringExtra("iMerah"));
            qtyInKuning.setText(intent.getStringExtra("iKuning"));
            qtyInMini.setText(intent.getStringExtra("iMini"));
            qtyPoKuning.setText(intent.getStringExtra("poKuning"));
            qtyPoMerah.setText(intent.getStringExtra("poMerah"));
            qtyPoMini.setText(intent.getStringExtra("poMini"));
        }catch (Exception e){
            e.printStackTrace();
        }

        BtnClear.setOnClickListener(v -> {
            finish();
        });

        BtnFinish.setOnClickListener(v -> {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
            String url = urlTablet + "SaveContainer/?storeId="+storeId+"&faktur="+fakturNoKutip + "&nik="+getNik()+"&qMerah="+qtyInMerah.getText().toString()+"&qKuning="+qtyInKuning.getText().toString()+"&qMini="+qtyInMini.getText().toString();
            JSONObject json = new JSONObject();

            Log.d("MD", "kiriman : " + url);
            Log.d("MD", "body : " + json);
            RequestBody body = RequestBody.create("", JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
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
                                runOnUiThread(() -> viewTotalRp(myResponse));
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

            //Intent intentx = new Intent(context, FinalisasiActivity.class);
            //intent.putExtra("srtJln", srtJln);
            //intent.putExtra("faktur", fakturNoKutip);
            //startActivity(intentx);
        });
    }

    public void viewTotalRp(String resp){
        try{
            android.app.AlertDialog.Builder Layout = new android.app.AlertDialog.Builder(context);
            LayoutInflater inflater = InputContainerFinalActivity.this.getLayoutInflater();
            final android.app.AlertDialog alertDialog = Layout.create();
            final ViewGroup nullParent = null;
            alertDialog.setView(inflater.inflate(R.layout.lay_total_rp, nullParent), 0, 0, 0, 0);
            alertDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(true);
            alertDialog.show();

            TVSrtJln = alertDialog.findViewById(R.id.TVSrtJln);
            BtnOk = alertDialog.findViewById(R.id.BtnOk);
            LLTotRp = alertDialog.findViewById(R.id.LLTotRp);
            LLTotRp.removeAllViews();

            TVSrtJln.setText(srtJln);

            BtnOk.setOnClickListener(v -> {
                Intent intentx = new Intent(context, FinalisasiActivity.class);
                intentx.putExtra("srtJln", srtJln);
                intentx.putExtra("faktur", fakturNoKutip);
                startActivity(intentx);
            });

            JSONArray dataResult = new JSONArray(resp);
            int n = dataResult.length();
            String[] k01 = new String[n];
            String[] k02 = new String[n];
            for (int i = 0; i < n; i++) {
                JSONObject data = dataResult.getJSONObject(i);
                k01[i] = data.getString("faktur");
                k02[i] = "Rp. " + currencyFormat(data.getDouble("total_faktur"));
                final TextView rtv01 = new TextView(context);
                final TextView rtv02 = new TextView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(0, 90,1);
                LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams(0, 90,1);
                params.topMargin=1;
                final LinearLayout llHor = new LinearLayout(InputContainerFinalActivity.this);
                llHor.setLayoutParams(params);
                llHor.setOrientation(LinearLayout.HORIZONTAL);

                rtv01.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv01.setText(k01[i]);
                rtv01.setSingleLine();
                rtv01.setHorizontallyScrolling(true);
                rtv01.setEllipsize(TextUtils.TruncateAt.END);
                rtv01.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginStart(convertDpToPixel(5));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtv01.setLayoutParams(params_1);
                llHor.addView(rtv01);

                rtv02.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv02.setText(k02[i]);
                rtv02.setHorizontallyScrolling(true);
                rtv02.setEllipsize(TextUtils.TruncateAt.END);
                rtv02.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_2.setMarginStart(convertDpToPixel(0));
                params_2.setMarginEnd(convertDpToPixel(5));
                rtv02.setSingleLine();
                rtv02.setLayoutParams(params_2);
                llHor.addView(rtv02);
                LLTotRp.addView(llHor);
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


    public String getNik(){
        return  ((PublicVariable) this.getApplication()).getNik();
    }

    public String currencyFormat(Double rp){
        NumberFormat formatter = new DecimalFormat("#,###");
        return formatter.format(rp);
    }
}