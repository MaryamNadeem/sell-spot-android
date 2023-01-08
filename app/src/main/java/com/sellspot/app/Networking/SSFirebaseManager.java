package com.sellspot.app.Networking;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

public class SSFirebaseManager {
    public static FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    SSFirebaseManager() {
        firestoreDatabase.setLoggingEnabled(true);
    }
}