package org.pesmypetcare.webservice.firebaseservice.firestore;

import org.springframework.lang.NonNull;

/**
 * @author Santiago Del Rey
 */
public class Path {
    private static final int[] NUMBERS = {3, 4};
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
     *     String path = Path.of(Collections.users);
     *     String path = Path.of(Collections.groupsNames, "Dogs");
     *     String path = Path.of(Collections.forumsNames, "Dogs", "Huskies");
     *     String path = Path.of(Collections.pets, "yIoqQ6OepwRaadAOz2EdeIhikOX2", "Rex");
     * </pre></blockquote>
     *
     * @param collection The collection where the document is stored
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to a document
     */
    @NonNull
    public static String of(@NonNull Collections collection, @NonNull String... ids) {
        checkProvidedIds(collection, ids);
        switch (collection) {
            case groups:
            case groupsNames:
            case tags:
            case usernames:
            case users:
                if (ids.length == 0) {
                    return COLLECTION_PATH.buildRootCollectionPath(collection);
                }
                return DOCUMENT_PATH.buildOneLevelPath(collection, ids).toString();
            case forums:
            case forumsNames:
            case pets:
            case members:
                if (ids.length == 1) {
                    return COLLECTION_PATH.buildTwoLevelPath(collection, ids).toString();
                }
                return DOCUMENT_PATH.buildTwoLevelPath(collection, ids).toString();
            case medications:
                if (ids.length < NUMBERS[1]) {
                    return COLLECTION_PATH.buildThreeLevelPath(collection, ids).toString();
                }
            case messages:
            case kcals:
            case meals:
            case weights:
            case freqWashes:
            case freqTrainings:
            case kcalsAverages:
            case weekTrainings:
                if (ids.length < NUMBERS[0]) {
                    return COLLECTION_PATH.buildThreeLevelPath(collection, ids).toString();
                }
                return DOCUMENT_PATH.buildThreeLevelPath(collection, ids).toString();
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Checks that the array is not empty and its elements are not null or empty.
     *
     * @param collection The collection where the document is stored
     * @param ids The array with the ids
     * @throws IllegalArgumentException When the array is empty or any of its elements is null or empty
     */
    private static void checkProvidedIds(Collections collection, @NonNull String[] ids) {
        if (ids.length == 0) {
            if (!(collection.equals(Collections.groups) || collection.equals(Collections.groupsNames) || collection
                .equals(Collections.tags) || collection.equals(Collections.usernames) || collection.equals(Collections.users))) {
                throw new IllegalArgumentException("Invalid document path. Provided path must not be empty.");
            }
        }
        for (int i = 0; i < ids.length; ++i) {
            if (ids[i] == null || ids[i].isEmpty()) {
                throw new IllegalArgumentException(
                    "Invalid ID at argument " + (i + 1) + ". IDs must not " + "be null or empty.");
            }
        }
    }
}
