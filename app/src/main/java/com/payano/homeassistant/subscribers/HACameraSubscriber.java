package com.payano.homeassistant.subscribers;
import com.payano.homeassistant.messages.CameraMessage;

abstract public class HACameraSubscriber{
    abstract public void notifyCamera(CameraMessage msg);
}
