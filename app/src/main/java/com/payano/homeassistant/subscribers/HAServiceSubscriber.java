package com.payano.homeassistant.subscribers;

import com.payano.homeassistant.messages.ServiceMessage;

abstract public class HAServiceSubscriber{
    abstract public void notifyService(ServiceMessage msg);
}