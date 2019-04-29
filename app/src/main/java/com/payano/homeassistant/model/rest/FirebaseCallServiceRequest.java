package com.payano.homeassistant.model.rest;


import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Keep the data same as Nodered
 */
public class FirebaseCallServiceRequest {

    @SerializedName("domain")
    public String domain;
    @SerializedName("service")
    public String service;
    @SerializedName("data")
    public CallData data;

    public FirebaseCallServiceRequest(String service, CallServiceRequest request) {
        domain = request.entityId.split("\\.")[0];
        this.service = service;
        data = new CallData();
        data.rgbColor = request.rgbColor;
        data.colorTemp = request.colorTemp;
        data.temperature = toDouble(request.temperature);
        data.brightness = request.brightness;
        data.isVolumeMuted = request.isVolumeMuted;
        data.date = request.date;
        data.speed = request.speed;
        data.fanSpeed = request.fanSpeed;
        data.time = request.time;
        data.message = request.message;
        data.entityId = request.entityId;
        data.option = request.option;
        data.value = request.value;
        data.code = request.code;
        data.rgbColor = request.rgbColor;
        data.volumeLevel = toDouble(request.volumeLevel);
        data.colorTemp = request.colorTemp;
        data.temperature = toDouble(request.temperature);
        data.brightness = request.brightness;
        data.isVolumeMuted = request.isVolumeMuted;
        data.date = request.date;
        data.speed = request.speed;
        data.fanSpeed = request.fanSpeed;
        data.time = request.time;
        data.message = request.message;
    }

    private static Double toDouble(BigDecimal val) {
        if (val == null) return null;
        return val.doubleValue();
    }


    public static class CallData {
        @SerializedName("entity_id")
        public String entityId;

        @SerializedName("option")
        public String option;

        @SerializedName("value")
        public String value;

        @SerializedName("code")
        public String code;

        @SerializedName("rgb_color")
        public Integer[] rgbColor;

        @SerializedName("volume_level")
        public Double volumeLevel;

        @SerializedName("brightness")
        public Integer brightness;

        @SerializedName("color_temp")
        public Integer colorTemp;

        @SerializedName("temperature")
        public Double temperature;

        @SerializedName("volume_mute")
        public Boolean isVolumeMuted;

        @SerializedName("date")
        public String date;

        @SerializedName("set_fan_mode")
        public String speed;

        @SerializedName("set_fan_speed")
        public String fanSpeed;

        @SerializedName("time")
        public String time;

        @SerializedName("message")
        public String message;

    }
}
