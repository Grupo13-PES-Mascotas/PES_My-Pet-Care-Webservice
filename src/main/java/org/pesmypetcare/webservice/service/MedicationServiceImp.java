package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.dao.MedicationDao;
import org.pesmypetcare.webservice.entity.MedicationEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class MedicationServiceImp implements MedicationService {
    @Autowired
    private MedicationDao medicationDao;

    @Override
    public void createMedication(String owner, String petName, String dateName, MedicationEntity medication) {
        medicationDao.createMedication(owner, petName, dateName, medication);
    }

    @Override
    public void deleteByDateAndName(String owner, String petName, String dateName) {
        medicationDao.deleteByDateAndName(owner, petName, dateName);
    }

    @Override
    public void deleteByDate(String owner, String petName, String date) throws DatabaseAccessException {
        medicationDao.deleteByDate(owner, petName, date);
    }

    @Override
    public void deleteByName(String owner, String petName, String medicationName) throws DatabaseAccessException {
        medicationDao.deleteByName(owner, petName, medicationName);
    }

    @Override
    public void deleteAllMedications(String owner, String petName) throws DatabaseAccessException {
        medicationDao.deleteAllMedications(owner, petName);
    }

    @Override
    public MedicationEntity getMedicationData(String owner, String petName, String dateName) throws DatabaseAccessException {
        return medicationDao.getMedicationData(owner, petName, dateName);
    }

    @Override
    public List<Map<List<String>, Object>> getAllMedicationData(String owner, String petName) throws DatabaseAccessException {
        return medicationDao.getAllMedicationData(owner, petName);
    }

    @Override
    public List<Map<List<String>, Object>> getAllMMedicationsBetween(String owner, String petName, String initialDate, String finalDate) throws DatabaseAccessException {
        return medicationDao.getAllMMedicationsBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public Object getMedicationField(String owner, String petName, String dateName, String field) throws DatabaseAccessException {
        return medicationDao.getMedicationField(owner, petName, dateName, field);
    }

    @Override
    public void updateMedicationField(String owner, String petName, String dateName, String field, Object value) {
        medicationDao.updateMedicationField(owner, petName, dateName, field, value);
    }
}
