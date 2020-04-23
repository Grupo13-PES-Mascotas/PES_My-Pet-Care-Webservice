package org.pesmypetcare.webservice.dao;

import org.pesmypetcare.webservice.entity.KcalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface KcalDao {
    /**
     * Creates a kcal of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Creation date of the pet's kcal
     * @param kcalEntity The parameters of kcal
     */
    void createKcal(String owner, String petName, String date, KcalEntity kcalEntity);

    /**
     * Deletes all kcals of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllKcals(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the kcal with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the kcal instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteKcalByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one kcal of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the kcal instance
     * @return The kcalEntity with the data of kcal
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    KcalEntity getKcalByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Gets all the specified kcals of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the kcals
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllKcal(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the kcals in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllKcalsBetween(String owner, String petName, String initialDate,
                                                   String finalDate) throws DatabaseAccessException;

    /**
     * Updates one of the kcals specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the kcal instance
     * @param value Value of the new kcal
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateKcal(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
