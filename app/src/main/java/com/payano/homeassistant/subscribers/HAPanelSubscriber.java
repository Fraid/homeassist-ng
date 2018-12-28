package com.payano.homeassistant.subscribers;

import com.payano.homeassistant.messages.PanelMessage;

abstract public class HAPanelSubscriber{
    abstract public void notifyPanel(PanelMessage msg);
}