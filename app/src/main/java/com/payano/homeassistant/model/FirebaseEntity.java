package com.payano.homeassistant.model;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

/**
 * POJO Wapper for Firebase.
 */
public class FirebaseEntity {
    @SerializedName("entity_id")
    public String entityId;

    //on
    @SerializedName("state")
    public String state;

    //2017-08-14T15:50:46.810842+00:00
    @SerializedName("last_updated")
    public String lastUpdated;

    //2017-08-14T15:50:46.810842+00:00
    @SerializedName("last_changed")
    public String lastChanged;

    @SerializedName("attributes")
    public Attribute attributes;

    @SerializedName("checksum")
    public String checksum;

    @SerializedName("displayOrder")
    public Integer displayOrder;

    public Boolean isAction = false;

    public FirebaseEntity(Entity entity) {
        this.entityId = entity.entityId;
        this.state = entity.state;
        this.lastUpdated = entity.lastUpdated;
        this.lastChanged = entity.lastChanged;
        this.attributes = entity.attributes;
        this.checksum = entity.checksum;
        this.displayOrder = entity.displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (!(o instanceof FirebaseEntity))
            return false;

        final FirebaseEntity other = (FirebaseEntity) o;
//        if (this.checksum == null || other.checksum == null) {
//            throw new RuntimeException("checksum is null");
//        }
        return this.entityId.equals(other.entityId);
    }

    @Exclude
    public String getFirebaseKey() {//Keys must not contain '/', '.', '#', '$', '[', or ']'
        return toFireBaseKey(entityId);
    }

    public static String toFireBaseKey(String entityId) {
        return entityId.replace(".", "__")
                ;
    }

}