#!/bin/sh

# Decrypt the file
# --batch to prevent interactive command --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$FIREBASE_DEVELOP_PASSPHRASE" --output my-pet-care-production-firebase-adminsdk-c1es4-6387c47d60.json my-pet-care-production-firebase-adminsdk-c1es4-6387c47d60.json.gpg
gpg --quiet --batch --yes --decrypt --passphrase="$PROPERTIES_PASSPHRASE" --output src/main/resources/application.properties src/main/resources/application.properties.gpg
