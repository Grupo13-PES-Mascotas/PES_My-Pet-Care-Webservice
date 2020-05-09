package org.pesmypetcare.webservice.dao.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.KcalAverageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface KcalAverageDao {
    /**
     * Creates a kcalAverage of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Creation date of the pet's kcalAverage
     * @param kcalAverageEntity The parameters of kcalAverage
     */
    void createKcalAverage(String owner, String petName, String date, KcalAverageEntity kcalAverageEntity);

    /**
     * Deletes all kcalAverages of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllKcalAverages(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the kcalAverage with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date ofDocument the kcalAverage instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteKcalAverageByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one kcalAverage ofDocument the pet with the specified date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the kcalAverage instance
     * @return The kcalAverageEntity with the data ofDocument kcalAverage
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    KcalAverageEntity getKcalAverageByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Gets all the specified kcalAverages ofDocument one pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @return The list with all ofDocument the kcalAverages
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllKcalAverage(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the kcalAverages in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllKcalAveragesBetween(String owner, String petName, String initialDate,
                                                 String finalDate) throws DatabaseAccessException;

    /**
     * Updates one ofDocument the kcalAverages specified with the date and the pet name.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the kcalAverage instance
     * @param value Value ofDocument the new kcalAverage
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateKcalAverage(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
