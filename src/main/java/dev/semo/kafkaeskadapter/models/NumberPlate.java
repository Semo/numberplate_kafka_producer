package dev.semo.kafkaeskadapter.models;

import java.io.Serializable;
import java.util.Arrays;

public class NumberPlate implements Serializable {

    private String numberString;
    private byte[] imageBlob;

    public NumberPlate() {
    }

    public String getNumberString() {
        return numberString;
    }

    public void setNumberString(String numberString) {
        this.numberString = numberString;
    }

    public byte[] getImageBlob() {
        return imageBlob;
    }

    public void setImageBlob(byte[] imageBlob) {
        this.imageBlob = imageBlob;
    }

    @Override
    public String toString() {
        return "NumberPlate{" +
                "numberString='" + numberString + '\'' +
                ", imageBlob=" + Arrays.toString(imageBlob) +
                '}';
    }
}
