package com.example.book.manager;

import android.content.Context;

public abstract class Manager {

    //region Attributes
    private boolean isInitialized;
    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }
    public boolean isInitialized() {
        return isInitialized;
    }
    //region Attributes

    public abstract void Initialize();

}
