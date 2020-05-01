package org.pesmypetcare.webservice.firebaseservice.firestore;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.springframework.lang.NonNull;

/**
 * @author Santiago Del Rey
 */
public class DocumentPath {
    private static final int[] NUMBERS = {3, 4};
    private DocumentPath() {
        throw new UnsupportedOperationException();
    }
    /**
     * Builds the path to the desired document.
     * <p>
     * There must be as many IDs as documents should be crossed until the desired document. Also, the IDs must be in
     * the same order they need to be accessed to reach the final document.
     * <p>
     * Here are some examples of how the method should be used:
     * <blockquote><pre>
     *     String path = DocumentPath.of(Collections.groupsNames, "Dogs");
     *     String path = DocumentPath.of(Collections.forumsNames, "Dogs", "Huskies");
     *     String path = DocumentPath.of(Collections.pets, "yIoqQ6OepwRaadAOz2EdeIhikOX2", "Rex");
     * </pre></blockquote>
     * @param collection The collection where the document is stored
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to a document
     * @throws WrongNumberArgsException When the number of ids does not match the required to build the path requested
     */
    @NonNull
    public static String of(@NonNull Collections collection, @NonNull String... ids)
        throws WrongNumberArgsException {
        checkProvidedIds(ids);
        switch (collection) {
            case groups:
            case groupsNames:
            case tags:
            case usernames:
            case users:
                return buildOneLevelPath(collection, ids).toString();
            case forums:
            case forumsNames:
            case pets:
            case members:
                return buildTwoLevelPath(collection, ids).toString();
            case messages:
            case kcals:
            case meals:
            case weights:
            case freqWashes:
            case medications:
            case freqTrainings:
            case kcalsAverages:
            case weekTrainings:
                return buildThreeLevelPath(collection, ids).toString();
            default:
                throw new EnumConstantNotPresentException(Collections.class, collection.name());
        }
    }

    private static void checkProvidedIds(@NonNull String[] ids) {
        if (ids.length <= 0) {
            throw new IllegalArgumentException("Invalid document path. Provided path must not be empty.");
        }
        for (int i = 0; i < ids.length; ++i) {
            if (ids[i] == null || ids[i].isEmpty()) {
                throw new IllegalArgumentException("Invalid ID at argument " + (i + 1) + ". IDs must not "
                    + "be null or empty.");
            }
        }
    }

