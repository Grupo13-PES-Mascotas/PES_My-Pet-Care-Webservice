package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.entity.FreqTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface FreqTrainingService {

    /**
     * Creates a freqTraining of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Creation date of the pet's freqTraining
     * @param freqTrainingEntity The parameters of freqTraining
     */
    void createFreqTraining(String owner, String petName, String date, FreqTrainingEntity freqTrainingEntity);

    /**
     * Deletes all freqTrainings of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllFreqTrainings(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the freqTraining with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the freqTraining instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteFreqTrainingByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one freqTraining of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the freqTraining instance
     * @return The FreqTrainingEntity with the data of freqTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    FreqTrainingEntity getFreqTrainingByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Gets all the specified freqTrainings of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the freqTrainings
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllFreqTraining(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the freqTrainings in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllFreqTrainingsBetween(String owner, String petName, String initialDate,
                                                     String finalDate) throws DatabaseAccessException;

    /**
     * Updates one of the freqTrainings specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the freqTraining instance
     * @param value Value of the new freqTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateFreqTraining(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
