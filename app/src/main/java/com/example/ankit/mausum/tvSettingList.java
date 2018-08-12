package com.example.ankit.mausum;

public class tvSettingList {
    String topic;
    Double value;

    public String getTopic() {
        return topic;
    }

    public Double getValue() {
        return value;
    }

    public tvSettingList(String topic, Double value) {

        this.topic = topic;
        this.value = value;
    }
}
