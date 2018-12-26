package com.payano.homeassistant.shared;

import android.support.v4.app.FragmentActivity;

import com.payano.homeassistant.model.Entity;
import com.payano.homeassistant.model.rest.CallServiceRequest;

public interface EntityInterface {
    /**
     * @param entity
     * @return true to indicate action is consumed.
     */
    //boolean onEntitySelected(Entity entity);

    void onEntityUpperViewClick(EntityAdapter.EntityTileViewHolder viewHolder, Entity entity);

    boolean onEntityUpperViewLongClick(EntityAdapter.EntityTileViewHolder viewHolder, Entity entity);

    //void callService(final String domain, final String service, CallServiceRequest serviceRequest);
}
