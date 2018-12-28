package com.payano.homeassistant.subscribers;

import com.payano.homeassistant.messages.EventMessage;

abstract public class HAEventSubscriber{
    abstract public void notifyEvent(EventMessage msg);
}