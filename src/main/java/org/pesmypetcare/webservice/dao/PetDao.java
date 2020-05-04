package org.pesmypetcare.webservice.dao;

import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;
import java.util.Map;

/**
 * @author Marc Simó
 */
public interface PetDao {

    /**
     * Creates a pet on the data base.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param petEntity The pet entity that contains the attributes of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void createPet(String owner, String name, PetEntity petEntity) throws DatabaseAccessException, DocumentException;

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void deleteByOwnerAndName(String owner, String name) throws DatabaseAccessException, DocumentException;

    /**
     * Deletes all the pets of the specified owner from database.
     * @param owner Username of the owner of the pets
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void deleteAllPets(String owner) throws DatabaseAccessException, DocumentException;

    /**
     * Gets a pet identified by its name and owner.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @return The PetEntity of the owner data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    PetEntity getPetData(String owner, String name) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the data from all the specified pets from the database.
     * @param owner Username of the owner of the pets
     * @return The List containing all the owner pets data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<Map<String, Object>> getAllPetsData(String owner) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the value for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Object getSimpleField(String owner, String name, String field) throws DatabaseAccessException, DocumentException;

    /**
     * Updates the pet's field.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to update
     * @param value Value the field will have
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void updateSimpleField(String owner, String name, String field, Object value)
        throws DatabaseAccessException, DocumentException;

    /**
     * Deletes the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void deleteFieldCollection(String owner, String name, String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to retrieve
     * @return The map from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<Map<String, Object>> getFieldCollection(String owner, String name, String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets all the elements between the keys from the map for the specified field.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key1 Start key (This one included)
     * @param key2 End Key (This one included)
     * @return The map containing the elements between the keys
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<Map<String, Object>> getFieldCollectionElementsBetweenKeys(String owner, String name, String field,
                                                                    String key1, String key2)
        throws DatabaseAccessException, DocumentException;

    /**
     * Adds an element to the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the new element to be added
     * @param body Element to be added
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void addFieldCollectionElement(String owner, String name, String field, String key, Map<String, Object> body)
        throws DatabaseAccessException, DocumentException;

    /**
     * Deletes an element from the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void deleteFieldCollectionElement(String owner, String name, String field, String key)
        throws DatabaseAccessException, DocumentException;

    /**
     * Updates an element from the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element to update
     * @param body Update of the element
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void updateFieldCollectionElement(String owner, String name, String field, String key, Map<String, Object> body)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets an element from the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element
     * @return Element assigned to the key
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Map<String, Object> getFieldCollectionElement(String owner, String name, String field, String key)
        throws DatabaseAccessException, DocumentException;

}
