package com.visualwallet.entity;

/**
 * 生成分存图用于返回前端的数据结构
 */
public class GenerateResponse {
    int[][][] keyMatrix;
    int[][] randList;
    int splitMatSize;
    int carrierMatSize;

    public GenerateResponse(int[][][] keyMatrix, int[][] randList, int splitMatSize, int carrierMatSize) {
        this.keyMatrix = keyMatrix;
        this.randList = randList;
        this.splitMatSize = splitMatSize;
        this.carrierMatSize = carrierMatSize;
    }

    public int[][][] getKeyMatrix() {
        return keyMatrix;
    }

    public void setKeyMatrix(int[][][] keyMatrix) {
        this.keyMatrix = keyMatrix;
    }

    public int[][] getRandList() {
        return randList;
    }

    public void setRandList(int[][] randList) {
        this.randList = randList;
    }

    public int getSplitMatSize() {
        return splitMatSize;
    }

    public void setSplitMatSize(int splitMatSize) {
        this.splitMatSize = splitMatSize;
    }

    public int getCarrierMatSize() {
        return carrierMatSize;
    }

    public void setCarrierMatSize(int carrierMatSize) {
        this.carrierMatSize = carrierMatSize;
    }
}
