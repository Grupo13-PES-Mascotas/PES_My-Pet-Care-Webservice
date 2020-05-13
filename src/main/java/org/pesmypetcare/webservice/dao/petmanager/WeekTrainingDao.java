package org.pesmypetcare.webservice.dao.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.WeekTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
public interface WeekTrainingDao {
    /**
     * Creates a weekTraining of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Creation date of the pet's weekTraining
     * @param weekTrainingEntity The parameters of weekTraining
     */
    void createWeekTraining(String owner, String petName, String date, WeekTrainingEntity weekTrainingEntity);

    /**
     * Deletes all weekTrainings of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllWeekTrainings(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the weekTraining with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weekTraining instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteWeekTrainingByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one weekTraining of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weekTraining instance
     * @return The weekTrainingEntity with the data of weekTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    WeekTrainingEntity getWeekTrainingByDate(String owner, String petName, String petDate)
        throws DatabaseAccessException;

    /**
     * Gets all the specified weekTrainings of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the weekTrainings
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllWeekTraining(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the weekTrainings in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllWeekTrainingsBetween(String owner, String petName, String initialDate,
                                                         String finalDate) throws DatabaseAccessException;

    /**
     * Updates one of the weekTrainings specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weekTraining instance
     * @param value Value of the new weekTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateWeekTraining(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
