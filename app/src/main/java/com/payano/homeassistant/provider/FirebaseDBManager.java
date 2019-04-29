package com.payano.homeassistant.provider;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.payano.homeassistant.model.Entity;
import com.payano.homeassistant.model.FirebaseEntity;
import com.payano.homeassistant.model.rest.CallServiceRequest;
import com.payano.homeassistant.model.rest.FirebaseCallServiceRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDBManager {
    private static String HA_ROOT = "homeassisant";

    public static void syncWithFireBase(ArrayList<Entity> statesResponse) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, FirebaseEntity> map = new HashMap<>();
        for (Entity entity : statesResponse) {
            FirebaseEntity firebaseEntity = entity.toFBEntity();
            map.put(firebaseEntity.getFirebaseKey(), firebaseEntity);
        }
        database.getReference(HA_ROOT).setValue(map);

    }

    /**
     * Should be call one time only when user switch to FirebaseMode
     */
    public static void waitOnStatesChange() {

        FirebaseDatabase
                .getInstance()
                .getReference(HA_ROOT + "/states")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //Here any change under homeassistant/states will be here.
                        //Should dispatch to local db here
                        //getContentResolver().update.... etc for exemple.

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Remove null because node ned will failed if unused field.
     *
     * @param userObject
     * @return Map<String                                                                                                                               ,                                                                                                                                                                                                                                                               Object> without null field.
     */
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
                .getReference(HA_ROOT + "/request")
                .setValue(removeNullValues(callServiceRequest.toFbCallServiceRequest(service)))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // Here we just know that the request as been sent!
                        // Node-red will switch the light and then update the stats of "homeassistant/states/light_1" in Firebase.

                        // Because at init, we will call FirebaseDBManager.waitOnStatesChange,
                        // onDataChange will be fired with the following payload:
                        // "homeassistant/states/light_1"

                    }
                });
    }

    public static void stopFirebaseSync() {
        //Probably stop any listening and close database. Clear cache?
    }
}
