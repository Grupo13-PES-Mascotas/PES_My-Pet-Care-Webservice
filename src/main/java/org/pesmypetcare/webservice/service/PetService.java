package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface PetService {

    /**
     * Creates a pet on the data base.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param petEntity The pet entity that contains the attributes of the pet
     */
    void createPet(String owner, String name, PetEntity petEntity);

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     */
    void deleteByOwnerAndName(String owner, String name) throws DatabaseAccessException;

    /**
     * Deletes all the pets of the specified owner from database.
     * @param owner Username of the owner of the pets
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllPets(String owner) throws DatabaseAccessException;

    /**
     * Gets a pet identified by its name and owner.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @return The PetEntity of the owner pet data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    PetEntity getPetData(String owner, String name) throws DatabaseAccessException;

    /**
     * Gets the data from all the specified pets from the database.
     * @param owner Username of the owner of the pets
     * @return The List containing all the owner pets data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllPetsData(String owner) throws DatabaseAccessException;

    /**
     * Gets the value for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    Object getField(String owner, String name, String field) throws DatabaseAccessException;

    /**
     * Updates the pet's field.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to update
     * @param value Value the field will have
     */
    void updateField(String owner, String name, String field, Object value);
}
