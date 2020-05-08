package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.entity.FreqWashEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
public interface FreqWashService {

    /**
     * Creates a freqWash of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Creation date of the pet's freqWash
     * @param freqWashEntity The parameters of freqWash
     */
    void createFreqWash(String owner, String petName, String date, FreqWashEntity freqWashEntity);

    /**
     * Deletes all freqWashs of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllFreqWashs(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the freqWash with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the freqWash instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteFreqWashByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one freqWash of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the freqWash instance
     * @return The FreqWashEntity with the data of freqWash
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    FreqWashEntity getFreqWashByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Gets all the specified freqWashs of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the freqWashs
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllFreqWash(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the freqWashs in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllFreqWashsBetween(String owner, String petName, String initialDate,
                                                 String finalDate) throws DatabaseAccessException;

    /**
     * Updates one of the freqWashs specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the freqWash instance
     * @param value Value of the new freqWash
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateFreqWash(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
