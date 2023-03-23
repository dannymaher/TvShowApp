package uk.ac.tees.tvshowapp.firebase;

/**
 * listener to be notified when items are added to tracked tv shows or films
 *
 * @param <T>
 */
public interface OnMyLibraryUpdatedListener<T> {

    /**
     * called when items are added to tracked films or tv shows
     *
     * @param item the item added
     */
    void itemAdded(T item);

    /**
     * called when all items have finished loading
     */
    void loadComplete();

}
