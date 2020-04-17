package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.dao.GroupDao;
import org.pesmypetcare.webservice.entity.GroupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupDao dao;

    @Override
    public void createGroup(String name, GroupEntity entity) {

    }

    @Override
    public void deleteGroup(String name) {

    }

    @Override
    public GroupEntity getGroup(String name) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getAllGroups() {
        return null;
    }

    @Override
    public void updateField(String name, String field, String newValue) {

    }
}
