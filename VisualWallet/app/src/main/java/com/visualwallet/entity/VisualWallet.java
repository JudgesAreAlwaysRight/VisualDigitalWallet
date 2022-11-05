package com.visualwallet.entity;

import java.io.Serializable;

public class VisualWallet implements Serializable {
    private String address;
    private int coeK;
    private int coeN;
    private int coeF;
    private String curType;
    private String walName;
    private int id;

    public VisualWallet(String address, int coeK, int coeN, int coeF, String curType, String walName) {
        this.address = address;
        this.coeK = coeK;
        this.coeN = coeN;
        this.coeF = coeF;
        this.curType = curType;
        this.walName = walName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCoeK() {
        return coeK;
    }

    public void setCoeK(int coeK) {
        this.coeK = coeK;
    }

    public int getCoeN() {
        return coeN;
    }

    public void setCoeN(int coeN) {
        this.coeN = coeN;
    }

    public String getCurType() {
        return curType;
    }

    public void setCurType(String curType) {
        this.curType = curType;
    }

    public String getWalName() {
        return walName;
    }

    public void setWalName(String walName) {
        this.walName = walName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoeF() {
        return coeF;
    }

    public void setCoeF(int coeF) {
        this.coeF = coeF;
    }
}
