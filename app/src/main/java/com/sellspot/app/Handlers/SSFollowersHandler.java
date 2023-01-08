package com.sellspot.app.Handlers;

import java.util.List;
import com.sellspot.app.Models.FollowerAndFollowing;

public interface SSFollowersHandler {
    void completionHandler(Boolean success, List<FollowerAndFollowing> followers, String error);
}
