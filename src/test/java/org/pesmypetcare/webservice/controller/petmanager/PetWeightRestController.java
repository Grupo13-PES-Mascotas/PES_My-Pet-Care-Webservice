package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.WeightEntity;
import org.pesmypetcare.webservice.service.WeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class PetWeightRestController {
    private static String jsonWeightEntity;
    private static String jsonField;
    private static WeightEntity weightEntity;
    private static List<Map<String, Object>> weightEntityList;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static String field;
    private static String value;
    private static String urlBase;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeightService service;

    @BeforeAll
    public static void setUp() {
        jsonWeightEntity = "{\n"
            + "  \"weightValue\": \"5,3\",\n"
            + "}";
        weightEntity = new WeightEntity();
        weightEntityList = new ArrayList<>();
        owner = "Albert";
        petName = "Manolito";
        date = "2018-10-22T00:47:00";
        date2 = "2020-10-22T00:47:00";
        value = "7,5";
        urlBase = "/pet/weight";
    }
}
