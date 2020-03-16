package org.pesmypetcare.webservice.securingService;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FirebaseFactory {
    private static FirebaseFactory instance;
    private FirebaseApp firebaseApp;

    private FirebaseFactory() {
        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream("./my-pet-care-85883-firebase-adminsdk-voovm-76b1b008f0.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://my-pet-care-85883.firebaseio.com")
                .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        firebaseApp = FirebaseApp.initializeApp(options);
    }

    public static FirebaseFactory getInstance() {
        if (instance == null)
            instance = new FirebaseFactory();
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
