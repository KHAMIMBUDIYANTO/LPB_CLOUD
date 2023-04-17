package com.alfastore.lpbpda.v2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    Context context;
    DatabaseHandler db;
    EditText ETDC,ETStore;
    Button BtnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        context = SettingActivity.this;
        db = new DatabaseHandler(getApplicationContext());
        ETDC = findViewById(R.id.ETDC);
        ETStore = findViewById(R.id.ETStore);
//        ETDC.setText("TZ01");
//        ETStore.setText("AD83");
        BtnSave = findViewById(R.id.BtnSave);

        BtnSave.setOnClickListener(v -> {
            if(ETDC.getText().toString().equals("")){
                Toast.makeText(context, "DC ID harus terisi", Toast.LENGTH_SHORT).show();
                return;
            }
            if(ETStore.getText().toString().equals("")){
                Toast.makeText(context, "STORE ID harus terisi", Toast.LENGTH_SHORT).show();
                return;
            }

            db.DeleteAllStore();
            db.InsertMsStore(new ORMMsStore(ETDC.getText().toString().toUpperCase(), ETStore.getText().toString().toUpperCase()));
            //Toast.makeText(context, "setting complete", Toast.LENGTH_SHORT).show();
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle("SETTING");
            builder.setMessage("Data berhasil disimpan, silahkan kembali ke menu Login").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        });
    }
}