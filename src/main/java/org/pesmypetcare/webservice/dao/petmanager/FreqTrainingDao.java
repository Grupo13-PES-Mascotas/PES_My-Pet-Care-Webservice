package org.pesmypetcare.webservice.dao.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.FreqTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface FreqTrainingDao {
    /**
     * Creates a freqTraining ofDocument the pet on the database.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date Creation date ofDocument the pet's freqTraining
     * @param freqTrainingEntity The parameters ofDocument freqTraining
     */
    void createFreqTraining(String owner, String petName, String date, FreqTrainingEntity freqTrainingEntity);

    /**
     * Deletes all freqTrainings ofDocument the pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllFreqTrainings(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the freqTraining with the specified pet and date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the freqTraining instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteFreqTrainingByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one freqTraining ofDocument the pet with the specified date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the freqTraining instance
     * @return The freqTrainingEntity with the data ofDocument freqTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    FreqTrainingEntity getFreqTrainingByDate(String owner, String petName, String petDate)
        throws DatabaseAccessException;

    /**
     * Gets all the specified freqTrainings ofDocument one pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @return The list with all ofDocument the freqTrainings
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllFreqTraining(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the freqtrainings by the pet between the initial and final date not including them.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the freqTrainings in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllFreqTrainingsBetween(String owner, String petName, String initialDate,
                                                     String finalDate) throws DatabaseAccessException;

    /**
     * Updates one ofDocument the freqTrainings specified with the date and the pet name.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the freqTraining instance
     * @param value Value ofDocument the new freqTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateFreqTraining(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
