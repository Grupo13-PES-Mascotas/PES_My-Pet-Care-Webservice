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

    public static FirebaseFactory getInstance() {
        if (instance == null) {
            instance = new FirebaseFactory();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    private FirebaseFactory() {
        InputStream serviceAccount = getServiceAccount();
        FirebaseOptions options = getFirebaseOptions(serviceAccount);
        firebaseApp = FirebaseApp.initializeApp(options);
    }

    private InputStream getServiceAccount() {
        InputStream serviceAccount = null;
        try {
            serviceAccount = Files
                .newInputStream(Paths.get("./my-pet-care-85883-firebase-adminsdk-voovm-76b1b008f0.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serviceAccount;
    }

    private FirebaseOptions getFirebaseOptions(InputStream serviceAccount) {
        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://my-pet-care-85883.firebaseio.com")
                .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeInputStream(serviceAccount);
        return options;
    }

    private void closeInputStream(InputStream serviceAccount) {
        try {
            serviceAccount.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
