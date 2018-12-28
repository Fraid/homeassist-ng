package com.payano.homeassistant.connector;

import org.json.JSONObject;

import java.util.Observable;

public abstract class BaseConnector extends Observable {

    public abstract void sendMsg(JSONObject msg);
    public abstract void setup();
    public abstract void waitForConnect(int delay, int retries);
    public abstract void teardown();
    public abstract boolean isConnected();
    public abstract void ping();


}