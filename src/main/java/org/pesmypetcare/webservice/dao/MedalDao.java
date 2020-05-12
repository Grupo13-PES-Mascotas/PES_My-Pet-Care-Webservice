package org.pesmypetcare.webservice.dao;

import org.pesmypetcare.webservice.entity.MedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
public interface MedalDao {

    /**
     * Creates a medal on the data base.
     * @param owner Username of the owner of the medal
     * @param name Name of the medal
     * @param medalEntity The medal entity that contains the attributes of the medal
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void createMedal(String owner, String name, MedalEntity medalEntity) throws DatabaseAccessException, DocumentException;

    /**
     * Gets a medal identified by its name and owner.
     * @param owner Username of the owner of the medal
     * @param name Name of the medal
     * @return The MedalEntity corresponding to the owner's medal data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    MedalEntity getMedalData(String owner, String name) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the data from all the specified medals from the database.
     * @param owner Username of the owner of the medals
     * @return The List containing all the owner medals data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<Map<String, Object>> getAllMedalsData(String owner) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the value for the specified field of the medal on the database.
     * @param owner Username of the owner of the medals
     * @param name Name of the medal
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Object getSimpleField(String owner, String name, String field) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the map for the specified field of the medal on the database.
     * @param owner Username of the owner of the medal
     * @param name Name of the medal
     * @param field Name of the field to retrieve
     * @return The map from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<Map<String, Object>> getFieldCollection(String owner, String name, String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets an element from the map for the specified field of the medal on the database.
     * @param owner Username of the owner of the medals
     * @param name Name of the medal
     * @param field Name of the field
     * @param key Key of the element
     * @return Element assigned to the key
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Map<String, Object> getFieldCollectionElement(String owner, String name, String field, String key)
        throws DatabaseAccessException, DocumentException;

}
