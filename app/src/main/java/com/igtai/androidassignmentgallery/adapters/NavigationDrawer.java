package com.igtai.androidassignmentgallery.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

public class NavigationDrawer extends DrawerLayout {
    public NavigationDrawer(@NonNull Context context) {
        super(context);
    }

    public NavigationDrawer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        View drawerView = getChildAt(1); // Get the drawer view
        if (isDrawerOpen(drawerView)) {
            float touchX = ev.getX();
            int drawerWidth = drawerView.getWidth();

            // If the touch is outside the drawer, close it
            if (touchX > drawerWidth) {
                closeDrawers();
                return true; // Consume the event to close the drawer
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
