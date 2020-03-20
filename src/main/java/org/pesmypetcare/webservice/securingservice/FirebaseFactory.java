package org.pesmypetcare.webservice.securingservice;

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
    private FirebaseApp firebaseApp;
    private GoogleCredentials googleCredentials;

    private FirebaseFactory() {
        setGoogleCredentials();
        FirebaseOptions options = getFirebaseOptions(googleCredentials);
        firebaseApp = FirebaseApp.initializeApp(options);
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
        return FirebaseAuth.getInstance(firebaseApp);
    }

    /**
     * Gets the instance to manages the data base.
     * @return The instance that handles the data base
     */
    public Firestore getFirestore() {
        return FirestoreOptions.newBuilder()
            .setCredentials(googleCredentials)
            .build().getService();
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
}
