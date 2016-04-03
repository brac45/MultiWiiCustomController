package com.apps.jhl.wifiremote.view;

/**
 * Created by owner-pc on 2016-01-07.
 */
public interface JoystickMovedListener {
    void OnMoved(int pan, int tilt);
    void OnReleased();
    void OnReturnedToCenter();
}