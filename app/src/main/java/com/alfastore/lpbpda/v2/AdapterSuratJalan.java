package com.alfastore.lpbpda.v2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterSuratJalan extends RecyclerView.Adapter<AdapterSuratJalan.ViewHolder> {
    DatabaseHandler db;
    Context context;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView TVSrtJln,TVFaktur,TVTglPo,TVStatus;
        public Button BtnTotal;
        LinearLayout LLSrtJln;

        public ViewHolder(View itemView) {
            super(itemView);
            TVSrtJln = itemView.findViewById(R.id.TVSrtJln);
            TVFaktur = itemView.findViewById(R.id.TVFaktur);
            BtnTotal = itemView.findViewById(R.id.BtnTotal);
            TVTglPo = itemView.findViewById(R.id.TVTglPo);
            TVStatus = itemView.findViewById(R.id.TVStatus);
            LLSrtJln = itemView.findViewById(R.id.LLSrtJln);

        }
    }

    private List<ORMSrtJln> modelSuratJalans;
    public AdapterSuratJalan(List<ORMSrtJln> contacts) {
        modelSuratJalans = contacts;
    }

    @Override
    public AdapterSuratJalan.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.lay_surat_jalan, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterSuratJalan.ViewHolder holder, int position) {
        String tag = new PublicVariable().tag();
        // Get the data model based on position
        ORMSrtJln modelSuratJalan = modelSuratJalans.get(position);

        // Set item views based on your views and data model
        TextView TVSrtJln = holder.TVSrtJln;
        TVSrtJln.setText(modelSuratJalan.getSrtJln());

        TextView TVFaktur = holder.TVFaktur;
        db = new DatabaseHandler(context);
        Cursor res = db.fakturSrtJln(TVSrtJln.getText().toString());

        String fakturX,fakturNoKutip;
        StringBuilder sb = new StringBuilder();
        res.moveToFirst();
        while(!res.isAfterLast()){
            fakturX =  res.getString(res.getColumnIndexOrThrow("faktur"))  + ",";
            sb.append(fakturX);
            res.moveToNext();
        }
        res.close();
        fakturNoKutip = sb.substring(0, (sb.length() - 1));
        //TVFaktur.setText(modelSuratJalan.getFaktur());
        TVFaktur.setText(fakturNoKutip);

        Button BtnTotal = holder.BtnTotal;
        BtnTotal.setOnClickListener(v -> {
            //view new layout
            try{
                Log.d(tag, "DETAIL " + TVFaktur.getText().toString());
                //getFakturDet(TVFaktur.getText().toString());
                Intent intent = new Intent("fakturDet");
                intent.putExtra("faktur",TVFaktur.getText().toString());
                intent.putExtra("srtJln",TVSrtJln.getText().toString());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        TextView TVTglPo = holder.TVTglPo;
        TVTglPo.setText(modelSuratJalan.getOrderDate());

        TextView TVStatus = holder.TVStatus;
        TVStatus.setText(modelSuratJalan.getStatusProses());

        LinearLayout LLSrtJln = holder.LLSrtJln;

        LLSrtJln.setOnClickListener(v -> {
            if(TVStatus.getText().toString().equals("FINAL")){
                Toast.makeText(v.getContext(),"STATUS SUDAH FINAL, TIDAK DAPAT DIUBAH", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("IR", TVSrtJln.getText().toString());
                Intent intent = new Intent(v.getContext(), ScanActivity.class);
                intent.putExtra("faktur", TVFaktur.getText().toString());
                intent.putExtra("srtJln", TVSrtJln.getText().toString());
                v.getContext().startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return modelSuratJalans.size();
    }

}
