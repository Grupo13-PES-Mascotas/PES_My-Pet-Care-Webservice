package org.pesmypetcare.webservice.firebaseservice;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FirebaseFactory {
    private static FirebaseFactory instance;
    private GoogleCredentials googleCredentials;
    private Firestore firestore;
    private FirebaseAuth firebaseAuth;

    private FirebaseFactory() {
        setGoogleCredentials();
        FirebaseOptions options = getFirebaseOptions(googleCredentials);
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
        initializeFirebaseAuth(firebaseApp);
        initializeFirestore();
    }

    /**
     * Gets the instance of the factory.
     * @return The instance of the factory
     */
    public static FirebaseFactory getInstance() {
        if (instance == null) {
            instance = new FirebaseFactory();
        }
        return instance;
    }

    /**
     * Gets the instance to manages user authentication.
     * @return The instance that handles user authentication
     */
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    /**
     * Gets the instance that manages the database.
     * @return The instance that handles the database
     */
    public Firestore getFirestore() {
        return firestore;
    }

    /**
     * Sets the google credentials for the Firebase service.
     */
    private void setGoogleCredentials() {
        try {
            InputStream serviceAccount = Files
                .newInputStream(Paths.get("./my-pet-care-85883-firebase-adminsdk-voovm-0b4dfbf318.json"));
            googleCredentials = GoogleCredentials.fromStream(serviceAccount);
            serviceAccount.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Firebase options for the service.
     * @param googleCredentials The Google credentials of the service
     * @return The Firebase options for the service
     */
    private FirebaseOptions getFirebaseOptions(GoogleCredentials googleCredentials) {
        return new FirebaseOptions.Builder()
            .setCredentials(googleCredentials)
            .setDatabaseUrl("https://my-pet-care-85883.firebaseio.com")
            .build();
    }

    /**
     * Creates the Firestore instance
     */
    private void initializeFirestore() {
        firestore = FirestoreOptions.newBuilder()
            .setCredentials(googleCredentials)
            .build().getService();
    }

    /**
     * Creates the FirebaseAuth instance
     * @param firebaseApp The FirebaseApp used to initialize the FirebaseAuth instance
     */
    private void initializeFirebaseAuth(FirebaseApp firebaseApp) {
        firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
    }
}