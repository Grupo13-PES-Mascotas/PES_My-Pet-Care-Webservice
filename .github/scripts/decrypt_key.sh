#!/bin/sh

# Decrypt the file
# --batch to prevent interactive command --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$FIREBASE_PASSPHRASE" --output my-pet-care-85883-firebase-adminsdk-voovm-0b4dfbf318.json my-pet-care-85883-firebase-adminsdk-voovm-0b4dfbf318.json.gpg
gpg --quiet --batch --yes --decrypt --passphrase="$PROPERTIES_PASSPHRASE" --output src/main/resources/application.properties src/main/resources/application.properties.gpg
