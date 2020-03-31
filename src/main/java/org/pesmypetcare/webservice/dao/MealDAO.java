package org.pesmypetcare.webservice.dao;

import org.pesmypetcare.webservice.entity.MealEntity;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MealDAO {

    /**
     * Creates a meal eaten by a pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param meal The meal entity that contains the attributes of the meal eaten by the pet
     */
    void createMeal(String owner, String petName, MealEntity meal);

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param hour Hour the meal was eaten
     */
    void deleteByDateAndHour(String owner, String petName, Date date, LocalTime hour);

    /**
     * Deletes all the meals of the specified pet from database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteAllMeals(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets a meal identified by its pet, date and hour.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param hour Hour the meal was eaten
     * @return The MealEntity identified by the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    PetEntity getMealData(String owner, String petName, Date date, LocalTime hour) throws DatabaseAccessException;

    /**
     * Gets the data from all the specified meals from the database identified by its pet.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @return The List containing all the meals from the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllMealData(String owner, String petName) throws DatabaseAccessException;

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final time including both
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param initialHour Initial Hour
     * @param finalDate Final Date
     * @param finalHour Final Hour
     * @return The List containing all the meals eaten by the pet in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Map<String, Object>> getAllMealsBetween(String owner, String petName, Date initialDate, LocalTime initialHour,
                                                 Date finalDate, LocalTime finalHour) throws DatabaseAccessException;

    /**
     * Gets the value for the specified field of the meal on the database.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param hour Hour the meal was eaten
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    Object getMealField(String owner, String petName, Date date, LocalTime hour, String field) throws DatabaseAccessException;

    /**
     * Updates the meal's field.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param hour Hour the meal was eaten
     * @param field Name of the field to update
     * @param value Value the field will have
     */
    void updateMealField(String owner, String petName, Date date, LocalTime hour, String field, Object value);
}
