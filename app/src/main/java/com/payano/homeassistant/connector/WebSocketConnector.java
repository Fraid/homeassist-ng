package com.payano.homeassistant.connector;

import okhttp3.*;
import okio.ByteString;

import org.json.JSONException;
import org.json.JSONObject;

public class WebSocketConnector extends BaseConnector {

    private OkHttpClient mClient;
    private WebSocket mWebSocket;
    private boolean mConnected;
    private WebSocketConfig mConfig;
    private static final String uriAppend = "/api/websocket";

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {}

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            incomingMsg(text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            incomingMsg(bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
            teardown();
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
            teardown();
        }
    }

    public WebSocketConnector(WebSocketConfig config){
        this.mConfig = config;
        mConfig.uri += uriAppend;
    }

    @Override
    public void setup() {
        if(mClient != null){return;}

        mClient = new OkHttpClient();
        Request request = new Request.Builder().url(mConfig.uri).build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        mWebSocket = mClient.newWebSocket(request, listener);
    }

    @Override
    public void waitForConnect(int delay, int retries){
        try {
            int multiplier = 1;

            for(int i = 0 ; i < retries; ++i){
                Thread.sleep(delay * multiplier);
                if(mConnected){return;}
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void teardown(){
        if(mWebSocket == null){return;}
        mWebSocket.close(1000, "Closing");
        mClient.dispatcher().executorService().shutdown();

        mConnected = false;
        mWebSocket = null;
        mClient = null;
    }

    private void output(final String txt){
        System.out.println(txt + "\n");

    }

    public boolean isConnected(){
        return mConnected;
    }

    @Override
    public void ping() {
        // do something with the ping

    }

    private void incomingMsg(String txt) {
        // Create JSON
        try {
            JSONObject incoming = new JSONObject(txt);
            // System.out.println("id: " + Thread.currentThread().getId() + " " + txt + "\n");

            // This is the setup phase
            if (incoming.get("type").equals("auth_required")) {
                JSONObject send = new JSONObject();
                send.put("type", "auth");
                if (!mConfig.api_password.isEmpty()) {
                    send.put("api_password", mConfig.api_password);
                } else {
                    send.put("auth_token", mConfig.auth_token);
                }
                sendMsg(send);
                return;
            } else if (incoming.get("type").equals("auth_invalid")) {
                // Failed with password close connection.
                // Signal to observers that we are down.
                teardown();
                return;
            } else if (incoming.get("type").equals("auth_ok")) {
                // Ready to subscribe and publish.
                JSONObject send = new JSONObject();
                send.put("id", 18);
                send.put("type", "subscribe_events");
                send.put("event_type", "state_changed");
                sendMsg(send);
                return;
            } else if (
                    incoming.has("success") &&
                            incoming.get("success").equals(true) &&
                            incoming.get("id").equals(18) &&
                            incoming.get("type").equals("result")
                    ) {
                mConnected = true;
                return;
            }


            // Notify observers
            setChanged();
            notifyObservers(incoming);
            clearChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void sendMsg(JSONObject sendMsg){
        // System.out.println("SEND: " + sendMsg.toString());
        mWebSocket.send(sendMsg.toString());
    }
}



