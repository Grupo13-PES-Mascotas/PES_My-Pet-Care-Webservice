package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.entity.PathologiesEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface PathologiesService {

    /**
     * Creates a pathologies of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Creation date of the pet's pathologies
     * @param pathologiesEntity The parameters of pathologies
     */
    void createPathologies(String owner, String petName, String date, PathologiesEntity pathologiesEntity);

    /**
     * Deletes all pathologiess of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllPathologiess(String owner, String petName) throws DatabaseAccessException;

    /**
     * Deletes the pathologies with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the pathologies instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deletePathologiesByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Get one pathologies of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the pathologies instance
     * @return The PathologiesEntity with the data of pathologies
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    PathologiesEntity getPathologiesByDate(String owner, String petName, String petDate) throws DatabaseAccessException;

    /**
     * Gets all the specified pathologiess of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the pathologiess
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllPathologies(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the pathologiess in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllPathologiessBetween(String owner, String petName, String initialDate,
                                                   String finalDate) throws DatabaseAccessException;

    /**
     * Updates one of the pathologiess specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the pathologies instance
     * @param value Value of the new pathologies
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updatePathologies(String owner, String petName, String petDate, Object value) throws DatabaseAccessException;
}
