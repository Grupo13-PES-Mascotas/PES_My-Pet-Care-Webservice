package org.pesmypetcare.webservice.service.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.FreqWashEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface FreqWashService {

    /**
     * Creates a freqWash ofDocument the pet on the database.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date Creation date ofDocument the pet's freqWash
     * @param freqWashEntity The parameters ofDocument freqWash
     */
    void createFreqWash(String owner, String petName, String date, FreqWashEntity freqWashEntity);

    /**
     * Deletes all freqWashs ofDocument the pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllFreqWashs(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the freqWash with the specified pet and date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the freqWash instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteFreqWashByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one freqWash ofDocument the pet with the specified date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the freqWash instance
     * @return The FreqWashEntity with the data ofDocument freqWash
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    FreqWashEntity getFreqWashByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Gets all the specified freqWashs ofDocument one pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @return The list with all ofDocument the freqWashs
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllFreqWash(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the freqWashs in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllFreqWashsBetween(String owner, String petName, String initialDate,
                                                 String finalDate) throws DatabaseAccessException;

    /**
     * Updates one ofDocument the freqWashs specified with the date and the pet name.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the freqWash instance
     * @param value Value ofDocument the new freqWash
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateFreqWash(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
