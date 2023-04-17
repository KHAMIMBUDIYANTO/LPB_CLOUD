package com.alfastore.lpbpda.v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    String tag = new PublicVariable().tag();
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "LPBPDA";
    private static final String MASTER_STORE = "ms_store";
    private static final String SRT_JLN_HEAD = "srt_jln_head";
    private static final String FAKTUR_DET = "FAKTUR_DET";

    //column ms_store
    private static final String store_id = "store_id";
    private static final String store_dcid = "dc_id";

    //column SRT_JLN_HEAD
    private static  final String srt_jln = "srt_jln";
    private static  final String faktur = "faktur";
    private static  final String order_date = "order_date";
    private static  final String status_proses = "status_proses";

    //column FAKTUR_DET
    private static  final String kontainer = "kontainer";
    private static  final String plu = "plu";
    private static  final String barcode = "barcode";
    private static  final String descp = "descp";
    private static  final String qty = "qty";
    private static  final String conv1 = "conv1";
    private static  final String conv2 = "conv2";
    private static  final String flag = "flag";
    private static  final String qty_scan = "qty_scan";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_FAKTUR_DET = "CREATE TABLE " + FAKTUR_DET + "("
            + srt_jln + " TEXT," + faktur + " TEXT," + kontainer + " TEXT," + plu + " TEXT," + barcode + " TEXT," + descp + " TEXT,"
            + qty + " NUMERIC," + conv1 + " NUMERIC," + conv2 + " NUMERIC," + flag + " TEXT," + qty_scan + " NUMERIC,"
            + " PRIMARY KEY(" + srt_jln + "," + faktur + "," + kontainer + "," + plu + "))";

    private static final String CREATE_MS_STORE = "CREATE TABLE " + MASTER_STORE + "("
            + store_id + " TEXT," + store_dcid + " TEXT)";

    private static final String CREATE_SRT_JLN_HEAD = "CREATE TABLE " + SRT_JLN_HEAD + "("
            + srt_jln + " TEXT," + faktur + " TEXT," + order_date + " TEXT," + status_proses + " TEXT,"
            + " PRIMARY KEY(" + srt_jln + "," + faktur +"))";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MS_STORE);
        db.execSQL(CREATE_SRT_JLN_HEAD);
        db.execSQL(CREATE_FAKTUR_DET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_STORE);
        db.execSQL("DROP TABLE IF EXISTS " + SRT_JLN_HEAD);
        db.execSQL("DROP TABLE IF EXISTS " + FAKTUR_DET);
    }

    //----------------------------------FAKTUR_DET-----------------------------------
    public void DeleteAllFakturDet(){
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(FAKTUR_DET, "",null);

        db.delete(FAKTUR_DET, faktur + " IN (select " + faktur + " from " + SRT_JLN_HEAD + " where " + status_proses + "='FINAL')", null);
    }

    public void DeleteFakturDet(String srtJlnx, String fakturx){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FAKTUR_DET, qty_scan + "=0 and " + srt_jln + " =? and " + faktur + "=?",new String[]{srtJlnx,fakturx});
    }

    public void InsertFakturDet(ORMFakturDet orm){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(srt_jln, orm.getSrtJln());
            values.put(faktur, orm.getFaktur());
            values.put(kontainer, orm.getKontainer());
            values.put(plu, orm.getPlu());
            values.put(barcode, orm.getBarcode());
            values.put(descp, orm.getDescp());
            values.put(qty, orm.getQty());
            values.put(conv1, orm.getConv1());
            values.put(conv2, orm.getConv2());
            values.put(flag, orm.getFlag());
            values.put(qty_scan, orm.getQtyScan());
            db.insertWithOnConflict(FAKTUR_DET, null, values, SQLiteDatabase.CONFLICT_IGNORE);

            /*int id = (int)db.insertWithOnConflict(SRT_JLN_HEAD, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if(id == -1){
                //db.update(SRT_JLN_HEAD, values, status_proses + "=?", new String[]{orm.getStatusProses()});
                db.update(SRT_JLN_HEAD, values, status_proses + "='BELUM'", null);
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Cursor distinctFaktur(String srtJln){
        String query = "select distinct " + faktur + " from " + FAKTUR_DET + " where " + srt_jln + " = '" + srtJln + "'";
        Log.d(tag, query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query,null);
    }

    public Cursor distinctKontainer(String srtJln){
        String query = "select distinct " + kontainer + " from " + FAKTUR_DET + " where " + srt_jln + " = '" + srtJln + "'";
        Log.d(tag, query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query,null);
    }

    public Cursor Kontainerplu(String srtJln, String kontainerx){
        String query = "select * from " + FAKTUR_DET + " where " + srt_jln + " = '" + srtJln + "' and " + kontainer + "='" + kontainerx  + "' order by " + descp + " asc";
        Log.d(tag, query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query,null);
    }

    public Cursor KontainerAll(String srtJln){
        String query = "select * from " + FAKTUR_DET + " where " + srt_jln + " = '" + srtJln + "' order by " + descp + " asc limit 100";
        Log.d(tag, query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query,null);
    }
    public Cursor KontainerAllpage(String srtJln,int page){
        String query = "select * from " + FAKTUR_DET + " where " + srt_jln + " = '" + srtJln + "' order by " + descp + " asc limit 100 OFFSET "+page*100;
        Log.d(tag, query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query,null);
    }

    public List<ORMFakturDet> GetFakturDet(String fakturx){
        List<ORMFakturDet> list = new ArrayList<>();
        String query = "select * from " + FAKTUR_DET + " where " + faktur + " in("+ fakturx +")";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                ORMFakturDet orm = new ORMFakturDet();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur(cursor.getString(cursor.getColumnIndexOrThrow(faktur)));
                orm.setKontainer(cursor.getString(cursor.getColumnIndexOrThrow(kontainer)));
                orm.setPlu(cursor.getString(cursor.getColumnIndexOrThrow(plu)));
                orm.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(barcode)));
                orm.setDescp(cursor.getString(cursor.getColumnIndexOrThrow(descp)));
                orm.setQty(cursor.getInt(cursor.getColumnIndexOrThrow(qty)));
                orm.setConv1(cursor.getInt(cursor.getColumnIndexOrThrow(conv1)));
                orm.setConv2(cursor.getInt(cursor.getColumnIndexOrThrow(conv2)));
                orm.setFlag(cursor.getString(cursor.getColumnIndexOrThrow(flag)));
                orm.setQtyScan(cursor.getInt(cursor.getColumnIndexOrThrow(qty_scan)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<ORMFakturDet> GetFakturDetKontainer(String fakturx, String kontainerx){
        List<ORMFakturDet> list = new ArrayList<>();
        String query = "select * from " + FAKTUR_DET + " where " + faktur + " in("+ fakturx +") and " + kontainer + " = '" + kontainerx + "'";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                ORMFakturDet orm = new ORMFakturDet();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur(cursor.getString(cursor.getColumnIndexOrThrow(faktur)));
                orm.setKontainer(cursor.getString(cursor.getColumnIndexOrThrow(kontainer)));
                orm.setPlu(cursor.getString(cursor.getColumnIndexOrThrow(plu)));
                orm.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(barcode)));
                orm.setDescp(cursor.getString(cursor.getColumnIndexOrThrow(descp)));
                orm.setQty(cursor.getInt(cursor.getColumnIndexOrThrow(qty)));
                orm.setConv1(cursor.getInt(cursor.getColumnIndexOrThrow(conv1)));
                orm.setConv2(cursor.getInt(cursor.getColumnIndexOrThrow(conv2)));
                orm.setFlag(cursor.getString(cursor.getColumnIndexOrThrow(flag)));
                orm.setQtyScan(cursor.getInt(cursor.getColumnIndexOrThrow(qty_scan)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<ORMFakturDet> GetPlu(String plux){
        List<ORMFakturDet> list = new ArrayList<>();
        String query = "select * from " + FAKTUR_DET + " where " + plu + " ='" + plux + "' LIMIT 1";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                ORMFakturDet orm = new ORMFakturDet();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur(cursor.getString(cursor.getColumnIndexOrThrow(faktur)));
                orm.setKontainer(cursor.getString(cursor.getColumnIndexOrThrow(kontainer)));
                orm.setPlu(cursor.getString(cursor.getColumnIndexOrThrow(plu)));
                orm.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(barcode)));
                orm.setDescp(cursor.getString(cursor.getColumnIndexOrThrow(descp)));
                orm.setQty(cursor.getInt(cursor.getColumnIndexOrThrow(qty)));
                orm.setConv1(cursor.getInt(cursor.getColumnIndexOrThrow(conv1)));
                orm.setConv2(cursor.getInt(cursor.getColumnIndexOrThrow(conv2)));
                orm.setFlag(cursor.getString(cursor.getColumnIndexOrThrow(flag)));
                orm.setQtyScan(cursor.getInt(cursor.getColumnIndexOrThrow(qty_scan)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<ORMFakturDet> GetPluDet(String plux, String srtJlnx, String kontainerx){
        List<ORMFakturDet> list = new ArrayList<>();
        String query = "select * from " + FAKTUR_DET + " where " + plu + " ='" + plux + "' and " + srt_jln + " ='" + srtJlnx + "' and " + kontainer + " ='" + kontainerx + "'";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                ORMFakturDet orm = new ORMFakturDet();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur(cursor.getString(cursor.getColumnIndexOrThrow(faktur)));
                orm.setKontainer(cursor.getString(cursor.getColumnIndexOrThrow(kontainer)));
                orm.setPlu(cursor.getString(cursor.getColumnIndexOrThrow(plu)));
                orm.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(barcode)));
                orm.setDescp(cursor.getString(cursor.getColumnIndexOrThrow(descp)));
                orm.setQty(cursor.getInt(cursor.getColumnIndexOrThrow(qty)));
                orm.setConv1(cursor.getInt(cursor.getColumnIndexOrThrow(conv1)));
                orm.setConv2(cursor.getInt(cursor.getColumnIndexOrThrow(conv2)));
                orm.setFlag(cursor.getString(cursor.getColumnIndexOrThrow(flag)));
                orm.setQtyScan(cursor.getInt(cursor.getColumnIndexOrThrow(qty_scan)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<ORMFakturDet> GetContainer(String srtJlnx, String kontainerx){
        List<ORMFakturDet> list = new ArrayList<>();
        String query = "select * from " + FAKTUR_DET + " where " + srt_jln + " ='" + srtJlnx + "' and " + kontainer + " ='" + kontainerx + "'";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                ORMFakturDet orm = new ORMFakturDet();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur(cursor.getString(cursor.getColumnIndexOrThrow(faktur)));
                orm.setKontainer(cursor.getString(cursor.getColumnIndexOrThrow(kontainer)));
                orm.setPlu(cursor.getString(cursor.getColumnIndexOrThrow(plu)));
                orm.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(barcode)));
                orm.setDescp(cursor.getString(cursor.getColumnIndexOrThrow(descp)));
                orm.setQty(cursor.getInt(cursor.getColumnIndexOrThrow(qty)));
                orm.setConv1(cursor.getInt(cursor.getColumnIndexOrThrow(conv1)));
                orm.setConv2(cursor.getInt(cursor.getColumnIndexOrThrow(conv2)));
                orm.setFlag(cursor.getString(cursor.getColumnIndexOrThrow(flag)));
                orm.setQtyScan(cursor.getInt(cursor.getColumnIndexOrThrow(qty_scan)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<ORMFakturDet> GetBarcodeDet(String barcodex, String srtJlnx, String kontainerx){
        List<ORMFakturDet> list = new ArrayList<>();
        String query = "select * from " + FAKTUR_DET + " where " + barcode + " ='" + barcodex + "' and " + srt_jln + " ='" + srtJlnx + "' and " + kontainer + " ='" + kontainerx + "'";
        Log.d(tag, query);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                ORMFakturDet orm = new ORMFakturDet();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur(cursor.getString(cursor.getColumnIndexOrThrow(faktur)));
                orm.setKontainer(cursor.getString(cursor.getColumnIndexOrThrow(kontainer)));
                orm.setPlu(cursor.getString(cursor.getColumnIndexOrThrow(plu)));
                orm.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(barcode)));
                orm.setDescp(cursor.getString(cursor.getColumnIndexOrThrow(descp)));
                orm.setQty(cursor.getInt(cursor.getColumnIndexOrThrow(qty)));
                orm.setConv1(cursor.getInt(cursor.getColumnIndexOrThrow(conv1)));
                orm.setConv2(cursor.getInt(cursor.getColumnIndexOrThrow(conv2)));
                orm.setFlag(cursor.getString(cursor.getColumnIndexOrThrow(flag)));
                orm.setQtyScan(cursor.getInt(cursor.getColumnIndexOrThrow(qty_scan)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<ORMFakturDet> GetPluDetAll(String plux, String srtJlnx){
        List<ORMFakturDet> list = new ArrayList<>();
        String query = "select * from " + FAKTUR_DET + " where " + plu + " ='" + plux + "' and " + srt_jln + " ='" + srtJlnx + "'";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                ORMFakturDet orm = new ORMFakturDet();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur(cursor.getString(cursor.getColumnIndexOrThrow(faktur)));
                orm.setKontainer(cursor.getString(cursor.getColumnIndexOrThrow(kontainer)));
                orm.setPlu(cursor.getString(cursor.getColumnIndexOrThrow(plu)));
                orm.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(barcode)));
                orm.setDescp(cursor.getString(cursor.getColumnIndexOrThrow(descp)));
                orm.setQty(cursor.getInt(cursor.getColumnIndexOrThrow(qty)));
                orm.setConv1(cursor.getInt(cursor.getColumnIndexOrThrow(conv1)));
                orm.setConv2(cursor.getInt(cursor.getColumnIndexOrThrow(conv2)));
                orm.setFlag(cursor.getString(cursor.getColumnIndexOrThrow(flag)));
                orm.setQtyScan(cursor.getInt(cursor.getColumnIndexOrThrow(qty_scan)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void updatePlu(String plux, String srtJlnx, String kontainerx, int qtyx){
        try{
            Log.d("TAG", "plux: "+plux);
            Log.d("TAG", "srtJlnx: "+srtJlnx);
            Log.d("TAG", "kontainerx: "+kontainerx);
            Log.d("TAG", "qtyx: "+qtyx);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(srt_jln, srtJlnx);
            values.put(plu, plux);
            values.put(kontainer, kontainerx);
            values.put(qty_scan, qtyx);
            db.update(FAKTUR_DET, values, srt_jln +"=? and " + kontainer + "=? and " + plu + "=? ", new String[]{srtJlnx,kontainerx,plux});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //----------------------------------SRT_JLN_HEAD-----------------------------------
    public void DeleteAllSrtJln(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SRT_JLN_HEAD, null,null);
    }

    public void InsertSrtJln(ORMSrtJln orm){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(srt_jln, orm.getSrtJln());
            values.put(faktur, orm.getFaktur());
            values.put(order_date, orm.getOrderDate());
            values.put(status_proses, orm.getStatusProses());
            int id = (int)db.insertWithOnConflict(SRT_JLN_HEAD, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if(id == -1){
                //db.update(SRT_JLN_HEAD, values, status_proses + "=?", new String[]{orm.getStatusProses()});
                db.update(SRT_JLN_HEAD, values, status_proses + "='BELUM'", null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void UpdateStatus(String srtJln){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(status_proses, "PROSES");
            db.update(SRT_JLN_HEAD,values,srt_jln + "=? ", new String[]{srtJln});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<ORMSrtJln> GetSrtJln(){
        List<ORMSrtJln> list = new ArrayList<>();
        String query = "select * from " + SRT_JLN_HEAD;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                ORMSrtJln orm = new ORMSrtJln();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur(cursor.getString(cursor.getColumnIndexOrThrow(faktur)));
                orm.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow(order_date)));
                orm.setStatusProses(cursor.getString(cursor.getColumnIndexOrThrow(status_proses)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Cursor fakturSrtJln(String srtJln){
        String query = "select distinct " + faktur + " from " + SRT_JLN_HEAD + " where " + srt_jln + " = '" + srtJln + "'";
        Log.d(tag, query);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query,null);
    }

    public List<ORMSrtJln> disSrtJln(){
        List<ORMSrtJln> list = new ArrayList<>();
        //String query = "select distinct " + srt_jln + "," + order_date + "," + status_proses + " from " + SRT_JLN_HEAD;
        String query = "select * from " + SRT_JLN_HEAD + " group by " + srt_jln;
        Log.d(tag, query);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        Log.d(tag,"size : " + cursor.getCount());
        if(cursor.moveToFirst()){
            do {
                ORMSrtJln orm = new ORMSrtJln();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur("");
                orm.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow(order_date)));
                orm.setStatusProses(cursor.getString(cursor.getColumnIndexOrThrow(status_proses)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<ORMSrtJln> getSrtJln2(String srtJlnx){
        List<ORMSrtJln> list = new ArrayList<>();
        //String query = "select distinct " + srt_jln + "," + order_date + "," + status_proses + " from " + SRT_JLN_HEAD;
        String query = "select * from " + SRT_JLN_HEAD + " where " + srt_jln + " = '" + srtJlnx + "'";
        Log.d(tag, query);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        Log.d(tag,"size : " + cursor.getCount());
        if(cursor.moveToFirst()){
            do {
                ORMSrtJln orm = new ORMSrtJln();
                orm.setSrtJln(cursor.getString(cursor.getColumnIndexOrThrow(srt_jln)));
                orm.setFaktur("");
                orm.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow(order_date)));
                orm.setStatusProses(cursor.getString(cursor.getColumnIndexOrThrow(status_proses)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    //----------------------------------MASTER_STORE-----------------------------------
    public void DeleteAllStore(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MASTER_STORE, null,null);
    }

    public void InsertMsStore(ORMMsStore orm){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(store_dcid, orm.getDcId());
            values.put(store_id, orm.getStoreId());
            db.insertWithOnConflict(MASTER_STORE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<ORMMsStore> GetStore(){
        List<ORMMsStore> list = new ArrayList<>();
        String query = "select * from " + MASTER_STORE;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                ORMMsStore orm = new ORMMsStore();
                orm.setDcId(cursor.getString(cursor.getColumnIndexOrThrow(store_dcid)));
                orm.setStoreId(cursor.getString(cursor.getColumnIndexOrThrow(store_id)));
                list.add(orm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
