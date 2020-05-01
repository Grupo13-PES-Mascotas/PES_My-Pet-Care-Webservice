package org.pesmypetcare.webservice.firebaseservice.firestore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Santiago Del Rey
 */
class PathTest {
    private final String groupId = "bYJzvJ0LTCUYgJIJEmx3";
    private final String forumId = "6wOqBt6p0tzOeySyUqAJ";
    private final String userId = "iw2VHtSHeoZohD3dAWRafXnb5x42";
    private final String groupName = "Dogs";
    private final String petName = "Rex";
    private final String messageId = "Gz72qaTq0Bv7oW5Fl9eX";
    private final String date = "2020-04-09T20:34:00";

    @Test
    public void buildGroupPath() {
        String path = Path.of(Collections.groups, groupId);
        assertEquals("groups/" + groupId, path, "Should return the path to the group.");
    }

    @Test
    public void buildGroupsCollectionPath() {
        String path = Path.of(Collections.groups);
        assertEquals("groups", path, "Should return the path to the groups collection.");
    }

    @Test
    public void buildGroupNamePath() {
        String path = Path.of(Collections.groupsNames, groupName);
        assertEquals("groups_names/" + groupName, path, "Should return the path to the group name.");
    }

    @Test
    public void buildGroupNamesCollectionPath() {
        String path = Path.of(Collections.groupsNames);
        assertEquals("groups_names", path, "Should return the path to the group names collection.");
    }

    @Test
    public void buildTagPath() {
        String tag = "huskies";
        String path = Path.of(Collections.tags, tag);
        assertEquals("tags/" + tag, path, "Should return the path to the tag.");
    }

    @Test
    public void buildTagsCollectionPath() {
        String tag = "huskies";
        String path = Path.of(Collections.tags);
        assertEquals("tags", path, "Should return the path to the tags collection.");
    }

    @Test
    public void buildUsernamePath() {
        String username = "John";
        String path = Path.of(Collections.usernames, username);
        assertEquals("used_usernames/" + username, path, "Should return the path to the username.");
    }

    @Test
    public void buildUsernamesCollectionPath() {
        String username = "John";
        String path = Path.of(Collections.usernames);
        assertEquals("used_usernames", path, "Should return the path to the usernames collection.");
    }

    @Test
    public void buildUserPath() {
        String path = Path.of(Collections.users, userId);
        assertEquals("users/" + userId, path, "Should return the path to the user.");
    }

    @Test
    public void buildUsersCollectionPath() {
        String path = Path.of(Collections.users);
        assertEquals("users", path, "Should return the path to the users collection.");
    }

    @Test
    public void buildForumPath() {
        String path = Path.of(Collections.forums, groupId, forumId);
        assertEquals("groups/" + groupId + "/forums/" + forumId, path, "Should return the path to the forum.");
    }

    @Test
    public void buildForumNamePath() {
        String forumName = "Huskies";
        String path = Path.of(Collections.forumsNames, groupName, forumName);
        assertEquals("groups_names/" + groupName + "/forums/" + forumName, path,
            "Should return the path to the forum name.");
    }

    @Test
    public void buildPetPath() {
        String path = Path.of(Collections.pets, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName, path, "Should return the path to the pet.");
    }

    @Test
    public void buildMemberPath() {
        String path = Path.of(Collections.members, groupId, userId);
        assertEquals("groups/" + groupId + "/members/" + userId, path, "Should return the path to the member.");
    }

    @Test
    public void buildMessagePath() {
        String path = Path.of(Collections.messages, groupId, forumId, messageId);
        assertEquals("groups/" + groupId + "/forums/" + forumId + "/messages/" + messageId, path,
            "Should return the path to the message.");
    }

    @Test
    public void buildKcalEntryPath() {
        String path = Path.of(Collections.kcals, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/kcals/" + date, path,
            "Should return the path to the kcal entry.");
    }

    @Test
    public void buildMealEntryPath() {
        String path = Path.of(Collections.meals, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/meals/" + date, path,
            "Should return the path to the meal entry.");
    }

    @Test
    public void buildWeightEntryPath() {
        String path = Path.of(Collections.weights, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/weights/" + date, path,
            "Should return the path to the weight entry.");
    }

    @Test
    public void buildFrequencyOfWashesEntryPath() {
        String path = Path.of(Collections.freqWashes, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/freqWashes/" + date, path,
            "Should return the path to the frequency of washes entry.");
    }

    @Test
    public void buildMedicationEntryPath() {
        String medicationName = "painkillers";
        String path = Path.of(Collections.medications, userId, petName, date, medicationName);
        assertEquals("users/" + userId + "/pets/" + petName + "/medications/" + date + "½" + medicationName, path,
            "Should return the path to the medication entry.");
    }

    @Test
    public void buildFrequencyOfTrainingEntryPath() {
        String path = Path.of(Collections.freqTrainings, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/freqTrainings/" + date, path,
            "Should return the path to the frequency of training entry.");
    }

    @Test
    public void buildAverageKcalEntryPath() {
        String path = Path.of(Collections.kcalsAverages, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/kcalsAverages/" + date, path,
            "Should return the path to the average kcal entry.");
    }

    @Test
    public void buildWeekTrainingEntryPath() {
        String path = Path.of(Collections.weekTrainings, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/weekTrainings/" + date, path,
            "Should return the path to the week training entry.");
    }

    @Test
    public void shouldFailWhenNumArgsDoesNotMatchTheRequiredForTheRequestedDocument() {
        assertThrows(IllegalArgumentException.class, () -> Path.of(Collections.forums), "Should fail"
            + " when the number of arguments passed is not the same as the required for the requested document.");
    }

    @Test
    public void shouldFailIfTheCollectionIsNull() {
        assertThrows(NullPointerException.class, () -> Path.of(null, groupId),
            "Should fail when the collection passed is null.");
    }

    @Test
    public void shouldFailIfAnyOfTheIdsIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> Path.of(Collections.messages, groupId, null, messageId),
            "Should fail when the any of the ids passed is null.");
    }
}
