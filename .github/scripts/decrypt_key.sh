#!/bin/sh

# Decrypt the file
# --batch to prevent interactive command --yes to assume "yes" for questions
echo "Script" > chivato.txt
gpg --quiet --batch --yes --decrypt --passphrase="$FIREBASE_PASSPHRASE" --output my-pet-care-85883-firebase-adminsdk-voovm-76b1b008f0.json my-pet-care-85883-firebase-adminsdk-voovm-76b1b008f0.json.gpg
