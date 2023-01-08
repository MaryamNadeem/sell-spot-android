package com.sellspot.app.Handlers;
import com.sellspot.app.Models.Review;
import java.util.List;

public interface SSReviewsHandler {
    void completionHandler(Boolean success, List<Review> reviews, String error);
}
