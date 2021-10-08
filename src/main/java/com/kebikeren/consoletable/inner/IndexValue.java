package com.kebikeren.consoletable.inner;

/**
 * @Classname IndexValue
 * @Description TODO
 * @Date 2021-10-08
 * @Created by kebikeren
 */
public class IndexValue {
    private int index;
    private int value;
    private double ratio;

    public IndexValue(int index, int value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public int getValue() {
        return value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
