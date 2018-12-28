package com.payano.homeassistant.subscribers;

import com.payano.homeassistant.messages.StateMessage;

abstract public class HAStateSubscriber{
    abstract public void notifyState(StateMessage msg);
}