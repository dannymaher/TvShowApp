package uk.ac.tees.tvshowapp.firebase;

/**
 * Holds information based around the Firebase variant of a review
 */
public class UserReview {
    /**
     * User Id of the review author
     */
    private String authorId;
    /**
     * ItemId of the item being reviewed
     */
    private Long itemId;
    /**
     * Rating of the item
     */
    private Long rating;
    /**
     * Text Review
     */
    private String reviewText;
    /**
     * Id of the file where the review is stored in firebase
     */
    private String fileId;

    /**
     * Basic Constructor that sets all values to their lowest value
     */
    UserReview() {
        authorId = "";
        itemId = new Long(0);
        rating = new Long(0);
        reviewText = "";
        fileId = "";
    }

    /**
     * Gets the author ID
     *
     * @return Author ID
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * Sets the Author ID
     *
     * @param authorId Author ID
     */
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    /**
     * Gets the Item ID
     *
     * @return Item ID
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * Sets the Item ID
     *
     * @param itemId Item ID
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    /**
     * Gets the Rating
     *
     * @return Item Rating
     */
    public Long getRating() {
        return rating;
    }

    /**
     * Sets the Rating
     *
     * @param rating Item Rating
     */
    public void setRating(Long rating) {
        this.rating = rating;
    }

    /**
     * Gets the Review Text
     *
     * @return Text form of review
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Sets the Review Text
     *
     * @param reviewText Text form of review
     */
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    /**
     * Gets the Name of the file that stores this review in Firebase
     *
     * @return
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * Sets the fileId
     *
     * @param fileId ID of the firebase document
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
