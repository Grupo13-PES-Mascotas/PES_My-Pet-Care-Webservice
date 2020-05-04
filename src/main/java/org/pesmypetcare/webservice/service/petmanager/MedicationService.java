package org.pesmypetcare.webservice.service.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.MedicationEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface MedicationService {
    /**
     * Creates a medication eaten by a pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date date of the medication
     * @param name name ofDocument the medication
     * @param medication The medication entity that has the attributes ofDocument the medication for the pet.
     */
    void createMedication(String owner, String petName, String date, String name, MedicationEntity medication);

    /**
     * Deletes all the medication with the specified owner and pet from certain date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date date ofDocument the medication
     * @param name name ofDocument the medication
     */
    void deleteByDateAndName(String owner, String petName, String date, String name);

    /**
     * Deletes all the medications ofDocument the specified pet from database.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllMedications(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets a medication identified by its pet, date and name.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date date ofDocument the medication
     * @param name name ofDocument the medication
     * @return The MedicationEntity identified by the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    MedicationEntity getMedicationData(String owner, String petName, String date, String name)
            throws DatabaseAccessException;

    /**
     * Gets the data from all the specified medications from the database identified by its pet.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @return The List containing all the medications ofDocument the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllMedicationData(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the medications ofDocument the pet between the initial and final date not including them.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the medication ofDocument the pet in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllMedicationsBetween(String owner, String petName, String initialDate,
                                                              String finalDate) throws DatabaseAccessException;

    /**
     * Gets the value for the specified field ofDocument the medication on the database.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @param date date ofDocument the medication
     * @param name name ofDocument the medication
     * @param field Name ofDocument the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    Object getMedicationField(String owner, String petName, String date, String name, String field)
            throws DatabaseAccessException;

    /**
     * Updates the medication's field.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date date ofDocument the medication
     * @param name name ofDocument the medication
     * @param field Name ofDocument the field to update
     * @param value Value the field will have
     */
    void updateMedicationField(String owner, String petName, String date, String name, String field,
                               Object value);

}
