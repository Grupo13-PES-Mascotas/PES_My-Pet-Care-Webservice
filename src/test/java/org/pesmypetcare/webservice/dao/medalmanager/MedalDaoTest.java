package org.pesmypetcare.webservice.dao.medalmanager;


import com.google.cloud.firestore.DocumentSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.medalmanager.Medal;
import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
/**
 * @author Oriol Catalán
 */
@ExtendWith(MockitoExtension.class)
public class MedalDaoTest {
    private static final String MEDAL_NAME = "MedalName";
    private static final String FIELD = "description";
    private static final String VALUE = "You have to walk a lot of kilometers!";

    private static Medal medal;
    private static MedalEntity medalEntity;
    private static List<Map<String, MedalEntity>> medalList;
    private static List<DocumentSnapshot> snapshotList;


    @Mock
    private FirestoreCollection dbCol;
    @Mock
    private FirestoreDocument dbDoc;
    @Mock
    private DocumentSnapshot documentSnapshot;

    @InjectMocks
    private MedalDao medalDao = new MedalDaoImpl();

    @BeforeEach
    public void setUp() {
        medal = new Medal();
        medal.setName("Walker");
        medal.setDescription("You have to walk a lot!");
        medal.setLevels(new ArrayList<>(Arrays.asList(5., 10., 25., 50., 100.)));
        medal.setMedalIconPath(new byte[] {10, 55, 67 ,89, 103, 116});
        medalList = new ArrayList<>();
        Map<String, MedalEntity> auxMap = new HashMap<>();
        auxMap.put("body", medalEntity);
        medalList.add(auxMap);
        medalList.add(auxMap);
        snapshotList = new ArrayList<>();
        snapshotList.add(documentSnapshot);
        snapshotList.add(documentSnapshot);
    }

    @Test
    public void shouldReturnMedalEntityFromDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getDocumentDataAsObject(anyString(), any())).willReturn(medalEntity);

        MedalEntity result = medalDao.getMedalData(MEDAL_NAME);

        assertSame(medalEntity, result, "Should return Medal Entity");
    }

    @Test
    public void shouldReturnAllMedalsDataOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(documentSnapshot.toObject(any())).willReturn(medalEntity);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(snapshotList);
        List<Map<String, MedalEntity>> list = medalDao.getAllMedalsData();

        assertEquals(medalList, list, "Should return a List containing all medals Data");
    }

    @Test
    public void shouldReturnMedalFieldFromDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getDocumentField(anyString(), anyString())).willReturn(VALUE);

        Object medalValue = medalDao.getField(MEDAL_NAME, FIELD);

        assertSame(VALUE, medalValue, "Should return field value");
    }
}
