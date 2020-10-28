package com.example.smartfarming.data;

public class NodeData {
    private int number;
    private String mRainPeriod;
    private float mPerTur;
    private int mLoss;

    public NodeData(int number, String mRainPeriod, float mPerTur, int mLoss) {
        this.number = number;
        this.mRainPeriod = mRainPeriod;
        this.mPerTur = mPerTur;
        this.mLoss = mLoss;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getmRainPeriod() {
        return mRainPeriod;
    }

    public void setmRainPeriod(String mRainPeriod) {
        this.mRainPeriod = mRainPeriod;
    }

    public float getmPerTur() {
        return mPerTur;
    }

    public void setmPerTur(float mPerTur) {
        this.mPerTur = mPerTur;
    }

    public int getmLoss() {
        return mLoss;
    }

    public void setmLoss(int mLoss) {
        this.mLoss = mLoss;
    }
}
