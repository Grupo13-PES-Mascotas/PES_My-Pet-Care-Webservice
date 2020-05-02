package org.pesmypetcare.webservice.service.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.WeekTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface WeekTrainingService {

    /**
     * Creates a weekTraining ofDocument the pet on the database.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date Creation date ofDocument the pet's weekTraining
     * @param weekTrainingEntity The parameters ofDocument weekTraining
     */
    void createWeekTraining(String owner, String petName, String date, WeekTrainingEntity weekTrainingEntity);

    /**
     * Deletes all weekTrainings ofDocument the pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllWeekTrainings(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the weekTraining with the specified pet and date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the weekTraining instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteWeekTrainingByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one weekTraining ofDocument the pet with the specified date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the weekTraining instance
     * @return The WeekTrainingEntity with the data ofDocument weekTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    WeekTrainingEntity getWeekTrainingByDate(String owner, String petName, String petDate)
        throws DatabaseAccessException;

    /**
     * Gets all the specified weekTrainings ofDocument one pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @return The list with all ofDocument the weekTrainings
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllWeekTraining(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the weekTrainings in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllWeekTrainingsBetween(String owner, String petName, String initialDate,
                                                         String finalDate) throws DatabaseAccessException;

    /**
     * Updates one ofDocument the weekTrainings specified with the date and the pet name.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the weekTraining instance
     * @param value Value ofDocument the new weekTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateWeekTraining(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
