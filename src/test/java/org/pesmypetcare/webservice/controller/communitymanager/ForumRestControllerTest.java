package org.pesmypetcare.webservice.controller.communitymanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.Message;
import org.pesmypetcare.webservice.entity.communitymanager.MessageEntity;
import org.pesmypetcare.webservice.service.communitymanager.ForumService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Santiago Del Rey
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ForumRestControllerTest {
    private static final String BASE_URL = "/community/";
    private static final String TOKEN_HEADER = "token";
    private String myToken;
    private String creationDate;
    private String creator;
    private String reporter;
    private String json;
    private String parentGroup;
    private ForumEntity forumEntity;
    private ObjectMapper mapper;
    private String forumName;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForumService service;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        myToken = "my-token";
        parentGroup = "Dogs";
        mapper = new ObjectMapper();
        forumEntity = new ForumEntity();
        creator = "John Doe";
        reporter = "Manolo Doe";
        forumName = "Huskies";
        creationDate = "2020-05-02T13:07:25";
        forumEntity.setCreator(creator);
        forumEntity.setName(forumName);
        forumEntity.setCreationDate(creationDate);
        json = mapper.writeValueAsString(forumEntity);
    }

    @Test
    public void createForum() throws Exception {
        willDoNothing().given(service).createForum(anyString(), anyString(), any(ForumEntity.class));
        mockMvc.perform(post(BASE_URL + parentGroup).contentType(MediaType.APPLICATION_JSON).content(json).header(TOKEN_HEADER, myToken))
            .andExpect(status().isCreated());
    }

    @Test
    public void deleteForum() throws Exception {
        willDoNothing().given(service).deleteForum(anyString(), anyString());
        mockMvc.perform(delete(BASE_URL + parentGroup).param("forum", forumName)).andExpect(status().isNoContent());
    }

    @Test
    public void getForum() throws Exception {
        given(service.getForum(anyString(), anyString())).willReturn(forumEntity);
        MvcResult mvcResult = mockMvc.perform(get(BASE_URL + parentGroup).param("forum", forumName))
            .andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertEquals("Should return the requested forum.", json, result);
    }

    @Test
    public void getAllForums() throws Exception {
        List<ForumEntity> forums = new ArrayList<>();
        forums.add(forumEntity);
        given(service.getAllForumsFromGroup(anyString())).willReturn(forums);
        MvcResult mvcResult = mockMvc.perform(get(BASE_URL + parentGroup)).andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertEquals("Should return all the forums from the requested group.", mapper.writeValueAsString(forums),
            result);
    }

    @Test
    public void updateTags() throws Exception {
        willDoNothing().given(service).updateTags(anyString(), anyString(), anyString(), anyList(), anyList());
        mockMvc.perform(put(BASE_URL + parentGroup + "/" + forumName).header(TOKEN_HEADER, myToken).param("newName",
            "German Shepherds"))
            .andExpect(status().isNoContent());
    }

    @Test
    public void updateName() throws Exception {
        willDoNothing().given(service).updateName(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(
            put(BASE_URL + "tags/" + parentGroup + "/" + forumName).header(TOKEN_HEADER, myToken).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new HashMap<String, List<String>>())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void postMessage() throws Exception {
        json = mapper.writeValueAsString(new MessageEntity());
        willDoNothing().given(service).postMessage(anyString(), anyString(), anyString(), any(Message.class));
        mockMvc.perform(post(BASE_URL + parentGroup + "/" + forumName).header(TOKEN_HEADER, myToken)
            .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isCreated());
    }

    @Test
    public void deleteMessage() throws Exception {
        willDoNothing().given(service).deleteMessage(anyString(), anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(
            delete(BASE_URL + parentGroup + "/" + forumName).header("token", myToken)
                .param("creator", creator).param("date", creationDate)).andExpect(status().isNoContent());
    }

    @Test
    public void reportMessage() throws Exception {
        willDoNothing().given(service).reportMessage(anyString(), anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(
            put(BASE_URL + parentGroup + "/" + forumName + "/report_message").header(TOKEN_HEADER, myToken)
                .param("creator", creator).param("date", creationDate).param("reporter", reporter))
            .andExpect(status().isNoContent());
    }

    @Test
    public void unbanMessage() throws Exception {
        willDoNothing().given(service).unbanMessage(anyString(), anyString(), anyString(), anyString(),
            anyString());
        mockMvc.perform(
            put(BASE_URL + parentGroup + "/" + forumName + "/unban_message").header("token", myToken)
                .param("creator", creator).param("date", creationDate)).andExpect(status().isNoContent());
    }

    @Test
    public void likeMessage() throws Exception {
        willDoNothing().given(service)
            .addUserToLikedByOfMessage(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(BASE_URL + parentGroup + "/" + forumName + "/messages").header(TOKEN_HEADER, myToken)
            .param("username", creator).param("creator", creator).param("date", creationDate).param("like", "true"))
            .andExpect(status().isNoContent());
    }

    @Test
    public void removeLikeFromMessage() throws Exception {
        willDoNothing().given(service)
            .removeUserFromLikedByOfMessage(anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString());
        mockMvc.perform(put(BASE_URL + parentGroup + "/" + forumName + "/messages").header(TOKEN_HEADER, myToken)
            .param("username", creator).param("creator", creator).param("date", creationDate).param("like", "false"))
            .andExpect(status().isNoContent());
    }
}
