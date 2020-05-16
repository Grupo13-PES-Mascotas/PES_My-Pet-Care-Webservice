package org.pesmypetcare.webservice.builders;

import org.springframework.lang.NonNull;

/**
 * @author Santiago Del Rey
 */
class DocumentPath extends PathBuilder {
    private static final int[] NUMBERS = {3, 4};

    /**
     * Builds a path to a document stored in a root collection.
     *
     * @param collection The collection type where the document is stored
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to a document
     */
    @NonNull
    public StringBuilder buildOneLevelPath(@NonNull Collections collection, @NonNull String[] ids) {
        throwExceptionIfWrongNumArgs(1, ids.length);
        switch (collection) {
            case groups:
                return buildPathToGroup(ids[0]);
            case groups_names:
                return buildPathToGroupName(ids[0]);
            case tags:
                return buildPathToTag(ids[0]);
            case used_usernames:
                return buildPathToUsername(ids[0]);
            case users:
                return buildPathToUser(ids[0]);
            case medals:
                return buildPathToMedal(ids[0]);
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Builds a path to a document stored in a second level collection.
     *
     * @param collection The collection type where the document is stored
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to a document
     */
    @NonNull
    public StringBuilder buildTwoLevelPath(@NonNull Collections collection, @NonNull String[] ids) {
        throwExceptionIfWrongNumArgs(2, ids.length);
        switch (collection) {
            case forums:
            case members:
                return buildPathToGroupInnerDocument(ids[0], collection, ids[1]);
            case forum_names:
                return buildPathToForumName(ids[0], ids[1]);
            case pets:
            case userMedals:
                return buildPathToUserInnerDocument(ids[0], collection, ids[1]);
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Builds a path to a document stored in a third level collection.
     *
     * @param collection The collection type where the document is stored
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to a document
     */
    @NonNull
    public StringBuilder buildThreeLevelPath(@NonNull Collections collection, @NonNull String[] ids) {
        if (collection.equals(Collections.medications)) {
            throwExceptionIfWrongNumArgs(NUMBERS[1], ids.length);
        } else {
            throwExceptionIfWrongNumArgs(NUMBERS[0], ids.length);
        }
        switch (collection) {
            case messages:
                return buildPathToForumInnerDocument(ids[0], ids[1], collection, ids[2]);
            case kcals:
                return buildPathToKcal(ids[0], ids[1], ids[2]);
            case meals:
            case weights:
            case exercises:
            case washes:
            case vaccinations:
            case illnesses:
            case medications:
            case vet_visits:
                return buildPathToPetCollection(ids[0], ids[1], collection.name(), ids[2]);
            case trainings:
                return buildPathToFreqTraining(ids[0], ids[1], ids[2]);
            case kcalsAverages:
                return buildPathToKcalsAverage(ids[0], ids[1], ids[2]);
            case weekTrainings:
                return buildPathToWeekTraining(ids[0], ids[1], ids[2]);
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Builds a path to a group.
     *
     * @param groupId The group ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToGroup(@NonNull String groupId) {
        return new StringBuilder("groups/").append(groupId);
    }

    /**
     * Builds a path to a group inner document.
     *
     * @param groupId The group ID
     * @param collection The collection which the document belongs to
     * @param id The document ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToGroupInnerDocument(@NonNull String groupId, @NonNull Collections collection,
                                                               @NonNull String id) {
        return buildPathToGroup(groupId).append('/').append(collection.name()).append('/').append(id);
    }

    /**
     * Builds a path to a forum inner document.
     *
     * @param groupId The group ID
     * @param forumId The forum ID
     * @param collection The collection which the document belongs to
     * @param id The document ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToForumInnerDocument(@NonNull String groupId, @NonNull String forumId,
                                                               @NonNull Collections collection, @NonNull String id) {
        return buildPathToGroupInnerDocument(groupId, Collections.forums, forumId).append('/').append(collection.name())
            .append('/').append(id);
    }

    /**
     * Builds a path to a group name.
     *
     * @param groupName The group name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToGroupName(@NonNull String groupName) {
        return new StringBuilder("groups_names/").append(groupName);
    }

    /**
     * Builds a path to a forum name.
     *
     * @param groupName The group name
     * @param forumName The forum name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToForumName(@NonNull String groupName, @NonNull String forumName) {
        return buildPathToGroupName(groupName).append('/').append(Collections.forum_names.name()).append('/')
            .append(forumName);
    }

    /**
     * Builds the path to a tag.
     *
     * @param name The tag name
     * @return The tag
     */
    @NonNull
    private static StringBuilder buildPathToTag(@NonNull String name) {
        return new StringBuilder("tags/").append(name);
    }

    /**
     * Builds the path to a username.
     *
     * @param username The username
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToUsername(@NonNull String username) {
        return new StringBuilder("used_usernames/").append(username);
    }

    /**
     * Builds the path to a user.
     *
     * @param userId The user ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToUser(@NonNull String userId) {
        return new StringBuilder("users/").append(userId);
    }

    /**
     * Builds the path to a medal.
     *
     * @param medalName The medal name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToMedal(@NonNull String medalName) {
        return new StringBuilder("medals/").append(medalName);
    }

    /**
     * Builds the path to a user inner document.
     *
     * @param userId The user ID
     * @param collection The inner collection which the document belongs to
     * @param id The document ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToUserInnerDocument(@NonNull String userId, @NonNull Collections collection,
                                                              @NonNull String id) {
        return buildPathToUser(userId).append('/').append(collection.name()).append('/').append(id);
    }

    /**
     * Builds the path to a week training entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param date The date of creation
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToWeekTraining(@NonNull String userId, @NonNull String petName,
                                                         @NonNull String date) {
        return buildPathToUserInnerDocument(userId, Collections.pets, petName).append("/weekTrainings/").append(date);
    }

    /**
     * Builds the path to an average kcals entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param date The date of creation
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToKcalsAverage(@NonNull String userId, @NonNull String petName,
                                                         @NonNull String date) {
        return buildPathToUserInnerDocument(userId, Collections.pets, petName).append("/kcalsAverages/").append(date);
    }

    /**
     * Builds the path to a frequency of training entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param date The date of creation
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToFreqTraining(@NonNull String userId, @NonNull String petName,
                                                         @NonNull String date) {
        return buildPathToUserInnerDocument(userId, Collections.pets, petName).append("/trainings/").append(date);
    }

    /**
     * Builds the path to a pet collection entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param collectionName The pet collection name
     * @param key Key to the collection entry
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToPetCollection(@NonNull String userId, @NonNull String petName,
                                                          @NonNull String collectionName, @NonNull String key) {
        return buildPathToUserInnerDocument(userId, Collections.pets, petName).append("/").append(collectionName)
            .append("/").append(key);
    }

    /**
     * Builds the path to a kcal entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param date The date of creation
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToKcal(@NonNull String userId, @NonNull String petName,
                                                 @NonNull String date) {
        return buildPathToUserInnerDocument(userId, Collections.pets, petName).append("/kcals/").append(date);
    }
}
