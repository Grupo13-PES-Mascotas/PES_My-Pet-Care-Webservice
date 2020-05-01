package org.pesmypetcare.webservice.firebaseservice.firestore;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Santiago Del Rey
 */
class DocumentPathTest {
    private final String groupId = "bYJzvJ0LTCUYgJIJEmx3";
    private final String forumId = "6wOqBt6p0tzOeySyUqAJ";
    private final String userId = "iw2VHtSHeoZohD3dAWRafXnb5x42";
    private final String groupName = "Dogs";
    private final String petName = "Rex";
    private final String messageId = "Gz72qaTq0Bv7oW5Fl9eX";
    private final String date = "2020-04-09T20:34:00";

    @Test
    public void buildGroupPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.groups, groupId);
        assertEquals("groups/" + groupId, path, "Should return the path to the group.");
    }

    @Test
    public void buildGroupNamePath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.groupsNames, groupName);
        assertEquals("groups_names/" + groupName, path, "Should return the path to the forum.");
    }

    @Test
    public void buildTagPath() throws WrongNumberArgsException {
        String tag = "huskies";
        String path = DocumentPath.of(Collections.tags, tag);
        assertEquals("tags/" + tag, path, "Should return the path to the tag.");
    }

    @Test
    public void buildUsernamePath() throws WrongNumberArgsException {
        String username = "John";
        String path = DocumentPath.of(Collections.usernames, username);
        assertEquals("used_usernames/" + username, path, "Should return the path to the username.");
    }

    @Test
    public void buildUserPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.users, userId);
        assertEquals("users/" + userId, path, "Should return the path to the user.");
    }

    @Test
    public void buildForumPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.forums, groupId, forumId);
        assertEquals("groups/" + groupId + "/forums/" + forumId, path, "Should return the path to the forum.");
    }

    @Test
    public void buildForumNamePath() throws WrongNumberArgsException {
        String forumName = "Huskies";
        String path = DocumentPath.of(Collections.forumsNames, groupName, forumName);
        assertEquals("groups_names/" + groupName + "/forums/" + forumName, path,
            "Should return the path to the forum name.");
    }

    @Test
    public void buildPetPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.pets, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName, path, "Should return the path to the pet.");
    }

    @Test
    public void buildMemberPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.members, groupId, userId);
        assertEquals("groups/" + groupId + "/members/" + userId, path, "Should return the path to the member.");
    }

    @Test
    public void buildMessagePath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.messages, groupId, forumId, messageId);
        assertEquals("groups/" + groupId + "/forums/" + forumId + "/messages/" + messageId, path,
            "Should return the path to the message.");
    }

    @Test
    public void buildKcalEntryPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.kcals, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/kcals/" + date, path,
            "Should return the path to the kcal entry.");
    }

    @Test
    public void buildMealEntryPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.meals, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/meals/" + date, path,
            "Should return the path to the meal entry.");
    }

    @Test
    public void buildWeightEntryPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.weights, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/weights/" + date, path,
            "Should return the path to the weight entry.");
    }

    @Test
    public void buildFrequencyOfWashesEntryPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.freqWashes, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/freqWashes/" + date, path,
            "Should return the path to the frequency of washes entry.");
    }

    @Test
    public void buildMedicationEntryPath() throws WrongNumberArgsException {
        String medicationName = "painkillers";
        String path = DocumentPath.of(Collections.medications, userId, petName, date, medicationName);
        assertEquals("users/" + userId + "/pets/" + petName + "/medications/" + date + "½" + medicationName, path,
            "Should return the path to the medication entry.");
    }

    @Test
    public void buildFrequencyOfTrainingEntryPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.freqTrainings, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/freqTrainings/" + date, path,
            "Should return the path to the frequency of training entry.");
    }

    @Test
    public void buildAverageKcalEntryPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.kcalsAverages, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/kcalsAverages/" + date, path,
            "Should return the path to the average kcal entry.");
    }

    @Test
    public void buildWeekTrainingEntryPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.weekTrainings, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/weekTrainings/" + date, path,
            "Should return the path to the week training entry.");
    }

    @Test
    public void shouldFailWhenNumArgsDoesNotMatchTheRequiredForTheRequestedDocument() {
        assertThrows(WrongNumberArgsException.class, () -> DocumentPath.of(Collections.forums, groupId), "Should fail"
            + " when the number of arguments passed is not the same as the required for the requested document.");
    }

    @Test
    public void shouldFailIfTheCollectionIsNull() {
        assertThrows(NullPointerException.class, () -> DocumentPath.of(null, groupId),
            "Should fail when the collection passed is null.");
    }

    @Test
    public void shouldFailIfAnyOfTheIdsIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> DocumentPath.of(Collections.messages, groupId, null, messageId),
            "Should fail when the any of the ids passed is null.");
    }
}
