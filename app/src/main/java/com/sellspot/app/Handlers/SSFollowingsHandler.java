package com.sellspot.app.Handlers;
import com.sellspot.app.Models.FollowerAndFollowing;
import java.util.List;

public interface SSFollowingsHandler {
    void completionHandler(Boolean success, List<FollowerAndFollowing> followings, String error);
}
