package com.payano.homeassistant.manager;

import com.payano.homeassistant.connector.BaseConnector;
import com.payano.homeassistant.connector.WebSocketConfig;
import com.payano.homeassistant.connector.WebSocketConnector;
import com.payano.homeassistant.messages.EventMessage;
import com.payano.homeassistant.publisher.HAPublisher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Subscribe to events
 * Unsubscribing from events
 * Calling a service
 * Fetching states
 * Fetching config
 * Fetching services
 * Fetching panels
 * Fetching camera thumbnails
 * Fetching media player thumbnails
 */
public class HAConnectionManager extends HAPublisher implements Runnable, Observer {
    private static final int CONNECT_DELAY_MS = 200;
    private static final int CONNECT_RETRY_COUNT = 4;

    private static HAConnectionManager mInstance;

    private boolean mStarted;
    private Thread mThread;
    private BaseConnector mConnector;

    private HAConnectionManager(){}

    public static HAConnectionManager getInstance(){
        if(mInstance == null){
            mInstance = new HAConnectionManager();
        }
        return mInstance;
    }

    public void startInstance(){
        if(mStarted) {return;}
        mStarted = true;
        mThread = new Thread((this));
        mThread.start();
    }
    public void stopInstance(){
        mStarted = false;
    }

    public boolean isRunning(){ return mStarted;}

    public void joinThread(){
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {

        // Check if HA websocket is running
        WebSocketConfig webSocketConfig = new WebSocketConfig();
        webSocketConfig.uri = "http://192.168.1.138:8123";
        webSocketConfig.api_password = "password123";

        WebSocketConnector wc = new WebSocketConnector(webSocketConfig);
        wc.addObserver(this);
        HashMap<BaseConnector, Boolean> connectors = new HashMap<>();
        connectors.put(wc, Boolean.valueOf(false));

        while(mStarted) {
            boolean connected = false;
            // Check if a connection is available.
            for (Map.Entry<BaseConnector, Boolean> entry : connectors.entrySet()) {
                if (entry.getKey().isConnected()) {
                    entry.getKey().ping();
                    connected = true;
                    break;
                }
            }

            // if something has been disconnected, do the teardown process:
            if(!connected) {
                for (Map.Entry<BaseConnector, Boolean> entry : connectors.entrySet()) {
                    if (entry.getKey().isConnected() != entry.getValue()) {
                        // State has changed
                        if (!entry.getKey().isConnected()) {
                            // Falling edge
                            System.out.println("entry.getKey().teardown()");
                            // Do a proper teardown if we lost connection.
                            entry.getKey().teardown();
                        } else {
                            // Rising edge
                            System.out.println("ODD");
                        }
                    }
                }
            }

            // if no connection is available, try them all.
            if(!connected) {
                for (Map.Entry<BaseConnector, Boolean> entry : connectors.entrySet()) {
                    // Try to connect.
                    System.out.println("entry.getKey().setup()");

                    entry.getKey().setup();
                    entry.getKey().waitForConnect(CONNECT_DELAY_MS, 4);

                    if (entry.getKey().isConnected()) {
                        // if connection succeeded, break the connect to loop.
                        System.out.println("entry.getKey().isConnected()");
                        break;
                    }
                }
            }

            // Copy current state to last state
            for (Map.Entry<BaseConnector, Boolean> entry : connectors.entrySet()) {
                entry.setValue(entry.getKey().isConnected());
            }

            // if something is connected, wait and check again.
            int ms = 10000; // 10 sec
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        // End of HA Connection Manager
        for (Map.Entry<BaseConnector, Boolean> entry : connectors.entrySet()) {
            entry.getKey().teardown();
        }

        System.out.println("CLOSE");
        mStarted = false;
        mInstance = null;
    }

    @Override
    public void update(Observable o, Object arg) {
        // The connectors will publish events here.
        // JSON objects will always be sent
        publishMessage(arg);    }

    private void publishMessage(Object arg){
        JSONObject msg = (JSONObject) arg;
        try {

            if (msg.get("type").equals("event")) {
                JSONObject event = (JSONObject) msg.get("event");
                if (event == null) {
                    return;
                }

                String event_type = (String) event.get("event_type");

                JSONObject data = (JSONObject) event.get("data");
                if (data == null) {
                    return;
                }

                String entity_id = (String) data.get("entity_id");

                JSONObject new_state = (JSONObject) data.get("new_state");
                if (new_state == null) {
                    return;
                }

                String state = (String) new_state.get("state");
                JSONObject attributes = (JSONObject) new_state.get("attributes");

                // Convert to eventMessage
                EventMessage newMsg = new EventMessage();
                newMsg.event_type = event_type;
                newMsg.entity_id = entity_id;
                newMsg.state = state;
                newMsg.attributes = attributes;

                //System.out.println(newMsg.toString());
                publishEvents(newMsg);
                return;
            }

            // Unhandled message
            System.out.println(msg.toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public boolean testConnector(BaseConnector bc){
        bc.setup();
        bc.waitForConnect(CONNECT_DELAY_MS,CONNECT_RETRY_COUNT);
        boolean connectStatus = bc.isConnected();
        bc.teardown();
        return connectStatus;

    }
}
