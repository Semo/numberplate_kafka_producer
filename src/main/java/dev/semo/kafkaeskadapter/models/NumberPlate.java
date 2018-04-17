package dev.semo.kafkaeskadapter.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberPlate that = (NumberPlate) o;
        return Objects.equals(numberString, that.numberString) &&
                Arrays.equals(imageBlob, that.imageBlob);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(numberString);
        result = 31 * result + Arrays.hashCode(imageBlob);
        return result;
    }

    @Override
    public String toString() {
        return "NumberPlate{" +
                "numberString='" + numberString + '\'' +
                ", imageBlob=" + Arrays.toString(imageBlob) +
                '}';
    }
}
