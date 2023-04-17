package com.alfastore.lpbpda.v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import okhttp3.Response;

public class FinalisasiActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    String[] ket={"BTA (Barang Tidak Ada)","SJI (Surat Jalan Internal - barang lebih kirim)","BK (Barang Kurang)","CNT (Kontainer)","BKC (Barang Kurang Kontainer)"};
    Context context;
    DatabaseHandler db;
    Spinner SPKet;
    Button BtnOk,BtnListPo;
    TextView TVJmlPO,TVSrtJln;
    LinearLayout LLProgress,ll015,LLFakturDet;
    String tag = new PublicVariable().tag();
    String urlTablet = new PublicVariable().url_tablet();
    String fakturNoKutip,srtJln,DCId,storeId;

    /*StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
            .detectLeakedClosableObjects()
            .detectLeakedSqlLiteObjects()
            .penaltyDeath()
            .penaltyLog()
            .build();*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalisasi);
        SPKet = findViewById(R.id.SPKet);
        BtnOk = findViewById(R.id.BtnOk);
        LLProgress = findViewById(R.id.LLProgress);
        ll015 = findViewById(R.id.ll015);
        BtnListPo = findViewById(R.id.BtnListPo);
        TVJmlPO = findViewById(R.id.TVJmlPO);
        TVSrtJln = findViewById(R.id.TVSrtJln);
        context = FinalisasiActivity.this;
        //dipake buat debug sqlite
        //StrictMode.setVmPolicy(policy);
        db = new DatabaseHandler(getApplicationContext());

        Intent intent = getIntent();
        srtJln = intent.getStringExtra("srtJln");
        fakturNoKutip = intent.getStringExtra("faktur");

        TVSrtJln.setText(srtJln);
        Cursor res = db.distinctFaktur(srtJln);
        TVJmlPO.setText(String.valueOf(res.getCount()));
        res.close();

        LLProgress.setVisibility(View.INVISIBLE);
        getStoreId();

        SPKet.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item,ket);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        SPKet.setAdapter(aa);

        BtnListPo.setOnClickListener(v -> {
            android.app.AlertDialog.Builder Layout = new android.app.AlertDialog.Builder(context);
            LayoutInflater inflater = FinalisasiActivity.this.getLayoutInflater();
            final android.app.AlertDialog alertDialog = Layout.create();
            final ViewGroup nullParent = null;
            alertDialog.setView(inflater.inflate(R.layout.lay_list_po, nullParent), 0, 0, 0, 0);
            alertDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(true);
            alertDialog.show();
            LLFakturDet = alertDialog.findViewById(R.id.LLFakturDet);
            LLFakturDet.removeAllViews();

            Cursor resx = db.distinctFaktur(srtJln);
            String faktur;
            resx.moveToFirst();
            while(!resx.isAfterLast()){
                faktur = resx.getString(resx.getColumnIndexOrThrow("faktur"));
                final TextView rtv01 = new TextView(FinalisasiActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(0,90,1);

                params.topMargin=1;
                final LinearLayout llHor = new LinearLayout(FinalisasiActivity.this);
                llHor.setLayoutParams(params);
                llHor.setOrientation(LinearLayout.HORIZONTAL);

                rtv01.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv01.setText(faktur);
                rtv01.setSingleLine();
                rtv01.setHorizontallyScrolling(true);
                rtv01.setEllipsize(TextUtils.TruncateAt.END);
                rtv01.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(1);
                rtv01.setLayoutParams(params_1);
                llHor.addView(rtv01);

                LLFakturDet.addView(llHor);
                resx.moveToNext();
            }
            resx.close();
        });

        BtnOk.setOnClickListener(v -> {
            db.DeleteAllFakturDet();
            Intent intentx = new Intent(context, FakturActivity.class);
            finishAffinity();
            startActivity(intentx);
        });
    }

    @Override
    public void onBackPressed() {
        // your code.
        Toast.makeText(context, "faktur harus dikomfirmasi",Toast.LENGTH_SHORT).show();
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        //Toast.makeText(getApplicationContext(), ket[position], Toast.LENGTH_LONG).show();
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
        String url="";
        switch (ket[position]){
            case "BTA (Barang Tidak Ada)":
                url = urlTablet + "ShowBta/?storeId="+storeId+"&faktur="+fakturNoKutip+"&nik="+getNik();
                break;
            case "SJI (Surat Jalan Internal - barang lebih kirim)":
                url = urlTablet + "ShowSji/?storeId="+storeId+"&faktur="+fakturNoKutip;
                break;
            case "BK (Barang Kurang)":
                url = urlTablet + "ShowBk/?storeId="+storeId+"&faktur="+fakturNoKutip;
                break;
            case "CNT (Kontainer)":
                url = urlTablet + "ShowCnt/?storeId="+storeId+"&faktur="+fakturNoKutip;
                break;
            case "BKC (Barang Kurang Kontainer)":
                url = urlTablet + "ShowBkc/?storeId="+storeId+"&faktur="+fakturNoKutip;
                break;
        }
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
                            runOnUiThread(() -> {
                                        ll015.removeAllViews();
                                        Toast.makeText(context, "DATA TIDAK ADA", Toast.LENGTH_LONG).show();
                                    });
                        }else{

                            runOnUiThread(() -> viewScroll(myResponse,ket[position]));
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

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    // TODO Auto-generated method stub

    }

    public void viewScroll(String resp, String mode){
        try{
            ll015.removeAllViews();
            JSONArray dataResult = new JSONArray(resp);
            int n = dataResult.length();
            String[] k01 = new String[n];
            String[] k02 = new String[n];
            String[] k03 = new String[n];
            String[] k04 = new String[n];

            final TextView faktur = new TextView(context);
            final TextView plu = new TextView(context);
            final TextView descp = new TextView(context);
            final TextView qty = new TextView(context);
            LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params_1x = new LinearLayout.LayoutParams(convertDpToPixel(100), 90);
            LinearLayout.LayoutParams params_2x = new LinearLayout.LayoutParams(convertDpToPixel(200), 90);
            LinearLayout.LayoutParams params_3x = new LinearLayout.LayoutParams(convertDpToPixel(50), 90);
            paramsx.topMargin = 1;
            final LinearLayout llHor = new LinearLayout(context);
            llHor.setLayoutParams(paramsx);
            llHor.setOrientation(LinearLayout.HORIZONTAL);

            if(mode.equals("CNT (Kontainer)")){
                faktur.setText("KONTAINER");
            }else{
                faktur.setText("FAKTUR");
            }
            faktur.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            faktur.setSingleLine();
            faktur.setEllipsize(TextUtils.TruncateAt.END);
            faktur.setBackgroundColor(Color.parseColor("#3A99BA"));
            params_1x.setMarginEnd(convertDpToPixel(1));
            faktur.setLayoutParams(params_1x);
            llHor.addView(faktur);

            plu.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            plu.setText("PLU");
            plu.setEllipsize(TextUtils.TruncateAt.END);
            plu.setSingleLine();
            plu.setBackgroundColor(Color.parseColor("#3A99BA"));
            params_1x.setMarginEnd(convertDpToPixel(1));
            plu.setLayoutParams(params_1x);
            llHor.addView(plu);

            descp.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            descp.setText("DESCP");
            descp.setSingleLine();
            descp.setEllipsize(TextUtils.TruncateAt.END);
            descp.setBackgroundColor(Color.parseColor("#3A99BA"));
            params_2x.setMarginEnd(convertDpToPixel(1));
            descp.setLayoutParams(params_2x);
            llHor.addView(descp);

            qty.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            qty.setText("QTY");
            qty.setSingleLine();
            qty.setEllipsize(TextUtils.TruncateAt.END);
            qty.setBackgroundColor(Color.parseColor("#3A99BA"));
            params_3x.setMarginEnd(convertDpToPixel(1));
            qty.setLayoutParams(params_3x);
            llHor.addView(qty);

            ll015.addView(llHor);

            for (int i = 0; i < n; i++) {
                JSONObject data = dataResult.getJSONObject(i);
                k01[i] = data.getString("descp");
                if(mode.equals("CNT (Kontainer)")){
                    k02[i] = data.getString("kontainer");
                }else{
                    k02[i] = data.getString("faktur");
                }
                k03[i] = data.getString("plu");
                if(mode.equals("CNT (Kontainer)")){
                    k04[i] = data.getString("qty");
                }else{
                    if(mode.equals("SJI (Surat Jalan Internal - barang lebih kirim)")){
                        k04[i] = data.getString("qty_sji");
                    }else{
                        k04[i] = data.getString("qty_ret");
                    }
                }


                final TextView rtv01 = new TextView(context);
                final TextView rtv02 = new TextView(context);
                final TextView rtv03 = new TextView(context);
                final TextView rtv04 = new TextView(context);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(convertDpToPixel(100), 90);
                LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams(convertDpToPixel(200), 90);
                LinearLayout.LayoutParams params_3 = new LinearLayout.LayoutParams(convertDpToPixel(50), 90);

                params.topMargin = 1;
                final LinearLayout llHorx = new LinearLayout(context);
                llHorx.setLayoutParams(params);
                llHorx.setOrientation(LinearLayout.HORIZONTAL);

                rtv01.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv01.setText(k02[i]);
                rtv01.setSingleLine();
                rtv01.setEllipsize(TextUtils.TruncateAt.END);
                rtv01.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtv01.setLayoutParams(params_1);
                llHorx.addView(rtv01);

                rtv02.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv02.setText(k03[i]);
                rtv02.setEllipsize(TextUtils.TruncateAt.END);
                rtv02.setSingleLine();
                rtv02.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_1.setMarginEnd(convertDpToPixel(1));
                rtv02.setLayoutParams(params_1);
                llHorx.addView(rtv02);

                rtv03.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv03.setText(k01[i]);
                rtv03.setSingleLine();
                rtv03.setEllipsize(TextUtils.TruncateAt.END);
                rtv03.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_2.setMarginEnd(convertDpToPixel(1));
                rtv03.setLayoutParams(params_2);
                llHorx.addView(rtv03);

                rtv04.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv04.setText(k04[i]);
                rtv04.setSingleLine();
                rtv04.setEllipsize(TextUtils.TruncateAt.END);
                rtv04.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_3.setMarginEnd(convertDpToPixel(1));
                rtv04.setLayoutParams(params_3);
                llHorx.addView(rtv04);

                ll015.addView(llHorx);
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
}