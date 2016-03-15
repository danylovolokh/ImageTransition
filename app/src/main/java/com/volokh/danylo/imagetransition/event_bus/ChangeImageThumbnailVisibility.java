package com.volokh.danylo.imagetransition.event_bus;

/**
 * Created by danylo.volokh on 3/15/16.
 *
 * This message is sent from {@link com.volokh.danylo.imagetransition.activities.ImageDetailsActivity}
 * to {@link com.volokh.danylo.imagetransition.activities.ImagesListActivity}
 *
 * When image transition is about to start. This message should invoke hiding of original image
 * Which transition we are imitating.
 *
 */
public class ChangeImageThumbnailVisibility {

    private final boolean visible;

    public ChangeImageThumbnailVisibility(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
