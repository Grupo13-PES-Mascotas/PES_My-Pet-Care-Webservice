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
            case groupsNames:
                return "groups_names";
            case tags:
                return "tags";
            case usernames:
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
                return buildPathToForumsCollection(ids[0]);
            case forumsNames:
                return buildPathToForumNamesCollection(ids[0]);
            case pets:
                return buildPathToPetsCollection(ids[0]);
            case members:
                return buildPathToMembersCollection(ids[0]);
            case userMedals:
                return buildPathToUserMedalsCollection(ids[0]);
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
        if (collection.equals(Collections.medications)) {
            throwExceptionIfWrongNumArgs(2, ids.length);
        }
        switch (collection) {
            case messages:
                return buildPathToMessagesCollection(ids[0], ids[1]);
            case kcals:
                return buildPathToKcalsCollection(ids[0], ids[1]);
            case meals:
                return buildPathToMealsCollection(ids[0], ids[1]);
            case weights:
                return buildPathToWeightsCollection(ids[0], ids[1]);
            case exercises:
                return buildPathToExercisesCollection(ids[0], ids[1]);
            case washes:
                return buildPathToFreqWashesCollection(ids[0], ids[1]);
            case vaccinations:
                return buildPathToVaccinationsCollection(ids[0], ids[1]);
            case illnesses:
                return buildPathToIllnessesCollection(ids[0], ids[1]);
            case medications:
                return buildPathToMedicationsCollection(ids[0], ids[1]);
            case vet_visits:
                return buildPathToVetVisitsCollection(ids[0], ids[1]);
            case trainings:
                return buildPathToFreqTrainingsCollection(ids[0], ids[1]);
            case kcalsAverages:
                return buildPathToKcalsAveragesCollection(ids[0], ids[1]);
            case weekTrainings:
                return buildPathToWeekTrainingsCollection(ids[0], ids[1]);
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    /**
     * Builds a path to a collection of forums.
     *
     * @param groupId The group ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToForumsCollection(@NonNull String groupId) {
        return new StringBuilder("groups/").append(groupId).append("/forums");
    }

    /**
     * Builds a path to a collection of messages.
     *
     * @param groupId The group ID
     * @param forumId The forum ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToMessagesCollection(@NonNull String groupId, @NonNull String forumId) {
        return buildPathToForumsCollection(groupId).append('/').append(forumId).append("/messages");
    }

    /**
     * Builds a path to a collection of members.
     *
     * @param groupId The group ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToMembersCollection(@NonNull String groupId) {
        return new StringBuilder("groups/").append(groupId).append("/members");
    }

    /**
     * Builds a path to a collection of forum names.
     *
     * @param groupName The group name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToForumNamesCollection(@NonNull String groupName) {
        return new StringBuilder("groups_names/").append(groupName).append("/forums");
    }

    /**
     * Builds the path to a collection of pets.
     *
     * @param userId The user ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToPetsCollection(@NonNull String userId) {
        return new StringBuilder("users/").append(userId).append("/pets");
    }

    /**
     * Builds the path to a collection of user's medals.
     * @param userId The user ID
     * @return The path
     */
    private StringBuilder buildPathToUserMedalsCollection(@NonNull String userId) {
        return new StringBuilder("users/").append(userId).append("/medals");
    }

    /**
     * Builds the path to a collection of week training entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToWeekTrainingsCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/weekTrainings");
    }

    /**
     * Builds the path to a collection of average kcals entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToKcalsAveragesCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/kcalsAverages");
    }

    /**
     * Builds the path to a collection of frequency of training entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToFreqTrainingsCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/trainings");
    }

    /**
     * Builds the path to a collection of medication entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToMedicationsCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/medications");
    }

    /**
     * Builds the path to a collection of frequency of wash entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToFreqWashesCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/washes");
    }

    /**
     * Builds the path to a collection of weight entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToWeightsCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/weights");
    }

    /**
     * Builds the path to a collection of meal entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToMealsCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/meals");
    }

    /**
     * Builds the path to a collection of exercise entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToExercisesCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/exercises");
    }

    /**
     * Builds the path to a collection of vet visit entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToVetVisitsCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/vet_visits");
    }

    /**
     * Builds the path to a collection of illness entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToIllnessesCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/illnesses");
    }

    /**
     * Builds the path to a collection of vaccination entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToVaccinationsCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/vaccinations");
    }

    /**
     * Builds the path to a collection of kcal entries.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToKcalsCollection(@NonNull String userId, @NonNull String petName) {
        return buildPathToPet(userId, petName).append("/kcals");
    }

    /**
     * Builds the path to a pet.
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    private static StringBuilder buildPathToPet(@NonNull String userId, @NonNull String petName) {
        return buildPathToPetsCollection(userId).append('/').append(petName);
    }




}
