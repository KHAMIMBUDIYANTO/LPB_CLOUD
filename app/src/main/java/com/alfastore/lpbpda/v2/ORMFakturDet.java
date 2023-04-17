package com.alfastore.lpbpda.v2;

public class ORMFakturDet {
    private String srtJln;
    private String faktur;
    private String kontainer;
    private String plu;
    private String barcode;
    private String descp;
    private Integer qty;
    private Integer conv1;
    private Integer conv2;
    private String flag;
    private Integer qtyScan;

    /**
     * No args constructor for use in serialization
     *
     */
    public ORMFakturDet() {
    }

    /**
     *
     * @param qtyScan
     * @param flag
     * @param faktur
     * @param qty
     * @param plu
     * @param conv2
     * @param conv1
     * @param kontainer
     * @param barcode
     * @param descp
     */
    public ORMFakturDet(String srtJln, String faktur, String kontainer, String plu, String barcode, String descp, Integer qty, Integer conv1, Integer conv2, String flag, Integer qtyScan) {
        super();
        this.srtJln = srtJln;
        this.faktur = faktur;
        this.kontainer = kontainer;
        this.plu = plu;
        this.barcode = barcode;
        this.descp = descp;
        this.qty = qty;
        this.conv1 = conv1;
        this.conv2 = conv2;
        this.flag = flag;
        this.qtyScan = qtyScan;
    }

    public String getSrtJln() {
        return srtJln;
    }

    public void setSrtJln(String srtJln) {
        this.srtJln = srtJln;
    }

    public String getFaktur() {
        return faktur;
    }

    public void setFaktur(String faktur) {
        this.faktur = faktur;
    }

    public String getKontainer() {
        return kontainer;
    }

    public void setKontainer(String kontainer) {
        this.kontainer = kontainer;
    }

    public String getPlu() {
        return plu;
    }

    public void setPlu(String plu) {
        this.plu = plu;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getConv1() {
        return conv1;
    }

    public void setConv1(Integer conv1) {
        this.conv1 = conv1;
    }

    public Integer getConv2() {
        return conv2;
    }

    public void setConv2(Integer conv2) {
        this.conv2 = conv2;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Integer getQtyScan() {
        return qtyScan;
    }

    public void setQtyScan(Integer qtyScan) {
        this.qtyScan = qtyScan;
    }

}