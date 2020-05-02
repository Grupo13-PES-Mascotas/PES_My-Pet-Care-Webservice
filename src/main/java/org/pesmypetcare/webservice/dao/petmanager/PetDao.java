package org.pesmypetcare.webservice.dao.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

/**
 * @author Marc Simó
 */
public interface PetDao {

    /**
     * Creates a pet on the data base.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param name Name ofDocument the pet
     * @param petEntity The pet entity that contains the attributes ofDocument the pet
     */
    void createPet(String owner, String name, PetEntity petEntity);

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param name Name ofDocument the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteByOwnerAndName(String owner, String name) throws DatabaseAccessException;

    /**
     * Deletes all the pets ofDocument the specified owner from database.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllPets(String owner) throws DatabaseAccessException;

    /**
     * Gets a pet identified by its name and owner.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param name Name ofDocument the pet
     * @return The PetEntity ofDocument the owner data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    PetEntity getPetData(String owner, String name) throws DatabaseAccessException;

    /**
     * Gets the data from all the specified pets from the database.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @return The List containing all the owner pets data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllPetsData(String owner) throws DatabaseAccessException;

    /**
     * Gets the value for the specified field ofDocument the pet on the database.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param name Name ofDocument the pet
     * @param field Name ofDocument the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    Object getField(String owner, String name, String field) throws DatabaseAccessException;

    /**
     * Updates the pet's field.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param name Name ofDocument the pet
     * @param field Name ofDocument the field to update
     * @param value Value the field will have
     */
    void updateField(String owner, String name, String field, Object value);
}
