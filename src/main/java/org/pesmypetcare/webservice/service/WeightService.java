package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.entity.WeightEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface WeightService {

    /**
     * Creates a weight of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param weightEntity The parameters of weight
     */
    void createWeight(String owner, String petName, String date, WeightEntity weightEntity);

    /**
     * Deletes all weights of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllWeights(String owner, String petName)  throws DatabaseAccessException;

    /**
     * Deletes the weight with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weight instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteWeightByDate(String owner, String petName, String petDate)  throws DatabaseAccessException;

    /**
     * Get one weight of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weight instance
     * @return The WeightEntity with the data of weight
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    WeightEntity getWeightByDate(String owner, String petName, String petDate)  throws DatabaseAccessException;

    /**
     * Gets all the specified weights of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the weights
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllWeight(String owner, String petName)  throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the weights in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllWeightsBetween(String owner, String petName, String initialDate,
                                                   String finalDate) throws DatabaseAccessException;

    /**
     * Updates one of the weights specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weight instance
     * @param value Value of the new weight
     * @param field Value of field to update
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateWeight(String owner, String petName, String petDate, double value, String field)  throws DatabaseAccessException;
}
