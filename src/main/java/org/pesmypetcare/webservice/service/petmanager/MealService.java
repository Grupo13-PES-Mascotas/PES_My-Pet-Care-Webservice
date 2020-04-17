package org.pesmypetcare.webservice.service.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.MealEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

public interface MealService {

    /**
     * Creates a meal eaten by a pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param meal The meal entity that contains the attributes of the meal eaten by the pet
     */
    void createMeal(String owner, String petName, String date, MealEntity meal);

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     */
    void deleteByDate(String owner, String petName, String date);

    /**
     * Deletes all the meals of the specified pet from database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllMeals(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets a meal identified by its pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @return The MealEntity identified by the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    MealEntity getMealData(String owner, String petName, String date) throws DatabaseAccessException;

    /**
     * Gets the data from all the specified meals from the database identified by its pet.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @return The List containing all the meals from the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllMealData(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the meals eaten by the pet in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllMealsBetween(String owner, String petName, String initialDate,
                                                 String finalDate) throws DatabaseAccessException;

    /**
     * Gets the value for the specified field of the meal on the database.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    Object getMealField(String owner, String petName, String date, String field) throws DatabaseAccessException;

    /**
     * Updates the meal's field.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param field Name of the field to update
     * @param value Value the field will have
     */
    void updateMealField(String owner, String petName, String date, String field, Object value);
}
