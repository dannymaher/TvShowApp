package uk.ac.tees.tvshowapp.firebase;

import java.util.ArrayList;

import uk.ac.tees.tvshowapp.tmdb.model.Review;

/**
 * Holds a list of reviews for a specified subject, in order to more easily transfer data
 * to the view
 */
public class ReviewList {

    /**
     * List of Reviews
     */
    private ArrayList<UserReview> reviews;
    /**
     * Average rating of all reviews in the list
     */
    private double avgRating;
    /**
     * Item ID of the subject of the Reviews
     */
    private Long reviewsSubject;

    ReviewList(ArrayList<UserReview> reviewsIn, Long id) {
        reviews = reviewsIn;
        reviewsSubject = id;
        double counter = 0;
        double reviewTotal = 0;
        for (UserReview review : reviews) {
            reviewTotal += (double) review.getRating();
            counter++;
        }
        avgRating = reviewTotal / counter;
    }

    /**
     * Gets the Average Rating for the Item
     *
     * @return Average Rating
     */
    public double getAverageRating() {
        return avgRating;
    }

    /**
     * Returns the id of the item that is subject of the reviews
     *
     * @return Reviews Subject
     */
    public int getReviewsSubject() {
        return reviewsSubject.intValue();
    }

    /**
     * Returns the list of Reviews
     *
     * @return List of reviews
     */
    public ArrayList<UserReview> getReviews() {
        return reviews;
    }

}
