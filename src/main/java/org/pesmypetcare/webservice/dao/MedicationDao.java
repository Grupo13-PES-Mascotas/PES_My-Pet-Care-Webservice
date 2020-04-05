
package org.pesmypetcare.webservice.dao;

import org.pesmypetcare.webservice.entity.MedicationEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;



public interface MedicationDao {
    /**
     * Creates a medication eaten by a pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date of the receipt of the medication
     * @param name Name of the medicine
     * @param medication The medication entity that has the attributes of the medication for the pet.
     */
    void createMedication(String owner, String petName, String date, String name,MedicationEntity medication);

    /**
     * Deletes all the medication with the specified owner and pet from certain date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date of the receipt of the medication
     * @param name Name of the medication
     */
    void deleteByDateAndName(String owner, String petName, String date, String name);

    /**
     * Deletes all the medication with the specified owner and pet from certain date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date of the receipt of the medication
     */
    void deleteByDate(String owner, String petName, String date) throws DatabaseAccessException;

    /**
     * Deletes all the medication with the specified owner and pet from certain date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param medicationName Name of the medication
     */
    void deleteByName(String owner, String petName, String medicationName) throws DatabaseAccessException;

    /**
     * Deletes all the medications of the specified pet from database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllMedications(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets a medication identified by its pet, date and name
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the medication was receipt.
     * @param name Name of the medication
     * @return The MedicationEntity identified by the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    MedicationEntity getMedicationData(String owner, String petName, String date, String name) throws DatabaseAccessException;

    /**
     * Gets the data from all the specified medications from the database identified by its pet.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @return The List containing all the medications of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<List<String>, Object>> getAllMedicationData(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the medications of the pet between the initial and final date not including them
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the medication of the pet in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<List<String>, Object>> getAllMMedicationsBetween(String owner, String petName, String initialDate,
                                                              String finalDate) throws DatabaseAccessException;

    /**
     * Gets the value for the specified field of the medication on the database.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param date Date of the medication
     * @param name name of the medication
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    Object getMedicationField(String owner, String petName, String date, String name, String field) throws DatabaseAccessException;

    /**
     * Updates the medication's field.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the medication
     * @param name Name of the medication
     * @param field Name of the field to update
     * @param value Value the field will have
     */
    void updateMedicationField(String owner, String petName, String date, String name, String field, Object value);


}
