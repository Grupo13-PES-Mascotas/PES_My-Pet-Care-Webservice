package org.pesmypetcare.webservice.securingservice;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FirebaseFactory {
    private static FirebaseFactory instance;
    private FirebaseApp firebaseApp;

    public FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    public static FirebaseFactory getInstance() {
        if (instance == null) {
            instance = new FirebaseFactory();
        }
        return instance;
    }

    private FirebaseFactory() {
        GoogleCredentials googleCredentials = getServiceAccount();
        FirebaseOptions options = getFirebaseOptions(googleCredentials);
        firebaseApp = FirebaseApp.initializeApp(options);
    }

    private GoogleCredentials getServiceAccount() {
        GoogleCredentials googleCredentials = null;
        try {
            InputStream serviceAccount = Files
                .newInputStream(Paths.get("./my-pet-care-85883-firebase-adminsdk-voovm-76b1b008f0.json"));
            googleCredentials = GoogleCredentials.fromStream(serviceAccount);
            serviceAccount.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleCredentials;
    }

    private FirebaseOptions getFirebaseOptions(GoogleCredentials googleCredentials) {
        return new FirebaseOptions.Builder()
            .setCredentials(googleCredentials)
            .setDatabaseUrl("https://my-pet-care-85883.firebaseio.com")
            .build();
    }
}
