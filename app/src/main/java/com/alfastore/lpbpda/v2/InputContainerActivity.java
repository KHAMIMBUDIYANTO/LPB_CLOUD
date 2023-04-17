package com.alfastore.lpbpda.v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class InputContainerActivity extends AppCompatActivity {
    DatabaseHandler db;
    Context context;
    String tag = new PublicVariable().tag();
    String urlTablet = new PublicVariable().url_tablet();
    EditText ETKuning,ETMerah,ETMini;
    Button BtnClear,BtnFinish;
    LinearLayout LLProgress;
    String srtJln="",fakturNoKutip="",DCId,storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_container);
        context = InputContainerActivity.this;
        ETKuning = findViewById(R.id.ETKuning);
        ETMerah = findViewById(R.id.ETMerah);
        ETMini = findViewById(R.id.ETMini);
        BtnClear = findViewById(R.id.BtnClear);
        BtnFinish = findViewById(R.id.BtnFinish);
        LLProgress = findViewById(R.id.LLProgress);
        db = new DatabaseHandler(getApplicationContext());
        LLProgress.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        srtJln = intent.getStringExtra("srtJln");
        fakturNoKutip = intent.getStringExtra("faktur");
        getStoreId();
        BtnClear.setOnClickListener(v -> {
            finish();
        });

        BtnFinish.setOnClickListener(v -> {
            if(ETMerah.getText().toString().equals("")){
                Toast.makeText(context,"KONTAINER MERAH TIDAK BOLEH KOSONG", Toast.LENGTH_SHORT).show();
                return;
            }
            if(ETKuning.getText().toString().equals("")){
                Toast.makeText(context,"KONTAINER KUNING TIDAK BOLEH KOSONG", Toast.LENGTH_SHORT).show();
                return;
            }
            if(ETMini.getText().toString().equals("")){
                Toast.makeText(context,"KONTAINER MINI TIDAK BOLEH KOSONG", Toast.LENGTH_SHORT).show();
                return;
            }
            //localhost:5000/tablet/lpb/CompareContainer/?storeId=TD46&faktur=TC22023960,TC220239601,TC220239602&nik=12345678&qMerah=2&qKuning=2&qMini=2
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
            String url = urlTablet + "CompareContainer/?storeId="+storeId+"&faktur="+fakturNoKutip + "&nik=12345678&qMerah="+ETMerah.getText().toString()+"&qKuning="+ETKuning.getText().toString()+"&qMini="+ETMini.getText().toString();
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
                                JSONObject json = new JSONObject(myResponse);
                                String poKuning,poMerah,poMini;
                                poKuning = json.getString("qty_kuning");
                                poMerah = json.getString("qty_merah");
                                poMini = json.getString("qty_mini");
                                Intent intentx = new Intent(context, InputContainerFinalActivity.class);
                                intentx.putExtra("srtJln", srtJln);
                                intentx.putExtra("faktur", fakturNoKutip);
                                intentx.putExtra("iMerah", ETMerah.getText().toString());
                                intentx.putExtra("iKuning", ETKuning.getText().toString());
                                intentx.putExtra("iMini", ETMini.getText().toString());
                                intentx.putExtra("poKuning", poKuning);
                                intentx.putExtra("poMerah", poMerah);
                                intentx.putExtra("poMini", poMini);
                                startActivity(intentx);
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
        });
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
}