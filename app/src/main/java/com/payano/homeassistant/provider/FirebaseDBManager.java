package com.payano.homeassistant.provider;

import android.app.Activity;
import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.payano.homeassistant.IViewUpdate;
import com.payano.homeassistant.model.FirebaseRawEntity;
import com.payano.homeassistant.model.rest.CallServiceRequest;
import com.payano.homeassistant.model.rest.FirebaseCallServiceRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDBManager {
    private static String HA_ROOT = "homeassistant";
    private static String HA_ANDROID_EVENT = HA_ROOT + "/android/event";
    private static String HA_USER_EVENT = HA_ANDROID_EVENT + "/user";
    private static String HA_STATES = HA_ROOT + "/states";
    private static String HA_POST_REQUEST = HA_ANDROID_EVENT + "/request";

    /**
     * Will fetch all data when return everything to viewUpdate.onInitDataReady
     *
     * @param viewUpdate
     */
    public static void fetchInitData(final IViewUpdate viewUpdate) {

        FirebaseDatabase
                .getInstance()
                .getReference(HA_STATES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //On time called.
                        final ArrayList<ContentValues> values = new ArrayList<>();

                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            try {
                                FirebaseRawEntity entity = messageSnapshot.getValue(FirebaseRawEntity.class);
                                if (entity == null) continue;
                                values.add(entity.getContentValues());
                            } catch (Exception e) {
                                System.out.println(">>>> " + messageSnapshot.getValue());
                            }
                        }
                        viewUpdate.onInitDataReady(values);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static ChildEventListener startListenToChild(final Activity activity) {
        return FirebaseDatabase
                .getInstance()
                .getReference(HA_STATES)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        FirebaseRawEntity entity = dataSnapshot.getValue(FirebaseRawEntity.class);
                        if (entity == null) return;
                        activity.getContentResolver().insert(EntityContentProvider.getUrl(), entity.getContentValues());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        FirebaseRawEntity entity = dataSnapshot.getValue(FirebaseRawEntity.class);
                        if (entity == null) return;
                        activity.getContentResolver()
                                .update(EntityContentProvider.getUrl()
                                        , entity.getContentValues()
                                        , "ENTITY_ID=?"
                                        , new String[]{entity.entityId});
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseRawEntity entity = dataSnapshot.getValue(FirebaseRawEntity.class);
                        if (entity == null) return;

                        activity.getContentResolver()
                                .delete(EntityContentProvider.getUrl()
                                        , "ENTITY_ID=?"
                                        , new String[]{entity.entityId});
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private static Map<String, Object> removeNullValues(FirebaseCallServiceRequest userObject) {
        Gson gson = new GsonBuilder().create();
        Map<String, Object> map = new Gson().fromJson(
                gson.toJson(userObject), new TypeToken<HashMap<String, Object>>() {
                }.getType()
        );

        return map;
    }

    /**
     * Will execute any action like on/off status
     *
     * @param service
     * @param callServiceRequest
     */
    public static void callService(String service, CallServiceRequest callServiceRequest) {
        //Basically here I added an entry in database under "homeassistant/request"
        FirebaseDatabase
                .getInstance()
                .getReference(HA_POST_REQUEST)
                .setValue(removeNullValues(callServiceRequest.toFbCallServiceRequest(service)))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Here we just know that the request as been sent!
                        // Node-red will switch the light and then update the stats which will trigger the childEventLister.
                        if (task.getException() != null) {
                            // TODO we should handler error on issue
                        }

                    }
                });
    }

    /**
     * Just a heartbeat like. No need to refreshing everything.
     *
     * @param previousListener
     */
    public static void removeEventListener(ChildEventListener previousListener) {
        FirebaseDatabase
                .getInstance()
                .getReference(HA_STATES)
                .removeEventListener(previousListener);
    }

    public static void refresh(final IViewUpdate viewUpdate) {
        FirebaseDatabase.getInstance()
                .getReference(HA_USER_EVENT + "/onAppStart")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        fetchInitData(viewUpdate);
                    }
                });
    }
    public static void onAppStart(final IViewUpdate viewUpdate) {
        FirebaseDatabase.getInstance().goOnline();
        refresh(viewUpdate);
    }

    /**
     * Will stop nodered sync to firebase.
     */
    public static void stopOnlineSync() {
        setUserIsOnline(false);
        FirebaseDatabase.getInstance().goOffline();
    }

    private static void setUserIsOnline(boolean value) {
        FirebaseDatabase.getInstance().getReference(HA_USER_EVENT + "/isOnline").setValue(value);
    }

}
