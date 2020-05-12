package org.pesmypetcare.webservice.builders;

import org.springframework.lang.NonNull;

/**
 * @author Santiago Del Rey
 */
public class Path {
    private static final DocumentPath DOCUMENT_PATH = new DocumentPath();
    private static final CollectionPath COLLECTION_PATH = new CollectionPath();

    private Path() {
        throw new UnsupportedOperationException();
    }

    /**
     * Builds the path to the desired document or collection.
     * <p>
     * There must be as many IDs as documents should be crossed until the desired file is reached. Also, the IDs must
     * be in the same order they need to be accessed to reach the final document or collection.
     * <p>
     * Here are some examples of how the method should be used:
     * <blockquote><pre>
     *     //Path to the Dogs group
     *     String path = Path.ofDocument(Collections.groupsNames, "Dogs");
     *
     *     //Path to the Huskies forum in the Dogs group
     *     String path = Path.ofDocument(Collections.forumsNames, "Dogs", "Huskies");
     *
     *     //Path to the pet Rex of the user with ID yIoqQ6OepwRaadAOz2EdeIhikOX2
     *     String path = Path.ofDocument(Collections.pets, "yIoqQ6OepwRaadAOz2EdeIhikOX2", "Rex");
     * </pre></blockquote>
     *
     * @param collection The collection where the document is stored
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to a document
     */
    @NonNull
    public static String ofDocument(@NonNull Collections collection, @NonNull String... ids) {
        checkProvidedIds(collection, ids);
        switch (collection) {
            case groups:
            case groupsNames:
            case tags:
            case usernames:
            case users:
                return DOCUMENT_PATH.buildOneLevelPath(collection, ids).toString();
            case forums:
            case forumsNames:
            case pets:
            case medals:
            case userMedals:
            case members:
                return DOCUMENT_PATH.buildTwoLevelPath(collection, ids).toString();
            case messages:
            case kcals:
            case trainings:
            case kcalsAverages:
            case weekTrainings:
            case meals:
            case weights:
            case exercises:
            case washes:
            case vaccinations:
            case illnesses:
            case medications:
            case vet_visits:
                return DOCUMENT_PATH.buildThreeLevelPath(collection, ids).toString();
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Builds the path to the desired collection.
     * <p>
     * There must be as many IDs as documents should be crossed until the desired collection is reached. Also, the IDs
     * must be in the same order they need to be accessed to reach the final document or collection.
     * <p>
     * Here are some examples of how the method should be used:
     * <blockquote><pre>
     *     //Path to the users collection
     *     String path = Path.ofCollection(Collections.users);
     *
     *     //Path to the forum names collection in the Dogs group name document
     *     String path = Path.ofCollection(Collections.forumsNames, "Dogs");
     * </pre></blockquote>
     *
     * @param collection The collection where the document is stored
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to a collection
     */
    @NonNull
    public static String ofCollection(@NonNull Collections collection, @NonNull String... ids) {
        checkProvidedIds(collection, ids);
        switch (collection) {
            case groups:
            case groupsNames:
            case tags:
            case usernames:
            case users:
                return COLLECTION_PATH.buildRootCollectionPath(collection);
            case forums:
            case forumsNames:
            case pets:
            case medals:
            case userMedals:
            case members:
                return COLLECTION_PATH.buildTwoLevelPath(collection, ids).toString();
            case messages:
            case kcals:
            case trainings:
            case kcalsAverages:
            case weekTrainings:
            case meals:
            case weights:
            case exercises:
            case washes:
            case vaccinations:
            case illnesses:
            case medications:
            case vet_visits:
                return COLLECTION_PATH.buildThreeLevelPath(collection, ids).toString();
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Returns the collection with the same name as the given.
     * @param collectionName Name of the collection
     * @return Collection with the name collectionName
     * @throws IllegalArgumentException When the array is empty or any of its elements is null or empty
     */
    public static Collections collectionOfField(String collectionName) {
        for (Collections c : Collections.values()) {
            if (c.name().equals(collectionName)) {
                return c;
            }
        }
        throw new IllegalArgumentException("CollectionName is not a valid name");
    }

    /**
     * Checks that the array is not empty and its elements are not null or empty.
     *
     * @param collection The collection where the document is stored
     * @param ids The array with the ids
     * @throws IllegalArgumentException When the array is empty or any of its elements is null or empty
     */
    private static void checkProvidedIds(Collections collection, @NonNull String[] ids) {
        if (ids.length == 0 && !isRootCollection(collection)) {
            throw new IllegalArgumentException("Invalid document path. Provided path must not be empty.");
        }
        for (int i = 0; i < ids.length; ++i) {
            if (ids[i] == null || ids[i].isEmpty()) {
                throw new IllegalArgumentException(
                    "Invalid ID at argument " + (i + 1) + ". IDs must not be null or empty.");
            }
        }
    }

    /**
     * Checks if the collection is one of the root collections.
     *
     * @param collection The collection
     * @return True if it is a root collection
     */
    private static boolean isRootCollection(Collections collection) {
        return collection.equals(Collections.groups) || collection.equals(Collections.groupsNames) || collection
            .equals(Collections.tags) || collection.equals(Collections.usernames) || collection
            .equals(Collections.users) || collection.equals(Collections.medals);
    }
}
