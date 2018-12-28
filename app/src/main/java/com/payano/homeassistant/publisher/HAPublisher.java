package com.payano.homeassistant.publisher;



import com.payano.homeassistant.messages.*;
import com.payano.homeassistant.subscribers.*;

import java.util.ArrayList;
import java.util.List;

abstract public class HAPublisher {
    private List<HAEventSubscriber> eventSubscribers = new ArrayList<>();
    public void subscribeEvents(HAEventSubscriber subscriber){ eventSubscribers.add(subscriber); }
    public void unsubscribeEvents(HAEventSubscriber subscriber){ eventSubscribers.remove(subscriber); }
    protected void publishEvents(EventMessage msg){
        List<HAEventSubscriber> nullSubscribers = new ArrayList<>();
        for(int i = 0 ; i < eventSubscribers.size();++i){
            HAEventSubscriber subscriber = eventSubscribers.get(i);
            // if the subscriber is null, remove it from the list.
            if(subscriber != null){
                eventSubscribers.remove(subscriber);
                subscriber.notifyEvent(msg);
            }else{
                nullSubscribers.add(subscriber);
            }
        }
        // Remove if there is null subscribers
        for (int i = 0 ; i < nullSubscribers.size(); ++i){
            eventSubscribers.remove(nullSubscribers.get(i));
        }
    }

    /*
    List<HAServiceSubscriber> serviceSubscribers = new ArrayList<>();
    List<HAStateSubscriber> stateSubscribers = new ArrayList<>();
    List<HAConfigSubscriber> configSubscribers = new ArrayList<>();
    List<HAPanelSubscriber> panelsSubscribers = new ArrayList<>();


    List<HACameraSubscriber> cameraSubscribers = new ArrayList<>();
    List<HAThumbnailsSubscriber> thumbnailsSubscribers = new ArrayList<>();
    public void subscribeServices(HAServiceSubscriber subscriber){ serviceSubscribers.add(subscriber); }
    public void subscribeStates(HAStateSubscriber subscriber){ stateSubscribers.add(subscriber); }
    public void subscribeConfig(HAConfigSubscriber subscriber){ configSubscribers.add(subscriber);}
    public void subscribePanels(HAPanelSubscriber subscriber){ panelsSubscribers.add(subscriber); }

    public void subscribeCamera(HACameraSubscriber subscriber){ cameraSubscribers.add(subscriber); }
    public void subscribeThumbnails(HAThumbnailsSubscriber subscriber) { thumbnailsSubscribers.add(subscriber); }
    public void unsubscribeServices(HAServiceSubscriber subscriber){ serviceSubscribers.remove(subscriber); }
    public void unsubscribeStates(HAStateSubscriber subscriber){ stateSubscribers.remove(subscriber); }
    public void unsubscribeConfig(HAConfigSubscriber subscriber){ configSubscribers.remove(subscriber); }
    public void unsubscribePanels(HAPanelSubscriber subscriber) { panelsSubscribers.remove(subscriber); }
    public void unsubscribeCamera(HACameraSubscriber subscriber){ cameraSubscribers.remove(subscriber); }

    public void unsubscribeThumbnails(HAThumbnailsSubscriber subscriber){ thumbnailsSubscribers.remove(subscriber); }
    public void publishervices(ServiceMessage msg){
        for (HAServiceSubscriber subscriber:serviceSubscribers) {subscriber.notifyService(msg);}
    }
    public void publishStates(StateMessage msg){
        for (HAStateSubscriber subscriber:stateSubscribers) {subscriber.notifyState(msg);}
    }
    public void publishConfig(ConfigMessage msg){
        for (HAConfigSubscriber subscriber:configSubscribers) {subscriber.notifyConfig(msg);}
    }
    public void publishPanels(PanelMessage msg) {
        for (HAPanelSubscriber subscriber:panelsSubscribers) {subscriber.notifyPanel(msg);}
    }
    public void publishCamera(CameraMessage msg){
        for (HACameraSubscriber subscriber:cameraSubscribers) {subscriber.notifyCamera(msg);}
    }
    public void publishThumbnails(ThumbnailMessage msg){
        for (HAThumbnailsSubscriber subscriber:thumbnailsSubscribers) {subscriber.notifyThumbnail(msg);}
    }
    */
}