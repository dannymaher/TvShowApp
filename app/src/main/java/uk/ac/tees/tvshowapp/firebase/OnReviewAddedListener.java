package uk.ac.tees.tvshowapp.firebase;

/**
 * Listener to be notified when reviews are added
 */
public interface OnReviewAddedListener {


    /**
     * Called when all reviews have been loaded
     */
    void reviewsLoaded();
}
