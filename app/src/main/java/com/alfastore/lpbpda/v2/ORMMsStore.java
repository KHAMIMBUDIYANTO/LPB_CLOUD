package com.alfastore.lpbpda.v2;

public class ORMMsStore {
    private String storeId;
    private String dcId;

    public ORMMsStore(){}

    public ORMMsStore(String dcId,String storeId){
        this.storeId = storeId;
        this.dcId = dcId;
    }

    public String getStoreId(){
        return storeId;
    }

    public void setStoreId(String storeId){
        this.storeId = storeId;
    }

    public String getDcId(){
        return dcId;
    }

    public void setDcId(String dcId){
        this.dcId = dcId;
    }
}
