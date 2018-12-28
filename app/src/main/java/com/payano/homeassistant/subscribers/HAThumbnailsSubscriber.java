package com.payano.homeassistant.subscribers;

import com.payano.homeassistant.messages.ThumbnailMessage;

abstract public class HAThumbnailsSubscriber{
    abstract public void notifyThumbnail(ThumbnailMessage msg);
}
