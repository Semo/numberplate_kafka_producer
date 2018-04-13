package dev.semo.kafkaeskadapter.models.channels.numberplates;

import java.io.Serializable;

public class NumberPlate implements Serializable {

    private String numberString;
    private Byte[] imageBlob;

    public NumberPlate() {
    }

    public String getNumberString() {
        return numberString;
    }

    public void setNumberString(String numberString) {
        this.numberString = numberString;
    }

    public Byte[] getImageBlob() {
        return imageBlob;
    }

    public void setImageBlob(Byte[] imageBlob) {
        this.imageBlob = imageBlob;
    }
}
