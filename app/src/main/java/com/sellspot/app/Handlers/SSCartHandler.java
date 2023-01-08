package com.sellspot.app.Handlers;

import java.util.List;

public interface SSCartHandler {
    void completionHandler(Boolean success, List<String> cartperstoreid , String error);
}
