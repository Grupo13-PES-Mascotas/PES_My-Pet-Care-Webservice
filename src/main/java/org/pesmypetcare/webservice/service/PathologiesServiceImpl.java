package org.pesmypetcare.webservice.service;


import org.pesmypetcare.webservice.dao.PathologiesDao;
import org.pesmypetcare.webservice.entity.PathologiesEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PathologiesServiceImpl implements PathologiesService {
    @Autowired
    private PathologiesDao pathologiesDao;


    @Override
    public void createPathologies(String owner, String petName, String date, PathologiesEntity pathologiesEntity) {
        pathologiesDao.createPathologies(owner, petName, date, pathologiesEntity);
    }

    @Override
    public void deleteAllPathologiess(String owner, String petName) throws DatabaseAccessException {
        pathologiesDao.deleteAllPathologiess(owner, petName);
    }

    @Override
    public void deletePathologiesByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        pathologiesDao.deletePathologiesByDate(owner, petName, petDate);
    }

    @Override
    public PathologiesEntity getPathologiesByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        return pathologiesDao.getPathologiesByDate(owner, petName, petDate);
    }

    @Override
    public List<Map<String, Object>> getAllPathologies(String owner, String petName) throws DatabaseAccessException {
        return pathologiesDao.getAllPathologies(owner, petName);
    }

    @Override
    public List<Map<String, Object>> getAllPathologiessBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        return pathologiesDao.getAllPathologiessBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public void updatePathologies(String owner, String petName, String petDate, Object value)
        throws DatabaseAccessException {
        pathologiesDao.updatePathologies(owner, petName, petDate, value);
    }
}
