package com.payano.homeassistant.model;

import android.content.ContentValues;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.SerializedName;
import com.payano.homeassistant.util.CommonUtil;

/**
 * POJO Wapper for Firebase.
 * To speed up the process we can directly get the required raw data only.
 * Inside of "get data-> pojo -> toJson -> toSql"
 * we can "get data-> pojo -> toSql".
 */
public class FirebaseRawEntity {

    public FirebaseRawEntity() {

    }

    @PropertyName("friendly_name")
    @SerializedName("friendly_name")
    public String friendlyName;


    @PropertyName("entity_id")
    @SerializedName("entity_id")
    public String entityId;

    @SerializedName("domain")
    public String domain;

    @PropertyName("raw_data")
    @SerializedName("raw_data")
    public String rawJson;


    public ContentValues getContentValues() {
        ContentValues initialValues = new ContentValues();
        initialValues.put("ENTITY_ID", entityId);
        initialValues.put("FRIENDLY_NAME", friendlyName);
        initialValues.put("DOMAIN", domain);
        initialValues.put("RAW_JSON", rawJson);
        initialValues.put("CHECKSUM", CommonUtil.md5(rawJson));
        return initialValues;
    }


}