package org.pesmypetcare.webservice.service.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.WeightEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface WeightService {

    /**
     * Creates a weight ofDocument the pet on the database.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date Creation date ofDocument the pet's weight
     * @param weightEntity The parameters ofDocument weight
     */
    void createWeight(String owner, String petName, String date, WeightEntity weightEntity);

    /**
     * Deletes all weights ofDocument the pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllWeights(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the weight with the specified pet and date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the weight instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteWeightByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one weight ofDocument the pet with the specified date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the weight instance
     * @return The WeightEntity with the data ofDocument weight
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    WeightEntity getWeightByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Gets all the specified weights ofDocument one pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @return The list with all ofDocument the weights
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllWeight(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the weights in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllWeightsBetween(String owner, String petName, String initialDate,
                                                   String finalDate) throws DatabaseAccessException;

    /**
     * Updates one ofDocument the weights specified with the date and the pet name.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the weight instance
     * @param value Value ofDocument the new weight
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateWeight(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