    /**
     * Builds a path to a document stored in a root collection.
     *
     * @param collection The collection type where the document is stored
     * @param ids The ids the builder should use, going from the most outer one to the inner one
     * @return The path to a document
     * @throws WrongNumberArgsException When the number of ids does not match the required to build the path requested
     */
    @NonNull
    private static StringBuilder buildOneLevelPath(@NonNull Collections collection, @NonNull String[] ids)
        throws WrongNumberArgsException {
        throwExceptionIfWrongNumArgs(1, ids.length);
        switch (collection) {
            case groups:
                return buildPathToGroup(ids[0]);
            case groupsNames:
                return buildPathToGroupName(ids[0]);
            case tags:
                return buildPathToTag(ids[0]);
            case usernames:
                return buildPathToUsername(ids[0]);
            case users:
                return buildPathToUser(ids[0]);
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
     * @throws WrongNumberArgsException When the number of ids does not match the required to build the path requested
     */
    @NonNull
    private static StringBuilder buildTwoLevelPath(@NonNull Collections collection, @NonNull String[] ids)
        throws WrongNumberArgsException {
        throwExceptionIfWrongNumArgs(2, ids.length);
        switch (collection) {
            case forums:
                return buildPathToForum(ids[0], ids[1]);
            case forumsNames:
                return buildPathToForumName(ids[0], ids[1]);
            case pets:
                return buildPathToPet(ids[0], ids[1]);
            case members:
                return buildPathToMember(ids[0], ids[1]);
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
     * @throws WrongNumberArgsException When the number of ids does not match the required to build the path requested
     */
    @NonNull
    private static StringBuilder buildThreeLevelPath(@NonNull Collections collection, @NonNull String[] ids)
        throws WrongNumberArgsException {
        if (collection.equals(Collections.medications)) {
            throwExceptionIfWrongNumArgs(NUMBERS[1], ids.length);
        } else {
            throwExceptionIfWrongNumArgs(NUMBERS[0], ids.length);
        }
        switch (collection) {
            case messages:
                return buildPathToMessage(ids[0], ids[1], ids[2]);
            case kcals:
                return buildPathToKcal(ids[0], ids[1], ids[2]);
            case meals:
                return buildPathToMeal(ids[0], ids[1], ids[2]);
            case weights:
                return buildPathToWeight(ids[0], ids[1], ids[2]);
            case freqWashes:
                return buildPathToFreqWash(ids[0], ids[1], ids[2]);
            case medications:
                return buildPathToMedication(ids[0], ids[1], ids[2], ids[NUMBERS[0]]);
            case freqTrainings:
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
     * Builds a path to a forum.
     *
     * @param groupId The group ID
     * @param forumId The forum ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToForum(@NonNull String groupId, @NonNull String forumId) {
        return buildPathToGroup(groupId).append("/forums/").append(forumId);
    }

    /**
     * Builds a path to a message.
     *
     * @param groupId The group ID
     * @param forumId The forum ID
     * @param messageId The message ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToMessage(@NonNull String groupId, @NonNull String forumId,
                                                    @NonNull String messageId) {
        return buildPathToForum(groupId, forumId).append("/messages/").append(messageId);
    }

    /**
     * Builds a path to a forum.
     *
     * @param groupId The group ID
     * @param memberId The member ID
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToMember(@NonNull String groupId, @NonNull String memberId) {
        return buildPathToGroup(groupId).append("/members/").append(memberId);
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
     * Builds a path to a group name.
     *
     * @param groupName The group name
     * @param forumName The forum name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToForumName(@NonNull String groupName, @NonNull String forumName) {
        return buildPathToGroupName(groupName).append("/forums/").append(forumName);
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
     * Builds the path to a pet.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToPet(@NonNull String userId, @NonNull String petName) {
        return buildPathToUser(userId).append("/pets/").append(petName);
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
        return buildPathToPet(userId, petName).append("/weekTrainings/").append(date);
    }

    /**
     * Builds the path to a average kcals entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param date The date of creation
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToKcalsAverage(@NonNull String userId, @NonNull String petName,
                                                         @NonNull String date) {
        return buildPathToPet(userId, petName).append("/kcalsAverages/").append(date);
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
        return buildPathToPet(userId, petName).append("/freqTrainings/").append(date);
    }

    /**
     * Builds the path to a medication entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param date The date of creation
     * @param medicationName The medication name
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToMedication(@NonNull String userId, @NonNull String petName,
                                                       @NonNull String date, @NonNull String medicationName) {
        return buildPathToPet(userId, petName).append("/medications/").append(date).append("½").append(medicationName);
    }

    /**
     * Builds the path to a frequency of wash entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param date The date of creation
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToFreqWash(@NonNull String userId, @NonNull String petName,
                                                     @NonNull String date) {
        return buildPathToPet(userId, petName).append("/freqWashes/").append(date);
    }

    /**
     * Builds the path to a weight entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param date The date of creation
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToWeight(@NonNull String userId, @NonNull String petName,
                                                   @NonNull String date) {
        return buildPathToPet(userId, petName).append("/weights/").append(date);
    }

    /**
     * Builds the path to a meal entry.
     *
     * @param userId The user ID
     * @param petName The pet name
     * @param date The date of creation
     * @return The path
     */
    @NonNull
    private static StringBuilder buildPathToMeal(@NonNull String userId, @NonNull String petName,
                                                 @NonNull String date) {
        return buildPathToPet(userId, petName).append("/meals/").append(date);
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
        return buildPathToPet(userId, petName).append("/kcals/").append(date);
    }

    /**
     * Throws a WrongNumberArgsException if the number of arguments does not match the expected.
     *
     * @param numExpected The number of argument expected
     * @param numArgs The number of arguments received
     * @throws WrongNumberArgsException When the number of arguments does not match the expected
     */
    private static void throwExceptionIfWrongNumArgs(int numExpected, int numArgs) throws WrongNumberArgsException {
        if (numExpected != numArgs) {
            throw new WrongNumberArgsException("Expected " + numExpected + " arguments instead of " + numArgs);
        }
    }
}
