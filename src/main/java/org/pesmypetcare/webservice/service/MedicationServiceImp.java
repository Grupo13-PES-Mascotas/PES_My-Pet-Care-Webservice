package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.dao.MedicationDao;
import org.pesmypetcare.webservice.entity.MedicationEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MedicationServiceImp implements MedicationService {
    @Autowired
    private MedicationDao medicationDao;

    @Override
    public void createMedication(String owner, String petName, String date, String name,
                                 MedicationEntity medication) {
        medicationDao.createMedication(owner, petName, date, name, medication);
    }

    @Override
    public void deleteByDateAndName(String owner, String petName, String date, String name) {
        medicationDao.deleteByDateAndName(owner, petName, date, name);
    }

    @Override
    public void deleteAllMedications(String owner, String petName) throws DatabaseAccessException {
        medicationDao.deleteAllMedications(owner, petName);
    }

    @Override
    public MedicationEntity getMedicationData(String owner, String petName, String date, String name)
            throws DatabaseAccessException {
        return medicationDao.getMedicationData(owner, petName, date, name);
    }

    @Override
    public List<Map<String, Object>> getAllMedicationData(String owner, String petName)
        throws DatabaseAccessException {
        return medicationDao.getAllMedicationData(owner, petName);
    }

    @Override
    public List<Map<String, Object>> getAllMedicationsBetween(String owner, String petName,
                                                                    String initialDate, String finalDate)
        throws DatabaseAccessException {
        return medicationDao.getAllMedicationsBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public Object getMedicationField(String owner, String petName, String date, String name, String field)
            throws DatabaseAccessException {
        return medicationDao.getMedicationField(owner, petName, date, name, field);
    }

    @Override
    public void updateMedicationField(String owner, String petName, String date, String name, String field,
                                      Object value) {
        medicationDao.updateMedicationField(owner, petName, date, name, field, value);
    }
}
