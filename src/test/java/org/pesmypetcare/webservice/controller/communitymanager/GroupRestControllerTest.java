package org.pesmypetcare.webservice.controller.communitymanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.communitymanager.Group;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.service.communitymanager.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Santiago Del Rey
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class GroupRestControllerTest {
    private static final String BASE_URL = "/community/";
    private static final String GROUP_FIELD = "group";
    private String creator;
    private String json;
    private String groupName;
    private Group group;
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService service;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        groupName = "Dogs";
        creator = "John Doe";
        mapper = new ObjectMapper();
        group = new Group();
        group.setName(groupName);
        group.setCreator(creator);
        group.setCreationDate("2020-05-02T13:07:25");
        json = mapper.writeValueAsString(group);
    }

    @Test
    public void createGroup() throws Exception {
        willDoNothing().given(service).createGroup(any(GroupEntity.class));
        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(
            status().isCreated());
    }

    @Test
    public void createGroupShouldReturnBadRequestIfGroupAlreadyExists() throws Exception {
        willThrow(DocumentException.class).given(service).createGroup(any(GroupEntity.class));
        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(
            status().isBadRequest());
    }

    @Test
    public void deleteGroup() throws Exception {
        willDoNothing().given(service).deleteGroup(anyString());
        mockMvc.perform(delete(BASE_URL).param(GROUP_FIELD, groupName)).andExpect(status().isNoContent());
    }

    @Test
    public void getGroup() throws Exception {
        given(service.getGroup(anyString())).willReturn(group);
        MvcResult mvcResult = mockMvc.perform(get(BASE_URL).param(GROUP_FIELD, groupName)).andExpect(status().isOk())
            .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertEquals("Should return the requested group.", json, result);
    }

    @Test
    public void getAllGroups() throws Exception {
        List<Group> forums = new ArrayList<>();
        forums.add(group);
        given(service.getAllGroups()).willReturn(forums);
        MvcResult mvcResult = mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertEquals("Should return all the groups.", mapper.writeValueAsString(forums), result);
    }

    @Test
    public void getGroupShouldReturnNotFoundWhenTheGroupDoesNotExist() throws Exception {
        willThrow(new DocumentException("document-not-exists", "")).given(service).getGroup(anyString());
        mockMvc.perform(get(BASE_URL).param(GROUP_FIELD, groupName)).andExpect(status().isNotFound());
    }

    @Test
    public void updateField() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("name", "German Shepherds");
        String request = mapper.writeValueAsString(data);
        willDoNothing().given(service).updateField(anyString(), anyString(), anyString());
        mockMvc.perform(put(BASE_URL).param(GROUP_FIELD, groupName).param("field", "name")
            .contentType(MediaType.APPLICATION_JSON).content(request)).andExpect(status().isNoContent());
    }

    @Test
    public void getAllTags() throws Exception {
        Map<String, TagEntity> tags = new HashMap<>();
        TagEntity tag = new TagEntity();
        tags.put("dogs", tag);
        given(service.getAllTags()).willReturn(tags);
        MvcResult mvcResult = mockMvc.perform(get(BASE_URL + "tags")).andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertEquals("Should return the all the existing tags.", mapper.writeValueAsString(tags), result);
    }

    @Test
    public void updateTags() throws Exception {
        Map<String, List<String>> updateValues = new HashMap<>();
        willDoNothing().given(service).updateTags(anyString(), anyList(), anyList());
        mockMvc.perform(put(BASE_URL + "tags").param(GROUP_FIELD, groupName).contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updateValues))).andExpect(status().isNoContent());
    }

    @Test
    public void subscribe() throws Exception {
        willDoNothing().given(service).subscribe(anyString(), anyString(), anyString());
        mockMvc.perform(post(BASE_URL + "subscribe").header("token", "my-token").param(GROUP_FIELD, groupName)
            .param("username", creator)).andExpect(status().isCreated());
    }

    @Test
    public void unsubscribe() throws Exception {
        willDoNothing().given(service).unsubscribe(anyString(), anyString(), anyString());
        mockMvc.perform(delete(BASE_URL + "unsubscribe").header("token", "my-token").param(GROUP_FIELD, groupName)
            .param("username", creator)).andExpect(status().isNoContent());
    }

    @Test
    public void existsGroup() throws Exception {
        given(service.groupNameInUse(anyString())).willReturn(true);
        MvcResult mvcResult = mockMvc.perform(get(BASE_URL + "groups").param(GROUP_FIELD, groupName)).andExpect(
            status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        TypeReference<HashMap<String, Boolean>> typeRef = new TypeReference<HashMap<String, Boolean>>() { };
        Map<String, Boolean> result = mapper.readValue(response, typeRef);
        assertTrue("Should return if the group exists or not.", result.get("exists"));
    }
}
