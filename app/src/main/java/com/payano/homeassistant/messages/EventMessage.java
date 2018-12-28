package com.payano.homeassistant.messages;

import org.json.JSONObject;

public class EventMessage {
    public String event_type;
    public String entity_id;
    public String state;
    public JSONObject attributes;

    @Override
    public String toString() {
        return "EventMessage{" +
                "event_type='" + event_type + '\'' +
                ", entity_id='" + entity_id + '\'' +
                ", state='" + state + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
