package com.payano.homeassistant.subscribers;

import com.payano.homeassistant.messages.ConfigMessage;

abstract public class HAConfigSubscriber{
    abstract public void notifyConfig(ConfigMessage msg);
}