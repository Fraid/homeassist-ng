package com.payano.homeassistant;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public interface IViewUpdate {
    /**
     * At start this method will get all states at start.
     *
     * @param values
     */
    void onInitDataReady(@NonNull ArrayList<ContentValues> values);
}
