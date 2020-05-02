package org.pesmypetcare.webservice.firebaseservice.builders;

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
        String path = Path.ofDocument(Collections.groups, groupId);
        assertEquals("groups/" + groupId, path, "Should return the path to the group.");
    }

    @Test
    public void buildGroupsCollectionPath() {
        String path = Path.ofCollection(Collections.groups);
        assertEquals("groups", path, "Should return the path to the groups collection.");
    }

    @Test
    public void buildGroupNamePath() {
        String path = Path.ofDocument(Collections.groupsNames, groupName);
        assertEquals("groups_names/" + groupName, path, "Should return the path to the group name.");
    }

    @Test
    public void buildGroupNamesCollectionPath() {
        String path = Path.ofCollection(Collections.groupsNames);
        assertEquals("groups_names", path, "Should return the path to the group names collection.");
    }

    @Test
    public void buildTagPath() {
        String tag = "huskies";
        String path = Path.ofDocument(Collections.tags, tag);
        assertEquals("tags/" + tag, path, "Should return the path to the tag.");
    }

    @Test
    public void buildTagsCollectionPath() {
        String path = Path.ofCollection(Collections.tags);
        assertEquals("tags", path, "Should return the path to the tags collection.");
    }

    @Test
    public void buildUsernamePath() {
        String username = "John";
        String path = Path.ofDocument(Collections.usernames, username);
        assertEquals("used_usernames/" + username, path, "Should return the path to the username.");
    }

    @Test
    public void buildUsernamesCollectionPath() {
        String path = Path.ofCollection(Collections.usernames);
        assertEquals("used_usernames", path, "Should return the path to the usernames collection.");
    }

    @Test
    public void buildUserPath() {
        String path = Path.ofDocument(Collections.users, userId);
        assertEquals("users/" + userId, path, "Should return the path to the user.");
    }

    @Test
    public void buildUsersCollectionPath() {
        String path = Path.ofCollection(Collections.users);
        assertEquals("users", path, "Should return the path to the users collection.");
    }

    @Test
    public void buildForumPath() {
        String path = Path.ofDocument(Collections.forums, groupId, forumId);
        assertEquals("groups/" + groupId + "/forums/" + forumId, path, "Should return the path to the forum.");
    }

    @Test
    public void buildForumsCollectionPath() {
        String path = Path.ofCollection(Collections.forums, groupId);
        assertEquals("groups/" + groupId + "/forums", path, "Should return the path to a forums collection.");
    }

    @Test
    public void buildForumNamePath() {
        String forumName = "Huskies";
        String path = Path.ofDocument(Collections.forumsNames, groupName, forumName);
        assertEquals("groups_names/" + groupName + "/forums/" + forumName, path,
            "Should return the path to the forum name.");
    }

    @Test
    public void buildForumNamesCollectionPath() {
        String path = Path.ofCollection(Collections.forumsNames, groupName);
        assertEquals("groups_names/" + groupName + "/forums", path,
            "Should return the path to the collectionof forum names.");
    }

    @Test
    public void buildPetPath() {
        String path = Path.ofDocument(Collections.pets, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName, path, "Should return the path to the pet.");
    }

    @Test
    public void buildPetsCollectionPath() {
        String path = Path.ofCollection(Collections.pets, userId);
        assertEquals("users/" + userId + "/pets", path, "Should return the path to the pets collection.");
    }

    @Test
    public void buildMemberPath() {
        String path = Path.ofDocument(Collections.members, groupId, userId);
        assertEquals("groups/" + groupId + "/members/" + userId, path, "Should return the path to the member.");
    }

    @Test
    public void buildMembersCollectionPath() {
        String path = Path.ofCollection(Collections.members, groupId);
        assertEquals("groups/" + groupId + "/members", path, "Should return the path to the members collection.");
    }

    @Test
    public void buildMessagePath() {
        String path = Path.ofDocument(Collections.messages, groupId, forumId, messageId);
        assertEquals("groups/" + groupId + "/forums/" + forumId + "/messages/" + messageId, path,
            "Should return the path to the message.");
    }

    @Test
    public void buildMessagesCollectionPath() {
        String path = Path.ofCollection(Collections.messages, groupId, forumId);
        assertEquals("groups/" + groupId + "/forums/" + forumId + "/messages", path,
            "Should return the path to the messages collection.");
    }

    @Test
    public void buildKcalEntryPath() {
        String path = Path.ofDocument(Collections.kcals, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/kcals/" + date, path,
            "Should return the path to the kcal entry.");
    }

    @Test
    public void buildKcalEntriesCollectionPath() {
        String path = Path.ofCollection(Collections.kcals, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName + "/kcals", path,
            "Should return the path to the kcal entries collection.");
    }

    @Test
    public void buildMealEntryPath() {
        String path = Path.ofDocument(Collections.meals, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/meals/" + date, path,
            "Should return the path to the meal entry.");
    }

    @Test
    public void buildMealEntriesCollectionPath() {
        String path = Path.ofCollection(Collections.meals, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName + "/meals", path,
            "Should return the path to the meal entries collection.");
    }

    @Test
    public void buildWeightEntryPath() {
        String path = Path.ofDocument(Collections.weights, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/weights/" + date, path,
            "Should return the path to the weight entry.");
    }

    @Test
    public void buildWeightEntriesCollectionPath() {
        String path = Path.ofCollection(Collections.weights, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName + "/weights", path,
            "Should return the path to the weight entries collection.");
    }

    @Test
    public void buildFrequencyOfWashesEntryPath() {
        String path = Path.ofDocument(Collections.freqWashes, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/freqWashes/" + date, path,
            "Should return the path to the frequencyof washes entry.");
    }

    @Test
    public void buildFrequencyOfWashesEntriesCollectionPath() {
        String path = Path.ofCollection(Collections.freqWashes, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName + "/freqWashes", path,
            "Should return the path to the frequencyof washes entries collection.");
    }

    @Test
    public void buildMedicationEntryPath() {
        String medicationName = "painkillers";
        String path = Path.ofDocument(Collections.medications, userId, petName, date, medicationName);
        assertEquals("users/" + userId + "/pets/" + petName + "/medications/" + date + "½" + medicationName, path,
            "Should return the path to the medication entry.");
    }

    @Test
    public void buildMedicationEntriesCollectionPath() {
        String path = Path.ofCollection(Collections.medications, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName + "/medications", path,
            "Should return the path to the medication entries collection.");
    }

    @Test
    public void buildFrequencyOfTrainingEntryPath() {
        String path = Path.ofDocument(Collections.freqTrainings, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/freqTrainings/" + date, path,
            "Should return the path to the frequencyof training entry.");
    }

    @Test
    public void buildFrequencyOfTrainingEntriesCollectionPath() {
        String path = Path.ofCollection(Collections.freqTrainings, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName + "/freqTrainings", path,
            "Should return the path to the frequencyof training entries collection.");
    }

    @Test
    public void buildAverageKcalEntryPath() {
        String path = Path.ofDocument(Collections.kcalsAverages, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/kcalsAverages/" + date, path,
            "Should return the path to the average kcal entry.");
    }

    @Test
    public void buildAverageKcalEntriesCollectionPath() {
        String path = Path.ofCollection(Collections.kcalsAverages, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName + "/kcalsAverages", path,
            "Should return the path to the average kcal entries collection.");
    }

    @Test
    public void buildWeekTrainingEntryPath() {
        String path = Path.ofDocument(Collections.weekTrainings, userId, petName, date);
        assertEquals("users/" + userId + "/pets/" + petName + "/weekTrainings/" + date, path,
            "Should return the path to the week training entry.");
    }

    @Test
    public void buildWeekTrainingEntriesCollectionPath() {
        String path = Path.ofCollection(Collections.weekTrainings, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName + "/weekTrainings", path,
            "Should return the path to the week training entries collection.");
    }

    @Test
    public void shouldFailWhenNumArgsDoesNotMatchTheRequiredForTheRequestedDocument() {
        assertThrows(IllegalArgumentException.class, () -> Path.ofDocument(Collections.forums), "Should fail"
            + " when the numberof arguments passed is not the same as the required for the requested "
            + "document.");
    }

    @Test
    public void shouldFailIfTheCollectionIsNull() {
        assertThrows(NullPointerException.class, () -> Path.ofDocument(null, groupId),
            "Should fail when the collection passed is null.");
    }

    @Test
    public void shouldFailIfAnyOfTheIdsIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> Path.ofDocument(Collections.messages, groupId, null, messageId),
            "Should fail when the anyof the ids passed is null.");
    }
}
