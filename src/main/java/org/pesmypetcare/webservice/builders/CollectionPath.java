package org.pesmypetcare.webservice.builders;

import org.springframework.lang.NonNull;

/**
 * @author Santiago Del Rey
 */
class CollectionPath extends PathBuilder {
    /**
     * Builds a path to a root collection.
     *
     * @param collection The collection type
     * @return The path to the collection
     */
    @NonNull
    public String buildRootCollectionPath(@NonNull Collections collection) {
        switch (collection) {
            case groups:
                return "groups";
            case groups_names:
                return "groups_names";
            case tags:
                return "tags";
            case used_usernames:
                return "used_usernames";
            case users:
                return "users";
            case medals:
                return "medals";
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Builds a path to a collection stored one level down the hierarchy.
     *
     * @param collection The collection type
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to the collection
     */
    @NonNull
    public StringBuilder buildTwoLevelPath(Collections collection, String[] ids) {
        throwExceptionIfWrongNumArgs(1, ids.length);
        switch (collection) {
            case forums:
            case members:
                return buildPathToGroupInnerCollection(ids[0], collection);
            case forum_names:
                return buildPathToGroupsNamesInnerCollection(ids[0], collection);
            case pets:
            case userMedals:
                return buildPathToUserInnerCollections(ids[0], collection);
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Builds a path to a collection stored two levels down in the hierarchy.
     *
     * @param collection The collection type
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to the collection
     */
    @NonNull
    public StringBuilder buildThreeLevelPath(Collections collection, String[] ids) {
        throwExceptionIfWrongNumArgs(2, ids.length);
        switch (collection) {
            case messages:
                return buildPathToForumInnerCollection(ids[0], ids[1], Collections.forums, collection);
            case kcals:
            case meals:
            case weights:
            case exercises:
            case washes:
            case vaccinations:
            case illnesses:
            case medications:
            case vet_visits:
            case trainings:
            case kcalsAverages:
            case weekTrainings:
                return buildPathToPetInnerCollection(ids[0], ids[1], collection);
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Builds a path to a inner collection of a group.
     *
     * @param groupId The group ID
     * @param collection The inner collection to reach
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToGroupInnerCollection(@NonNull String groupId,
                                                                 @NonNull Collections collection) {
        return new StringBuilder("groups/").append(groupId).append('/').append(collection.name());
    }

    /**
     * Builds a path to a inner collection of a forum.
     *
     * @param groupId The group ID
     * @param forumId The forum ID
     * @param collection The inner collection to reach
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToForumInnerCollection(@NonNull String groupId, @NonNull String forumId,
                                                                 @NonNull Collections parentCollection,
                                                                 @NonNull Collections collection) {
        return buildPathToGroupInnerCollection(groupId, parentCollection).append('/').append(forumId).append('/')
            .append(collection.name());
    }

    /**
     * Builds a path to a collection of forum names.
     *
     * @param groupName The group name
     * @param collection The inner collection to reach
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToGroupsNamesInnerCollection(@NonNull String groupName,
                                                                       Collections collection) {
        return new StringBuilder("groups_names/").append(groupName).append('/').append(collection.name());
    }

    /**
     * Builds the path to a inner collection of a user.
     *
     * @param userId The user ID
     * @param collection The collection to reach
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToUserInnerCollections(@NonNull String userId,
                                                                 @NonNull Collections collection) {
        return new StringBuilder("users/").append(userId).append('/').append(collection.name());
    }

    /**
     * Builds the path to a pet.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    private static StringBuilder buildPathToPet(@NonNull String userId, @NonNull String petName) {
        return buildPathToUserInnerCollections(userId, Collections.pets).append('/').append(petName);
    }

    /**
     * Builds the path to a inner collection of a pet.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param collection The inner collection to reach
     * @return The path
     */
    private static StringBuilder buildPathToPetInnerCollection(@NonNull String userId, @NonNull String petName,
                                                               @NonNull Collections collection) {
        return buildPathToPet(userId, petName).append('/').append(collection.name());
    }


}
