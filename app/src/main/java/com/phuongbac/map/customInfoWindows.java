package com.phuongbac.map;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class customInfoWindows extends MarkerInfoWindow {
    /**
     * @param layoutResId layout that must contain these ids: bubble_title,bubble_description,
     *                    bubble_subdescription, bubble_image
     * @param mapView
     */
    public customInfoWindows(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }
}
