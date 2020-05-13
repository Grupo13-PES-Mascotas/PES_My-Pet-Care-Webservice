package org.pesmypetcare.webservice.service.medalmanager;

import org.pesmypetcare.webservice.dao.medalmanager.MedalDao;
import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
@Service
public class MedalServiceImpl implements MedalService {
    @Autowired
    private MedalDao medalDao;

    @Override
    public MedalEntity getMedalData(String name) throws DatabaseAccessException, DocumentException {
        return medalDao.getMedalData(name);
    }

    @Override
    public List<Map<String, Object>> getAllMedalsData() throws DatabaseAccessException, DocumentException {
        return medalDao.getAllMedalsData();
    }

    @Override
    public Object getSimpleField(String name, String field) throws DatabaseAccessException, DocumentException {
        return medalDao.getSimpleField(name, field);
    }
}
