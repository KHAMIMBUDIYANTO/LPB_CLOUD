package com.alfastore.lpbpda.v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class ScanActivity extends AppCompatActivity {
    Context context;
    DatabaseHandler db;
    String tag = new PublicVariable().tag();
    String urlTablet = new PublicVariable().url_tablet();
    int convPlu = 1;
    android.app.AlertDialog alertDialog;
    int convPcs = 0,convKarton=0,convSlove=0;
    LinearLayout LLSlove,LLKarton,LLPcs,LLFakturDet,LLItem;
    TextView TVSrtJln,TVJmlPO,TVLastQty,TVDescp,TVPlu,TVAll1,TVAll2;
    Button BtnListPo,BtnPilihCon,BtnListPlu,BtnClear,BtnFinish;
    EditText ETContainer,ETPcs,ETKarton,ETSlove,ETBarcode;
    String faktur,srtJln,modeConv,kontainer="",fakturKutip,fakturNoKutip,DCId,storeId,statCnt;
    int countpage = 0;
    public static String tmp_datares="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        LLSlove = findViewById(R.id.LLSlove);
        LLKarton = findViewById(R.id.LLKarton);
        LLPcs = findViewById(R.id.LLPcs);
        LLItem = findViewById(R.id.LLItem);
        TVSrtJln = findViewById(R.id.TVSrtJln);
        TVJmlPO = findViewById(R.id.TVJmlPO);
        TVLastQty = findViewById(R.id.TVLastQty);
        TVDescp = findViewById(R.id.TVDescp);
        TVPlu = findViewById(R.id.TVPlu);
        BtnListPo = findViewById(R.id.BtnListPo);
        BtnPilihCon = findViewById(R.id.BtnPilihCon);
        BtnListPlu = findViewById(R.id.BtnListPlu);
        //BtnClear = findViewById(R.id.BtnClear);
        BtnFinish = findViewById(R.id.BtnFinish);
        ETContainer = findViewById(R.id.ETContainer);
        ETPcs = findViewById(R.id.ETPcs);
        ETKarton = findViewById(R.id.ETKarton);
        ETSlove = findViewById(R.id.ETSlove);

        ETBarcode = findViewById(R.id.ETBarcode);

        ETPcs.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                String edtChar=ETPcs.getText().toString();
                if(edtChar.length()>4)
                {
                    Toast.makeText(ScanActivity.this, "Angka yang diinput lebih dari 4 digit " , Toast.LENGTH_SHORT).show();
                    ETPcs.setText("");
                }

            }
        });

        ETKarton.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                String edtChar=ETKarton.getText().toString();
                if(edtChar.length()>2)
                {
                    Toast.makeText(ScanActivity.this, "Angka yang diinput lebih dari 2 digit " , Toast.LENGTH_SHORT).show();
                    ETKarton.setText("");
                }

            }
        });

        ETSlove.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                String edtChar=ETSlove.getText().toString();
                if(edtChar.length()>4)
                {
                    Toast.makeText(ScanActivity.this, "Angka yang diinput lebih dari 4 digit " , Toast.LENGTH_SHORT).show();
                    ETSlove.setText("");
                }

            }
        });


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new
                                                    ViewTreeObserver.OnScrollChangedListener() {
                                                        @Override
                                                        public void onScrollChanged() {

                                                            if (!scrollView.canScrollVertically(1)) {
                                                                // bottom of scroll view
                                                                Log.d("TAG", "Start: ");
                                                                if(kontainer.equals("ALL")){
                                                                    loadmore(srtJln);
                                                                }else {
                                                                }
                                                            }
                                                            if (!scrollView.canScrollVertically(-1)) {
                                                                // top of scroll view
//                                                                Log.d("TAG", "End: ");

                                                            }
                                                        }
                                                    });

        db = new DatabaseHandler(getApplicationContext());
        context = ScanActivity.this;

        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
        modeConv = "pcs";
        TVPlu.setTag("");
        TVPlu.setText("");
        getStoreId();

        ETContainer.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
                switch (keyCode){
                    case KeyEvent.KEYCODE_ENTER:
                        try{
                            if(!ETContainer.getText().toString().equals("")){
                                String cutscan = "";
                                try {
                                    String[] splitRak = ETContainer.getText().toString().split("-");
                                    cutscan = splitRak[1].toUpperCase();
                                }catch (Exception e){
                                    cutscan = ETContainer.getText().toString().toUpperCase();
                                }

                                Log.d(tag, "ETCON ENTER SCAN : " + cutscan);
                                ETContainer.setText(cutscan);
                                setViewPlu(srtJln,cutscan);
                                clearPlu();
                                kontainer = cutscan;
                            }

                            if (ETContainer.getText().toString().equals("DOS")){
                                Log.d(tag, "ETCON ENTER SCAN 1: " + ETContainer.getText().toString());
                                LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                                LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                modeConv = "karton";
                                ETKarton.setText("");

                                ETPcs.clearFocus();
                                ETKarton.requestFocus();
                                ETKarton.setCursorVisible(true);
                            }else{
                                Log.d(tag, "ETCON ENTER SCAN 2: " + ETContainer.getText().toString());
                                LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                                LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                modeConv = "pcs";
                                ETPcs.setText("");

                                ETKarton.clearFocus();
                                ETPcs.requestFocus();
                                ETPcs.setCursorVisible(true);
                            }

                            return false;


                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
            return false;
        });

        try{
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                faktur= "";
                srtJln = "";
            } else {
                faktur= extras.getString("faktur");
                String[] separated = faktur.split(",");
                int n = separated.length;
                String[] XX = new String[n];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < n; i++){
                    XX[i] = "'" + separated[i] + "'" + ",";
                    Log.d(tag, XX[i]);
                    sb.append(XX[i]);
                }
                fakturKutip = sb.substring(0, (sb.length() - 1));
                fakturNoKutip = fakturKutip.replace("'","");
                Log.d("SAT", "fakturKutip : "+fakturKutip);
                Log.d("SAT", "fakturNoKutip : "+fakturNoKutip);

                srtJln= extras.getString("srtJln");
                Log.d(tag, "SURAT JALAN : " + srtJln);
                Log.d(tag, "FAKTUR: " + faktur);
                //db.UpdateStatus(srtJln);
                TVSrtJln.setText(srtJln);

                /*Cursor res = db.distinctFaktur(srtJln);
                TVJmlPO.setText(String.valueOf(res.getCount()));
                res.close();*/
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        ETPcs.setOnFocusChangeListener((v, hasFocus) -> {
            if(modeConv.equals("karton")){
                if(!ETKarton.getText().toString().equals("")){
                    int convPlu = getConv("karton",TVPlu.getText().toString(),Integer.parseInt(ETKarton.getText().toString()));
                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                    LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                    LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    modeConv = "pcs";
                    ETKarton.setText("");
                    clearPlu();
                    setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                }else{
                    LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                    LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    modeConv = "pcs";
                }
            }else if(modeConv.equals("slove")){
                if(!ETSlove.getText().toString().equals("")){
                    int convPlu = getConv("slove",TVPlu.getText().toString(),Integer.parseInt(ETSlove.getText().toString()));
                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                    LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                    LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    modeConv = "pcs";
                    ETSlove.setText("");
                    clearPlu();
                    setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                }else{
                    LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                    LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    modeConv = "pcs";
                }
            }
        });

        ETKarton.setOnFocusChangeListener((v, hasFocus) -> {
            Log.d("TAG", "DI ED KARTON 1: "+hasFocus);
                if(modeConv.equals("pcs")){
                    if(!ETPcs.getText().toString().equals("")){
                        int convPlu = getConv("pcs",TVPlu.getText().toString(),Integer.parseInt(ETPcs.getText().toString()));
                        savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        modeConv = "karton";
                        ETPcs.setText("");
                        clearPlu();
                        setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());

                    }else{
                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        modeConv = "karton";
                        ETKarton.setText("");
                    }
                }else if(modeConv.equals("slove")){
                    if(!ETSlove.getText().toString().equals("")){
                        int convPlu = getConv("slove",TVPlu.getText().toString(),Integer.parseInt(ETSlove.getText().toString()));
                        savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                        //savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(),ETCon.getText().toString(),Integer.parseInt(ETSlove.getText().toString()));
                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        modeConv = "karton";
                        ETSlove.setText("");
                        clearPlu();
                        setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                    }else{
                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        modeConv = "karton";
                        ETKarton.setText("");
                    }
                }


        });

        ETSlove.setOnFocusChangeListener((v, hasFocus) -> {
            Log.d("TAG", "onCreate: "+modeConv);
            if(modeConv.equals("pcs")){
                if(!ETPcs.getText().toString().equals("")){
                    int convPlu = getConv("pcs",TVPlu.getText().toString(),Integer.parseInt(ETPcs.getText().toString()));
                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                    LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                    modeConv = "slove";
                    ETPcs.setText("");
                    clearPlu();
                    setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                }else{
                    LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                    modeConv = "slove";
                    ETSlove.setText("");
                }
            }else if(modeConv.equals("karton")){
                if(!ETKarton.getText().toString().equals("")){
                    int convPlu = getConv("karton",TVPlu.getText().toString(),Integer.parseInt(ETKarton.getText().toString()));
                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                    LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                    modeConv = "slove";
                    ETKarton.setText("");
                    clearPlu();
                    setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                }else{
                    LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                    LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                    modeConv = "slove";
                    ETSlove.setText("");
                }
            }
        });

        ETPcs.setOnKeyListener((v, keyCode, event) -> {

            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                if(!ETPcs.getText().toString().equals("")){
                    if(!TVPlu.getText().toString().equals("")){
                        if(ETPcs.getText().toString().length()>=4){

                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        //save plu
                                        int convPlu = getConv("pcs",TVPlu.getText().toString(),Integer.parseInt(ETPcs.getText().toString()));
                                        savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        modeConv = "pcs";
                                        ETPcs.setText("");
                                        clearPlu();
                                        setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        modeConv = "pcs";
                                        ETPcs.setText("");
                                        ETPcs.setFocusable(true);
                                        ETPcs.setFocusableInTouchMode(true);
                                        View view = ScanActivity.this.getCurrentFocus();
                                        ETPcs.requestFocus();
                                        if (view != null) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        }
                                        ETPcs.requestFocus();
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.showSoftInput(ETPcs, InputMethodManager.SHOW_IMPLICIT);
                                        break;
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(false);
                            builder.setTitle("Warning");
                            builder.setMessage("Pastikan qty yang diinput sudah benar!\n" + TVDescp.getText().toString() + " QTY : " + ETPcs.getText().toString()).setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener)
                                    .show();
                        }else{
                            int convPlu = getConv("pcs",TVPlu.getText().toString(),Integer.parseInt(ETPcs.getText().toString()));
                            savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                            LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                            LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                            LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                            modeConv = "pcs";
                            ETPcs.setText("");
                            clearPlu();
                            setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                        }
                    }
                }
                return true;
            }
            return false;
        });

        ETKarton.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                Log.d("TAG", "DI ED KARTON setOnKeyListener: ");
                // Perform action on key press
                if(!ETKarton.getText().toString().equals("")){
                    if(!TVPlu.getText().toString().equals("")){
                        if(ETKarton.getText().toString().length()>=2){
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        //save plu
                                        int convPlu = getConv("karton",TVPlu.getText().toString(),Integer.parseInt(ETKarton.getText().toString()));
                                        savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        modeConv = "karton";
                                        ETKarton.setText("");
                                        clearPlu();
                                        setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        modeConv = "karton";
                                        ETKarton.setText("");
                                        ETKarton.setFocusable(true);
                                        ETKarton.setFocusableInTouchMode(true);
                                        View view = ScanActivity.this.getCurrentFocus();
                                        ETKarton.requestFocus();
                                        if (view != null) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        }
                                        break;
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(false);
                            builder.setTitle("Warning");
                            builder.setMessage("Pastikan qty yang diinput sudah benar!\n" + TVDescp.getText().toString() + " QTY : " + ETKarton.getText().toString()).setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener)
                                    .show();
                        }else{

                            Log.d("TAG", "On klik plu: "+TVPlu.getText().toString());
                            int convPlu = getConv("karton",TVPlu.getText().toString(),Integer.parseInt(ETKarton.getText().toString()));
                            savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                            LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                            LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                            LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                            modeConv = "karton";
                            ETKarton.setText("");
                            clearPlu();
                            setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                        }
                    }
                }
                return true;
            }
            return false;
        });

        ETSlove.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                if(!ETSlove.getText().toString().equals("")){
                    if(!TVPlu.getText().toString().equals("")){
                        if(ETSlove.getText().toString().length()>=4){
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        //save plu
                                        int convPlu = getConv("slove",TVPlu.getText().toString(),Integer.parseInt(ETSlove.getText().toString()));
                                        savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                                        //savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(),ETCon.getText().toString(),Integer.parseInt(ETSlove.getText().toString()));
                                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                                        modeConv = "slove";
                                        ETSlove.setText("");
                                        clearPlu();
                                        setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                                        modeConv = "slove";
                                        ETSlove.setText("");
                                        ETSlove.setFocusable(true);
                                        ETSlove.setFocusableInTouchMode(true);
                                        View view = ScanActivity.this.getCurrentFocus();
                                        ETSlove.requestFocus();
                                        if (view != null) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        }
                                        break;
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(false);
                            builder.setTitle("Warning");
                            builder.setMessage("Pastikan qty yang diinput sudah benar!\n" + TVDescp.getText().toString() + " QTY : " + ETSlove.getText().toString()).setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener)
                                    .show();
                        }else{
                            int convPlu = getConv("slove",TVPlu.getText().toString(),Integer.parseInt(ETSlove.getText().toString()));
                            savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);

                            //savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(),ETCon.getText().toString(),Integer.parseInt(ETSlove.getText().toString()));
                            LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                            LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                            LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                            modeConv = "slove";
                            ETSlove.setText("");
                            clearPlu();
                            setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                        }
                    }
                }
                return true;
            }
            return false;
        });

        BtnListPo.setOnClickListener(v -> {
            android.app.AlertDialog.Builder Layout = new android.app.AlertDialog.Builder(context);
            LayoutInflater inflater = ScanActivity.this.getLayoutInflater();
            final android.app.AlertDialog alertDialog = Layout.create();
            final ViewGroup nullParent = null;
            alertDialog.setView(inflater.inflate(R.layout.lay_list_po, nullParent), 0, 0, 0, 0);
            alertDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(true);
            alertDialog.show();
            LLFakturDet = alertDialog.findViewById(R.id.LLFakturDet);
            LLFakturDet.removeAllViews();

            Cursor res = db.distinctFaktur(srtJln);
            String faktur;
            res.moveToFirst();
            while(!res.isAfterLast()){
                faktur = res.getString(res.getColumnIndexOrThrow("faktur"));
                final TextView rtv01 = new TextView(ScanActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(0,90,1);

                params.topMargin=1;
                final LinearLayout llHor = new LinearLayout(ScanActivity.this);
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
                res.moveToNext();
            }
            res.close();

        });

        BtnPilihCon.setOnClickListener(v -> {
            //https://mobile-svc-stg-dot-sis-stagging.et.r.appspot.com/tablet/lpb/GetLovContainer/?storeId=TD46&faktur=TC22023960,TC220239601,TC220239602&statCon=F
            try{
                //final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                //OkHttpClient client = new OkHttpClient();
                OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
                String url = urlTablet + "GetLovContainer/?storeId="+storeId+"&faktur="+fakturNoKutip +"&statCon=" +getStatCnt();
                Log.d("MD", "kiriman : " + url);
                Request request = new Request.Builder()
                        .url(url)
                        //.post(body)
                        .build();
                alert();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                        runOnUiThread(() ->  alertDialog.dismiss());
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
                                    runOnUiThread(() -> viewKontainer(myResponse));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                            }
                            runOnUiThread(() -> alertDialog.dismiss());
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
                                runOnUiThread(() -> alertDialog.dismiss());
                            }
                        }
                        runOnUiThread(() ->  alertDialog.dismiss());
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }

        });

        BtnListPlu.setOnClickListener(v -> {
            try{
                if(ETContainer.getText().toString().equals("")){
                    Toast.makeText(context, "SILAHKAN PILIH KONTAINER TERLEBIH DAHULU", Toast.LENGTH_LONG).show();
                    return;
                }
                android.app.AlertDialog.Builder Layout = new android.app.AlertDialog.Builder(context);
                LayoutInflater inflater = ScanActivity.this.getLayoutInflater();
                final android.app.AlertDialog alertDialog = Layout.create();
                final ViewGroup nullParent = null;
                alertDialog.setView(inflater.inflate(R.layout.lay_list_plu, nullParent), 0, 0, 0, 0);
                alertDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCancelable(true);
                alertDialog.show();

                LLFakturDet = alertDialog.findViewById(R.id.LLFakturDet);
                LLFakturDet.removeAllViews();
                Cursor res;
                if(kontainer.equals("ALL")){
                    res = db.KontainerAll(srtJln);
                }else{
                    res = db.Kontainerplu(srtJln,kontainer);
                }
                String plux, descpx;
                res.moveToFirst();
                while(!res.isAfterLast()){
                    plux = res.getString(res.getColumnIndexOrThrow("plu"));
                    descpx = res.getString(res.getColumnIndexOrThrow("descp"));

                    final TextView rtv01 = new TextView(ScanActivity.this);
                    final TextView rtv02 = new TextView(ScanActivity.this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(0,90,1);
                    LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams(0,90,2);

                    params.topMargin=1;
                    final LinearLayout llHor = new LinearLayout(ScanActivity.this);
                    llHor.setLayoutParams(params);
                    llHor.setOrientation(LinearLayout.HORIZONTAL);

                    rtv01.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                    rtv01.setText(plux);
                    rtv01.setSingleLine();
                    rtv01.setHorizontallyScrolling(true);
                    rtv01.setEllipsize(TextUtils.TruncateAt.END);
                    rtv01.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                    params_1.setMarginEnd(1);
                    rtv01.setLayoutParams(params_1);
                    rtv01.setOnClickListener(v1 -> {

                        Log.d("TAG", "onCreate: 1"+modeConv);
                        //cek jika plu berbeda maka muncul alert dialog save?
                        if(!TVPlu.getText().toString().equals(rtv01.getText().toString()) && !TVPlu.getText().toString().equals("")){
                            Log.d("TAG", "Masuk if: 2"+modeConv);
                            int convPlu = 1;
                            int convPcs = 0,convKarton=0,convSlove=0;
                            if(ETPcs.getText().toString().equals("")){
                                convPcs = 0;
                            }else{
                                convPcs = Integer.parseInt(ETPcs.getText().toString());
                            }
                            if(ETKarton.getText().toString().equals("")){
                                convKarton = 0;
                            }else{
                                convKarton = Integer.parseInt(ETKarton.getText().toString());
                            }
                            if(ETSlove.getText().toString().equals("")){
                                convSlove = 0;
                            }else{
                                convSlove = Integer.parseInt(ETSlove.getText().toString());
                            }
                            switch (modeConv){
                                case "pcs":
                                    convPlu = getConv("pcs",TVPlu.getText().toString(),convPcs);
                                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                    break;
                                case "karton":
                                    convPlu = getConv("karton",TVPlu.getText().toString(),convKarton);
                                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                    break;
                                case "slove":
                                    convPlu = getConv("slove",TVPlu.getText().toString(),convSlove);
                                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                    break;
                            }

                            //savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(),ETCon.getText().toString(),Integer.parseInt(ETSlove.getText().toString()));
//                            LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
//                            LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
//                            LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
//                            modeConv = "pcs";
//                            ETPcs.setText("");
                            clearPlu();

                            setPlu(rtv01.getText().toString(),srtJln,kontainer);
                            setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                        }else{
                            Log.d("TAG", "Masuk else: 2"+modeConv);
                            setPlu(rtv01.getText().toString(),srtJln,kontainer);
                        }
                        alertDialog.dismiss();
                    });
                    llHor.addView(rtv01);

                    rtv02.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                    rtv02.setText(String.valueOf(descpx));
                    rtv02.setHorizontallyScrolling(true);
                    rtv02.setEllipsize(TextUtils.TruncateAt.END);
                    rtv02.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                    rtv02.setSingleLine();
                    rtv02.setLayoutParams(params_2);
                    rtv02.setOnClickListener(v1 -> {
                        //cek jika plu berbeda maka muncul alert dialog save?
                        if(!TVPlu.getText().toString().equals(rtv01.getText().toString()) && !TVPlu.getText().toString().equals("")){
                            int convPlu = 1;
                            int convPcs = 0,convKarton=0,convSlove=0;
                            if(ETPcs.getText().toString().equals("")){
                                convPcs = 0;
                            }else{
                                convPcs = Integer.parseInt(ETPcs.getText().toString());
                            }
                            if(ETKarton.getText().toString().equals("")){
                                convKarton = 0;
                            }else{
                                convKarton = Integer.parseInt(ETKarton.getText().toString());
                            }
                            if(ETSlove.getText().toString().equals("")){
                                convSlove = 0;
                            }else{
                                convSlove = Integer.parseInt(ETSlove.getText().toString());
                            }
                            switch (modeConv){
                                case "pcs":
                                    convPlu = getConv("pcs",TVPlu.getText().toString(),convPcs);
                                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                    break;
                                case "karton":
                                    convPlu = getConv("karton",TVPlu.getText().toString(),convKarton);
                                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                    break;
                                case "slove":
                                    convPlu = getConv("slove",TVPlu.getText().toString(),convSlove);
                                    savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                    break;
                            }

//                            LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
//                            LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
//                            LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
//                            modeConv = "pcs";
//                            ETPcs.setText("");
                            clearPlu();

                            setPlu(rtv01.getText().toString(),srtJln,kontainer);
                            setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                        }else{
                            setPlu(rtv01.getText().toString(),srtJln,kontainer);
                        }
                        alertDialog.dismiss();
                    });
                    llHor.addView(rtv02);

                    LLFakturDet.addView(llHor);
                    res.moveToNext();
                }
                res.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        ETBarcode.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
                switch (keyCode){
                    case KeyEvent.KEYCODE_ENTER:
                        try{
                            if(ETContainer.getText().toString().equals("")){
                                Toast.makeText(context, "PILIH KONTAINER TERLEBIH DAHULU", Toast.LENGTH_LONG).show();
                                ETBarcode.setText("");
                                new Handler().postDelayed(() -> ETContainer.requestFocus(),0);
                                return false;
                            }

                            if(!ETBarcode.getText().toString().equals("")){
                                //cek barcode alfanumeric atau bukan
                                if(ETBarcode.getText().toString().matches(".*[A-Za-z].*")){
                                        Toast.makeText(context, "FORMAT BARCODE TIDAK SESUAI", Toast.LENGTH_SHORT).show();
                                    ETBarcode.setText("");
                                    new Handler().postDelayed(() -> ETBarcode.requestFocus(),0);
                                    return false;
                                }
                                Log.d(tag, "ETBAR ENTER SCAN : " + ETBarcode.getText().toString());
                                String barcodex = ETBarcode.getText().toString();

                                String barcodex222 = PublicVariable.hapusbelakangbarcode(PublicVariable.removeLeadingZeroes(barcodex));

                                if(!TVPlu.getTag().toString().equals(barcodex222) && !TVPlu.getText().toString().equals("")){
                                    Log.d(tag, "masuk sini 1");

                                    if(ETPcs.getText().toString().equals("")){
                                        convPcs = 0;
                                    }else{
                                        convPcs = Integer.parseInt(ETPcs.getText().toString());
                                    }
                                    if(ETKarton.getText().toString().equals("")){
                                        convKarton = 0;
                                    }else{
                                        convKarton = Integer.parseInt(ETKarton.getText().toString());
                                    }
                                    if(ETSlove.getText().toString().equals("")){
                                        convSlove = 0;
                                    }else{
                                        convSlove = Integer.parseInt(ETSlove.getText().toString());
                                    }

                                    if (barcodex222.equals(TVPlu.getTag().toString())){
                                        switch (modeConv){
                                            case "pcs":
                                                Log.d(tag, "MOde Cov : " + TVPlu.getText().toString());
                                                Log.d(tag, "MOde Cov 2: " + convPcs);
                                                convPlu = getConv("pcs",TVPlu.getText().toString(),convPcs);
                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                break;
                                            case "karton":
                                                convPlu = getConv("karton",TVPlu.getText().toString(),convKarton);
                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                break;
                                            case "slove":
                                                convPlu = getConv("slove",TVPlu.getText().toString(),convSlove);
                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                break;
                                        }
                                    }

                                    //savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(),ETCon.getText().toString(),Integer.parseInt(ETSlove.getText().toString()));

                                    List<ORMFakturDet> srtJlns = db.GetBarcodeDet(barcodex222,TVSrtJln.getText().toString(), ETContainer.getText().toString());
                                    Log.d(tag, "srtJlns.size() : " + srtJlns.size());
                                    if(srtJlns.size()>0){
                                        switch (modeConv){
                                            case "pcs":
                                                Log.d(tag, "MOde Cov 3 : " + TVPlu.getText().toString());
                                                Log.d(tag, "MOde Cov 2 3: " + convPcs);
                                                convPlu = getConv("pcs",TVPlu.getText().toString(),convPcs);
                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                break;
                                            case "karton":
                                                convPlu = getConv("karton",TVPlu.getText().toString(),convKarton);
                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                break;
                                            case "slove":
                                                convPlu = getConv("slove",TVPlu.getText().toString(),convSlove);
                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                break;
                                        }


                                    ETBarcode.setText("");
                                    setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                                    new Handler().postDelayed(() -> ETBarcode.requestFocus(),0);
                                    Log.d(tag, "modeConv : " + modeConv);
                                    switch (modeConv){
                                        case "pcs":
                                            ETKarton.setText("");
                                            ETSlove.setText("");
                                            ETPcs.setText("1");
                                            break;
                                        case "karton" :
                                            ETSlove.setText("");
                                            ETPcs.setText("");
                                            ETKarton.setText("1");
                                            break;
                                        case "slove" :
                                            ETKarton.setText("");
                                            ETPcs.setText("");
                                            ETSlove.setText("1");
                                            break;
                                    }

                                        for(ORMFakturDet b:srtJlns){
                                            TVPlu.setText(b.getPlu());
                                            TVPlu.setTag(barcodex222);
                                            TVDescp.setText(b.getDescp());
                                            TVLastQty.setText(String.valueOf(b.getQtyScan()));
                                        }
                                    }else{
                                        reqPlu(ETBarcode.getText().toString());
                                    }


                                }else{
                                    Log.d(tag, "masuk sini 2");
                                    List<ORMFakturDet> srtJlns = db.GetBarcodeDet(barcodex222,TVSrtJln.getText().toString(), ETContainer.getText().toString());
                                    if(srtJlns.size()>0){
                                        Log.d(tag, "masuk sini 3");
//                                        switch (modeConv){
//                                            case "pcs":
//                                                Log.d(tag, "MOde Cov : " + TVPlu.getText().toString());
//                                                Log.d(tag, "MOde Cov 2: " + convPcs);
//                                                convPlu = getConv("pcs",TVPlu.getText().toString(),convPcs);
//                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(),ETCon.getText().toString(),convPlu);
//                                                break;
//                                            case "karton":
//                                                convPlu = getConv("karton",TVPlu.getText().toString(),convKarton);
//                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(),ETCon.getText().toString(),convPlu);
//                                                break;
//                                            case "slove":
//                                                convPlu = getConv("slove",TVPlu.getText().toString(),convSlove);
//                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(),ETCon.getText().toString(),convPlu);
//                                                break;
//                                        }

                                        for(ORMFakturDet b:srtJlns){
                                            TVPlu.setText(b.getPlu());
                                            //TVPlu.setTag(b.getBarcode());
                                            TVPlu.setTag(barcodex222);
                                            TVDescp.setText(b.getDescp());
                                            TVLastQty.setText(String.valueOf(b.getQtyScan()));
                                        }
                                        //Log.d(tag, "BARCODEDB : " + barcodeDb + " BARCODEX : " + barcodex);

                                        new Handler().postDelayed(() -> ETBarcode.requestFocus(),0);
                                        switch (modeConv){
                                            case "pcs":
                                                ETKarton.setText("");
                                                ETSlove.setText("");
                                                if(ETPcs.getText().toString().equals("")){
                                                    ETPcs.setText("1");
                                                }else{
                                                    int pcs = Integer.parseInt(ETPcs.getText().toString());
                                                    pcs +=1;
                                                    ETPcs.setText(String.valueOf(pcs));
                                                }
                                                break;
                                            case "karton" :
                                                ETSlove.setText("");
                                                ETPcs.setText("");
                                                if(ETKarton.getText().toString().equals("")){
                                                    ETKarton.setText("1");
                                                }else{
                                                    int karton = Integer.parseInt(ETKarton.getText().toString());
                                                    karton +=1;
                                                    ETKarton.setText(String.valueOf(karton));
                                                }
                                                break;
                                            case "slove" :
                                                ETKarton.setText("");
                                                ETPcs.setText("");
                                                if(ETSlove.getText().toString().equals("")){
                                                    ETSlove.setText("1");
                                                }else{
                                                    int slove = Integer.parseInt(ETSlove.getText().toString());
                                                    slove +=1;
                                                    ETSlove.setText(String.valueOf(slove));
                                                }
                                                break;
                                        }
                                        /*ETBarcode.setText("");
                                        View view = ScanActivity.this.getCurrentFocus();
                                        Log.d(tag, "req 2");
                                        ETBarcode.requestFocus();
                                        if (view != null) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        }*/
                                    }else{
                                        Log.d(tag, "PLU NOT FOUND, REQ KE SERVER");
                                        //OkHttpClient client = new OkHttpClient();
                                        OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();

                                        String url = urlTablet + "GetPlu/?storeId="+storeId+"&plu="+ETBarcode.getText().toString()+"&region=1";
                                        Log.d("MD", "kiriman : " + url);
                                        Request request = new Request.Builder()
                                                .url(url)
                                                //.post(body)
                                                .build();
                                        alert();

                                        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                                                runOnUiThread(() ->  alertDialog.dismiss());
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    final String myResponse = Objects.requireNonNull(response.body()).string();
                                                    Log.d(tag, myResponse);
                                                    try{
                                                        if(myResponse.isEmpty()){
                                                            runOnUiThread(() -> Toast.makeText(context, "PLU TIDAK DITEMUKAN", Toast.LENGTH_LONG).show());
                                                        }else{
                                                            runOnUiThread(() -> {
                                                                try{
                                                                    //insert ke faktur_det

                                                                    switch (modeConv){
                                                                        case "pcs":
                                                                            Log.d(tag, "MOde Cov : " + TVPlu.getText().toString());
                                                                            Log.d(tag, "MOde Cov 2: " + convPcs);
                                                                            convPlu = getConv("pcs",TVPlu.getText().toString(),convPcs);
                                                                            savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                                            break;
                                                                        case "karton":
                                                                            convPlu = getConv("karton",TVPlu.getText().toString(),convKarton);
                                                                            savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                                            break;
                                                                        case "slove":
                                                                            convPlu = getConv("slove",TVPlu.getText().toString(),convSlove);
                                                                            savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                                            break;
                                                                    }


                                                                    JSONObject obj = new JSONObject(myResponse);
                                                                    db.InsertFakturDet(new ORMFakturDet(srtJln,faktur, ETContainer.getText().toString(), String.valueOf(obj.getLong("plu")),String.valueOf(obj.getLong("barcode")),
                                                                            obj.getString("descp"),0,obj.getInt("conv1"),obj.getInt("conv2"),"1",0));
                                                                    /*String barcodex = ETBarcode.getText().toString().substring(0,ETBarcode.getText().toString().length()-1);*/
                                                                    String barcodex = String.valueOf(obj.getLong("barcode"));
                                                                    List<ORMFakturDet> srtJlns = db.GetBarcodeDet(barcodex,TVSrtJln.getText().toString(), ETContainer.getText().toString());
                                                                    if(srtJlns.size()>0){
                                                                        for(ORMFakturDet b:srtJlns){
                                                                            TVPlu.setText(b.getPlu());
                                                                            TVPlu.setTag(b.getBarcode());
                                                                            TVDescp.setText(b.getDescp());
                                                                            TVLastQty.setText(String.valueOf(b.getQtyScan()));
                                                                        }
                                                                        new Handler().postDelayed(() -> ETBarcode.requestFocus(),0);
                                                                        switch (modeConv){
                                                                            case "pcs":
                                                                                ETKarton.setText("");
                                                                                ETSlove.setText("");
                                                                                if(ETPcs.getText().toString().equals("")){
                                                                                    ETPcs.setText("1");
                                                                                }else{
                                                                                    int pcs = Integer.parseInt(ETPcs.getText().toString());
                                                                                    pcs +=1;
                                                                                    ETPcs.setText(String.valueOf(pcs));
                                                                                }
                                                                                break;
                                                                            case "karton" :
                                                                                ETSlove.setText("");
                                                                                ETPcs.setText("");
                                                                                if(ETKarton.getText().toString().equals("")){
                                                                                    ETKarton.setText("1");
                                                                                }else{
                                                                                    int karton = Integer.parseInt(ETKarton.getText().toString());
                                                                                    karton +=1;
                                                                                    ETKarton.setText(String.valueOf(karton));
                                                                                }
                                                                                break;
                                                                            case "slove" :
                                                                                ETKarton.setText("");
                                                                                ETPcs.setText("");
                                                                                if(ETSlove.getText().toString().equals("")){
                                                                                    ETSlove.setText("1");
                                                                                }else{
                                                                                    int slove = Integer.parseInt(ETSlove.getText().toString());
                                                                                    slove +=1;
                                                                                    ETSlove.setText(String.valueOf(slove));
                                                                                }
                                                                                break;
                                                                        }
                                                                       /* ETBarcode.setText("");
                                                                        View view = ScanActivity.this.getCurrentFocus();
                                                                        Log.d(tag, "req 3");
                                                                        ETBarcode.requestFocus();
                                                                        if (view != null) {
                                                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                                                        }*/
                                                                    }
                                                                    /*ETBarcode.setText("");
                                                                    View view = ScanActivity.this.getCurrentFocus();
                                                                    Log.d(tag, "req 4");
                                                                    ETBarcode.requestFocus();
                                                                    if (view != null) {
                                                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                                                    }*/
                                                                }catch (Exception e){
                                                                    e.printStackTrace();
                                                                }
                                                            });
                                                        }
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                        runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                                                    }
                                                    runOnUiThread(() -> alertDialog.dismiss());
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
                                                        runOnUiThread(() ->  alertDialog.dismiss());
                                                    }
                                                }
                                                runOnUiThread(() -> alertDialog.dismiss());
                                            }
                                        });
                                    }
                                }
                            }
                            ETBarcode.setText("");
                            new Handler().postDelayed(() -> ETBarcode.requestFocus(),0);
                            return false;


                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
            return false;
        });

        BtnFinish.setOnClickListener(v -> {
            try{
                if(!ETContainer.getText().toString().equals("")){
                    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                //localhost:5000/tablet/lpb/Final/?storeId=TD46&faktur=TC22023960,TCXXXXX,TCZZZZZZ&kontainer=ALL
                                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                //OkHttpClient client = new OkHttpClient();
                                OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
                                String url = urlTablet + "Final/?storeId="+storeId+"&kontainer="+ ETContainer.getText().toString()+ "&nik="+getNik();
                                JSONObject json = new JSONObject();
                                JSONArray jsonArray = new JSONArray();
                                JSONArray fakturArray = new JSONArray();
                                try{
                                    Cursor res = db.distinctFaktur(srtJln);
                                    String faktur;
                                    res.moveToFirst();
                                    while(!res.isAfterLast()){
                                        faktur = res.getString(res.getColumnIndexOrThrow("faktur"));
                                        fakturArray.put(faktur);
                                        res.moveToNext();
                                    }
                                    res.close();
                                    json.put("stat_cnt", getStatCnt());
                                    json.put("faktur", fakturArray);
                                    List<ORMFakturDet> orm;
                                    if(ETContainer.getText().toString().equals("ALL")){
                                        orm = db.GetFakturDet(fakturKutip);
                                    }else{
                                        orm = db.GetFakturDetKontainer(fakturKutip, ETContainer.getText().toString());
                                    }
                                    if(orm.size()>0){
                                        for(ORMFakturDet b: orm){
                                            JSONObject obj = new JSONObject();
                                            obj.put("barcode",b.getBarcode());
                                            obj.put("conv1", b.getConv1());
                                            obj.put("conv2",b.getConv2());
                                            obj.put("descp",b.getDescp());
                                            obj.put("kontainer", b.getKontainer());
                                            obj.put("plu", Integer.parseInt(b.getPlu()));
                                            obj.put("qtyScan",b.getQtyScan());
                                            jsonArray.put(obj);
                                        }
                                        json.put("params",jsonArray);


                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

//                                generateNoteOnSD(json.toString());
                                Log.d("MD", "kiriman : " + url);
                                Log.d("MD", "body : " + json);
                                RequestBody body = RequestBody.create(String.valueOf(json), JSON);
                                Request request = new Request.Builder()
                                        .url(url)
                                        .post(body)
                                        .build();
                                alert();

                                try {

                                    client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                                        runOnUiThread(() -> alertDialog.dismiss());
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                        if (response.isSuccessful()) {
                                            try{
                                                final String myResponse = Objects.requireNonNull(response.body()).string();
                                                Log.d(tag, myResponse);
                                                if(myResponse.isEmpty()){
                                                    //runOnUiThread(() -> Toast.makeText(context, "EMPTY RESPONSE", Toast.LENGTH_LONG).show());
                                                    Intent intent = new Intent(context, ScanSelisihActivity.class);
                                                    tmp_datares = "";
//                                                    intent.putExtra("jsonRes", "");
                                                    intent.putExtra("srtJln", srtJln);
                                                    intent.putExtra("faktur", fakturNoKutip);
                                                    intent.putExtra("kontainer", ETContainer.getText().toString());
                                                    startActivity(intent);
                                                }else{
                                                    Intent intent = new Intent(context, ScanSelisihActivity.class);
                                                    tmp_datares = myResponse;
//                                                    intent.putExtra("jsonRes", myResponse);
                                                    intent.putExtra("srtJln", srtJln);
                                                    intent.putExtra("faktur", fakturNoKutip);
                                                    intent.putExtra("kontainer", ETContainer.getText().toString());
                                                    startActivity(intent);
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                                            }
                                            runOnUiThread(() ->  alertDialog.dismiss());
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
                                                runOnUiThread(() ->  alertDialog.dismiss());
                                            }
                                        }
                                        runOnUiThread(() ->  alertDialog.dismiss());
                                    }
                                });


                                }catch (Exception e){e.printStackTrace();}
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setTitle("Warning");
                    builder.setMessage("FINISH SURAT JALAN " + TVSrtJln.getText().toString() + " KONTAINER " + ETContainer.getText().toString() + "?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener)
                            .show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        getFaktur(srtJln,faktur);

        Cursor res = db.distinctFaktur(srtJln);
        TVJmlPO.setText(String.valueOf(res.getCount()));
        res.close();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onRestart(){
        super.onRestart();

    }

    private void alert() {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(ScanActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_loading, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }
    public void reqPlu(String barcodex){
        try{
            Log.d(tag, "PLU NOT FOUND, REQ KE SERVER");
            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
            String url = urlTablet + "GetPlu/?storeId="+storeId+"&plu="+barcodex+"&region=1";
            Log.d("MD", "kiriman : " + url);
            Request request = new Request.Builder()
                    .url(url)
                    //.post(body)
                    .build();
            alert();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                    runOnUiThread(() -> alertDialog.dismiss());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myResponse = Objects.requireNonNull(response.body()).string();
                        Log.d(tag, myResponse);
                        try{
                            if(myResponse.isEmpty()){
                                Log.d("TAG", "onResponse: Tidak ada");
                                runOnUiThread(() -> Toast.makeText(context, "PLU TIDAK DITEMUKAN", Toast.LENGTH_LONG).show());
                            }else{
                                Log.d("TAG", "onResponse: ada");
                                runOnUiThread(() -> {
                                    try{
                                        //insert ke faktur_det
                                        JSONObject obj = new JSONObject(myResponse);
                                        db.InsertFakturDet(new ORMFakturDet(srtJln,faktur, ETContainer.getText().toString(), String.valueOf(obj.getLong("plu")),String.valueOf(obj.getLong("barcode")),
                                                obj.getString("descp"),0,obj.getInt("conv1"),obj.getInt("conv2"),"1",0));
                                        //String barcodex2 = barcodex.substring(0,barcodex.length()-1);
                                        String barcodex2 = String.valueOf(obj.getLong("barcode"));
                                        switch (modeConv){
                                            case "pcs":
                                                Log.d(tag, "MOde Cov 3 : " + TVPlu.getText().toString());
                                                Log.d(tag, "MOde Cov 2 3: " + convPcs);
                                                convPlu = getConv("pcs",TVPlu.getText().toString(),convPcs);
                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                break;
                                            case "karton":
                                                convPlu = getConv("karton",TVPlu.getText().toString(),convKarton);
                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                break;
                                            case "slove":
                                                convPlu = getConv("slove",TVPlu.getText().toString(),convSlove);
                                                savePlu(TVPlu.getText().toString(),TVSrtJln.getText().toString(), ETContainer.getText().toString(),convPlu);
                                                break;
                                        }

//                                        switch (modeConv){
//                                            case "pcs":
//                                                ETKarton.setText("");
//                                                ETSlove.setText("");
//                                                ETPcs.setText("1");
//                                                break;
//                                            case "karton" :
//                                                ETSlove.setText("");
//                                                ETPcs.setText("");
//                                                ETKarton.setText("1");
//                                                break;
//                                            case "slove" :
//                                                ETKarton.setText("");
//                                                ETPcs.setText("");
//                                                ETSlove.setText("1");
//                                                break;
//                                        }


                                        Log.d(tag, barcodex2);
                                        List<ORMFakturDet> srtJlns = db.GetBarcodeDet(barcodex2,TVSrtJln.getText().toString(), ETContainer.getText().toString());
                                        if(srtJlns.size()>0){
                                            Log.d("TAG", "ORMFakturDet: ada");
                                            for(ORMFakturDet b:srtJlns){
                                                TVPlu.setText(b.getPlu());
                                                TVPlu.setTag(b.getBarcode());
                                                TVDescp.setText(b.getDescp());
                                                ETPcs.setText("");
                                                TVLastQty.setText(String.valueOf(b.getQtyScan()));
                                            }
                                            ETBarcode.setText("");
                                            new Handler().postDelayed(() -> ETBarcode.requestFocus(),0);
                                            switch (modeConv){
                                                case "pcs":
                                                    ETKarton.setText("");
                                                    ETSlove.setText("");
                                                    ETPcs.setText("1");
                                                    break;
                                                case "karton" :
                                                    ETSlove.setText("");
                                                    ETPcs.setText("");
                                                    ETKarton.setText("1");
                                                    break;
                                                case "slove" :
                                                    ETKarton.setText("");
                                                    ETPcs.setText("");
                                                    ETSlove.setText("1");
                                                    break;
                                            }
                                        }

                                        setViewPlu(TVSrtJln.getText().toString(), ETContainer.getText().toString());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                        }
                        runOnUiThread(() ->  alertDialog.dismiss());
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
                            runOnUiThread(() ->  alertDialog.dismiss());
                        }
                    }
                    runOnUiThread(() -> alertDialog.dismiss());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void viewKontainer(String resp){
        try{
            ETPcs.setText("");
            ETKarton.setText("");
            ETSlove.setText("");
            android.app.AlertDialog.Builder Layout = new android.app.AlertDialog.Builder(context);
            LayoutInflater inflater = ScanActivity.this.getLayoutInflater();
            final android.app.AlertDialog alertDialog = Layout.create();
            final ViewGroup nullParent = null;
            alertDialog.setView(inflater.inflate(R.layout.lay_kontainer, nullParent), 0, 0, 0, 0);
            alertDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(true);
            alertDialog.show();

            TVAll1 = alertDialog.findViewById(R.id.TVAll1);
            TVAll2 = alertDialog.findViewById(R.id.TVAll2);
            LLFakturDet = alertDialog.findViewById(R.id.LLFakturDet);
            LLFakturDet.removeAllViews();

            TVAll1.setOnClickListener(v1 -> {
                ETBarcode.requestFocus();
                kontainer = "ALL";
                ETContainer.setText("ALL");
                alertDialog.dismiss();
                setViewPlu(srtJln,"ALL");
                countpage = 0;
            });

            TVAll2.setOnClickListener(v1 -> {
                ETBarcode.requestFocus();
                kontainer = "ALL";
                ETContainer.setText("ALL");
                alertDialog.dismiss();
                countpage = 0;
                setViewPlu(srtJln,"ALL");
            });

            JSONArray dataResult = new JSONArray(resp);
            int n = dataResult.length();
            String[] k01 = new String[n];
            String[] k02 = new String[n];
            for (int i = 0; i < n; i++) {
                JSONObject data = dataResult.getJSONObject(i);
                k01[i] = data.getString("kontainer");
                k02[i] = data.getString("status");
                String tmpdata = k01[i];

                final TextView rtv01 = new TextView(context);
                final TextView rtv02 = new TextView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(0, 90,1);
                params.topMargin=1;
                final LinearLayout llHor = new LinearLayout(ScanActivity.this);
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
                rtv01.setOnClickListener(v1 -> {
                    kontainer = rtv01.getText().toString();
                    ETContainer.setText(rtv01.getText().toString());
                    alertDialog.dismiss();
                    setViewPlu(srtJln,kontainer);
                    clearPlu();
                    countpage = 0;
                    Log.d(tag, "viewKontainer: "+tmpdata);

                    ETBarcode.requestFocus();
                    if (tmpdata.equals("DOS")){
                        Log.d(tag, "MLEBU O  1 "+tmpdata);
                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        modeConv = "karton";
                        ETKarton.setText("");
                        ETBarcode.requestFocus();

                    }else{
                        Log.d(tag, "MLEBU  0 2 "+tmpdata);
                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        modeConv = "pcs";
                        ETPcs.setText("");

                    }

                });
                llHor.addView(rtv01);

                rtv02.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv02.setText(k02[i]);
                rtv02.setHorizontallyScrolling(true);
                rtv02.setEllipsize(TextUtils.TruncateAt.END);
                rtv02.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                rtv02.setSingleLine();
                rtv02.setLayoutParams(params_1);
                rtv02.setOnClickListener(v1 -> {
                    kontainer = rtv01.getText().toString();
                    ETContainer.setText(rtv01.getText().toString());
                    alertDialog.dismiss();
                    setViewPlu(srtJln,kontainer);
                    clearPlu();
                    countpage = 0;
                    Log.d(tag, "viewKontainer: "+tmpdata);

                    ETBarcode.requestFocus();
                    if (tmpdata.equals("DOS")){
                        Log.d(tag, "MLEBU  1 "+tmpdata);
                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        modeConv = "karton";
                        ETKarton.setText("");


                    }else{
                        Log.d(tag, "MLEBU  2 "+tmpdata);
                        LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
                        LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
                        modeConv = "pcs";
                        ETPcs.setText("");
                    }
                });
                llHor.addView(rtv02);
                LLFakturDet.addView(llHor);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void savePlu(String plux, String srtJlnx, String kontainerx, int qtyx){
        try{
            //cek kontainer all atau tidak
            if (!plux.equals("")){
                if(kontainerx.equals("ALL")){
                    //cek sebelumnya ada qty scan atau tidak
                    int qtyScan=0;
                    List<ORMFakturDet> srtJlns = db.GetPluDet(plux,srtJlnx,"ALL");
                    if(srtJlns.size()>0){
                        for(ORMFakturDet b:srtJlns){
                            qtyScan = b.getQtyScan();
                        }
                        if(qtyScan>0){
                            //update
                            db.updatePlu(plux,srtJlnx,"ALL",(qtyx+qtyScan));
                        }else{
                            //insert
                            db.updatePlu(plux,srtJlnx,"ALL",qtyx);
                        }
                    }else{
                        //insert
                        List<ORMFakturDet> gets = db.GetPlu(plux);
                        String barcode="", descp="";
                        int conv1=1, conv2=1;
                        for(ORMFakturDet b:gets){
                            barcode = b.getBarcode();
                            descp = b.getDescp();
                            conv1 = b.getConv1();
                            conv2 = b.getConv2();
                        }

                        Log.d(tag, "MASUK ALL  5"+plux);
                        db.InsertFakturDet(new ORMFakturDet(srtJln,faktur, "ALL", plux,barcode,
                                descp,0,conv1,conv2,"1",qtyx));
                    }
                }else{
                    //cek sebelumnya ada qty scan atau tidak
                    int qtyScan=0;
                    List<ORMFakturDet> srtJlns = db.GetPluDet(plux,srtJlnx,kontainerx);
                    if(srtJlns.size()>0){
                        for(ORMFakturDet b:srtJlns){
                            qtyScan = b.getQtyScan();
                        }
                        if(qtyScan>0){
                            //update
                            db.updatePlu(plux,srtJlnx,kontainerx,(qtyx+qtyScan));
                        }else{
                            //insert
                            db.updatePlu(plux,srtJlnx,kontainerx,qtyx);
                        }
                    }
                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getFaktur(String srtJlnx, String faktur) {
        try{
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = TrustSSL.getUnsafeOkHttpClient();
            String url = urlTablet + "get_det_faktur/?storeId="+storeId+"&faktur="+faktur;
            /*JSONObject json = new JSONObject();
            json.put("storeid", "TD46");
            json.put("filter", "");*/
            Log.d("MD", "kiriman : " + url);
            //RequestBody body = RequestBody.create(String.valueOf(json), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    //.post(body)
                    .build();
            alert();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                    runOnUiThread(() ->  alertDialog.dismiss());
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
                                db.DeleteFakturDet(srtJlnx,faktur);
                                JSONObject obj = new JSONObject(myResponse);
                                JSONArray dataResult = obj.getJSONArray("data");
                                int n = dataResult.length();
                                for (int i = 0; i < n; i++) {
                                    JSONObject data = dataResult.getJSONObject(i);
                                    //db.InsertSrtJln(new ORMSrtJln(data.getString("faktur"),data.getString("faktur_sji"),data.getString("order_date"),data.getString("status_proses")));
                                    db.InsertFakturDet(new ORMFakturDet(srtJlnx,faktur, data.getString("kontainer"), String.valueOf(data.getLong("plu")),String.valueOf(data.getLong("barcode")),
                                            data.getString("descp"),0,data.getInt("conv1"),data.getInt("conv2"),data.getString("flag"),data.getInt("qty")));
                                }
                                statCnt = obj.getString("stat_cnt");
                                saveStatCnt(statCnt);

                                runOnUiThread(() ->  {
                                    Cursor res = db.distinctFaktur(srtJln);
                                    TVJmlPO.setText(String.valueOf(res.getCount()));
                                    res.close();
                                });

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
                        }
                        runOnUiThread(() ->  alertDialog.dismiss());
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
                            runOnUiThread(() ->  alertDialog.dismiss());
                        }
                    }
                    runOnUiThread(() -> alertDialog.dismiss());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
        }
    }

    public void setPlu(String plux, String srtJlnx, String kontainerx){
        try{
            if(kontainerx.equals("ALL")){
                Log.d("TAG", "Masuk    if: 12"+modeConv);
                List<ORMFakturDet> srtJlns = db.GetPluDetAll(plux,srtJlnx);
                int lastQty = 0;
                if(srtJlns.size()>0){
                    for(ORMFakturDet b:srtJlns){
                        TVPlu.setText(b.getPlu());
                        TVPlu.setTag(b.getBarcode());
                        TVDescp.setText(b.getDescp());
                        //TVLastQty.setText(String.valueOf(b.getQtyScan()));
                        lastQty += b.getQtyScan();
                    }
                    TVLastQty.setText(String.valueOf(lastQty));
                    switch (modeConv){
                        case "pcs" :
                            ETPcs.setText("1");
                            break;
                        case "karton" :
                            ETKarton.setText("1");
                            break;
                        case "slove" :
                            ETSlove.setText("1");
                            break;
                    }
                }
            }else{
                Log.d("TAG", "Masuk    else: 12"+modeConv);
                Log.d(tag, plux);
                List<ORMFakturDet> srtJlns = db.GetPluDet(plux,srtJlnx,kontainerx);
                if(srtJlns.size()>0){
                    for(ORMFakturDet b:srtJlns){
                        TVPlu.setText(b.getPlu());
                        Log.d(tag, b.getPlu());
                        TVPlu.setTag(b.getBarcode());
                        TVDescp.setText(b.getDescp());
                        TVLastQty.setText(String.valueOf(b.getQtyScan()));
                    }
                    switch (modeConv){
                        case "pcs" :
                            ETPcs.setText("1");
                            break;
                        case "karton" :
                            ETKarton.setText("1");
                            break;
                        case "slove" :
                            ETSlove.setText("1");
                            break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearPlu(){
        TVPlu.setText("");
        TVPlu.setTag("");
        TVDescp.setText("");
        TVLastQty.setText("");
    }

    public int getConv(String convMode, String plu, int qty){
        //rumus slove : (Val(QTYslv) * (Val(RecordQTYPO("Conv2")) / Val(RecordQTYPO("conv1"))))
        //rumus karton : (Val(QTYKrtn) * (Val(RecordQTYPO("Conv2"))))
        try{
            if(ETContainer.getText().toString().equals("ALL")){
                List<ORMFakturDet> orm = db.GetPlu(plu);
                int conv1=1,conv2=1;
                if(orm.size()>0){
                    for(ORMFakturDet b:orm){
                        conv1 = b.getConv1();
                        conv2 = b.getConv2();
                    }
                    switch (convMode){
                        case "pcs":
                            return qty;
                        case "karton":
                            return (qty * conv2);
                        case  "slove":
                            return (qty * (conv2/conv1));
                    }
                }
            }else{
                List<ORMFakturDet> orm = db.GetPluDet(plu,TVSrtJln.getText().toString(), ETContainer.getText().toString());
                int conv1=1,conv2=1;
                if(orm.size()>0){
                    for(ORMFakturDet b:orm){
                        conv1 = b.getConv1();
                        conv2 = b.getConv2();
                    }
                    switch (convMode){
                        case "pcs":
                            return qty;
                        case "karton":
                            return (qty * conv2);
                        case  "slove":
                            return (qty * (conv2/conv1));
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void setViewPlu(String srtJlnX, String kontainerX){
        try{
            LLItem.removeAllViews();
            Cursor res;
            if(kontainerX.equals("ALL")){
                res = db.KontainerAll(srtJlnX);
            }else {
                res = db.Kontainerplu(srtJlnX, kontainerX);
            }
            String plux,descpx,flagx;
            int qtyx;
            res.moveToFirst();
            while(!res.isAfterLast()){
                plux = res.getString(res.getColumnIndexOrThrow("plu"));
                qtyx = res.getInt(res.getColumnIndexOrThrow("qty_scan"));
                descpx = res.getString(res.getColumnIndexOrThrow("descp"));
                flagx = res.getString(res.getColumnIndexOrThrow("flag"));

                final TextView rtv01 = new TextView(ScanActivity.this);
                final TextView rtv02 = new TextView(ScanActivity.this);
                final TextView rtv03 = new TextView(ScanActivity.this);
                final TextView rtv04 = new TextView(ScanActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(0,90,1);
                LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams(0,90,2);
                LinearLayout.LayoutParams params_3 = new LinearLayout.LayoutParams(0,90,3);
                params.topMargin=1;
                final LinearLayout llHor = new LinearLayout(ScanActivity.this);
                llHor.setLayoutParams(params);
                llHor.setOrientation(LinearLayout.HORIZONTAL);

                rtv01.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv01.setText(plux);
                rtv01.setSingleLine();
                rtv01.setHorizontallyScrolling(true);
                rtv01.setEllipsize(TextUtils.TruncateAt.END);
                rtv01.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_2.setMarginEnd(1);
                rtv01.setLayoutParams(params_2);
                llHor.addView(rtv01);

                rtv02.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv02.setText(String.valueOf(qtyx));
                rtv02.setHorizontallyScrolling(true);
                rtv02.setEllipsize(TextUtils.TruncateAt.END);
                rtv02.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                rtv02.setSingleLine();
                params_1.setMarginEnd(1);
                rtv02.setLayoutParams(params_1);
                llHor.addView(rtv02);

                rtv03.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv03.setText(descpx);
                rtv03.setHorizontallyScrolling(true);
                rtv03.setEllipsize(TextUtils.TruncateAt.END);
                rtv03.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                rtv03.setSingleLine();
                params_3.setMarginEnd(1);
                rtv03.setLayoutParams(params_3);
                llHor.addView(rtv03);

                rtv04.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv04.setText(flagx);
                rtv04.setHorizontallyScrolling(true);
                rtv04.setEllipsize(TextUtils.TruncateAt.END);
                rtv04.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                rtv04.setSingleLine();
                rtv04.setLayoutParams(params_1);
                llHor.addView(rtv04);

                LLItem.addView(llHor);
                res.moveToNext();
            }
            res.close();

            /*LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
            LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
            LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
            modeConv = "pcs";
            TVPlu.setTag("");
            TVPlu.setText("");
            ETPcs.requestFocus();*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadmore(String srtJlnX){
        countpage = countpage+1;
        Log.d("TAG", "loadmore: kontainer"+kontainer);
        Log.d("TAG", "loadmore: countpage"+countpage);
        try{
//            LLItem.removeAllViews();
            Cursor res;
            res = db.KontainerAllpage(srtJlnX,countpage);
            String plux,descpx,flagx;
            int qtyx;
            res.moveToFirst();
            while(!res.isAfterLast()){
                plux = res.getString(res.getColumnIndexOrThrow("plu"));
                qtyx = res.getInt(res.getColumnIndexOrThrow("qty_scan"));
                descpx = res.getString(res.getColumnIndexOrThrow("descp"));
                flagx = res.getString(res.getColumnIndexOrThrow("flag"));

                final TextView rtv01 = new TextView(ScanActivity.this);
                final TextView rtv02 = new TextView(ScanActivity.this);
                final TextView rtv03 = new TextView(ScanActivity.this);
                final TextView rtv04 = new TextView(ScanActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(0,90,1);
                LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams(0,90,2);
                LinearLayout.LayoutParams params_3 = new LinearLayout.LayoutParams(0,90,3);
                params.topMargin=1;
                final LinearLayout llHor = new LinearLayout(ScanActivity.this);
                llHor.setLayoutParams(params);
                llHor.setOrientation(LinearLayout.HORIZONTAL);

                rtv01.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv01.setText(plux);
                rtv01.setSingleLine();
                rtv01.setHorizontallyScrolling(true);
                rtv01.setEllipsize(TextUtils.TruncateAt.END);
                rtv01.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                params_2.setMarginEnd(1);
                rtv01.setLayoutParams(params_2);
                llHor.addView(rtv01);

                rtv02.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv02.setText(String.valueOf(qtyx));
                rtv02.setHorizontallyScrolling(true);
                rtv02.setEllipsize(TextUtils.TruncateAt.END);
                rtv02.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                rtv02.setSingleLine();
                params_1.setMarginEnd(1);
                rtv02.setLayoutParams(params_1);
                llHor.addView(rtv02);

                rtv03.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv03.setText(descpx);
                rtv03.setHorizontallyScrolling(true);
                rtv03.setEllipsize(TextUtils.TruncateAt.END);
                rtv03.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                rtv03.setSingleLine();
                params_3.setMarginEnd(1);
                rtv03.setLayoutParams(params_3);
                llHor.addView(rtv03);

                rtv04.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                rtv04.setText(flagx);
                rtv04.setHorizontallyScrolling(true);
                rtv04.setEllipsize(TextUtils.TruncateAt.END);
                rtv04.setBackgroundColor(Color.parseColor("#ff00ABEB"));
                rtv04.setSingleLine();
                rtv04.setLayoutParams(params_1);
                llHor.addView(rtv04);

                LLItem.addView(llHor);
                res.moveToNext();
            }
            res.close();

            /*LLPcs.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay_red));
            LLKarton.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
            LLSlove.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_lay2));
            modeConv = "pcs";
            TVPlu.setTag("");
            TVPlu.setText("");
            ETPcs.requestFocus();*/
        }catch (Exception e){
            e.printStackTrace();
        }
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

    public void saveStatCnt(String stat){
        ((PublicVariable) this.getApplication()).setStatCnt(stat);
    }

    public String getStatCnt(){
        return  ((PublicVariable) this.getApplication()).getStatCnt();
    }

    public String getNik(){
        return  ((PublicVariable) this.getApplication()).getNik();
    }
}