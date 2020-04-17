package org.pesmypetcare.webservice.controller.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.MealEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.petmanager.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meal")
public class PetMealRestController {
    @Autowired
    private MealService mealService;

    /**
     * Creates a meal eaten by a pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param meal The meal entity that contains the attributes of the meal eaten by the pet
     */
    @PostMapping("/{owner}/{petName}/{date}")
    public void createMeal(@PathVariable String owner, @PathVariable String petName, @PathVariable String date,
                    @RequestBody MealEntity meal) {
        mealService.createMeal(owner, petName, date, meal);
    }

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     */
    @DeleteMapping("/{owner}/{petName}/{date}")
    public void deleteByDate(@PathVariable String owner, @PathVariable String petName, @PathVariable String date) {
        mealService.deleteByDate(owner, petName, date);
    }

    /**
     * Deletes all the meals of the specified pet from database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteAllMeals(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        mealService.deleteAllMeals(owner, petName);
    }

    /**
     * Gets a meal identified by its pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @return The MealEntity identified by the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{date}")
    public MealEntity getMealData(@PathVariable String owner, @PathVariable String petName, @PathVariable String date)
        throws DatabaseAccessException {
        return mealService.getMealData(owner, petName, date);
    }

    /**
     * Gets the data from all the specified meals from the database identified by its pet.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @return The List containing all the meals from the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllMealData(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        return mealService.getAllMealData(owner, petName);
    }

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the meals eaten by the pet in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/between/{initialDate}/{finalDate}")
    public List<Map<String, Object>> getAllMealsBetween(@PathVariable String owner, @PathVariable String petName,
                                                 @PathVariable String initialDate, @PathVariable String finalDate)
        throws DatabaseAccessException {
        return mealService.getAllMealsBetween(owner, petName, initialDate, finalDate);
    }

    /**
     * Gets the value for the specified field of the meal on the database.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{date}/{field}")
    public Object getMealField(@PathVariable String owner, @PathVariable String petName, @PathVariable String date,
                        @PathVariable String field) throws DatabaseAccessException {
        return mealService.getMealField(owner, petName, date, field);
    }

    /**
     * Updates the meal's field.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param field Name of the field to update
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *      *                key "value"
     */
    @PutMapping("/{owner}/{petName}/{date}/{field}")
    public void updateMealField(@PathVariable String owner, @PathVariable String petName, @PathVariable String date,
                         @PathVariable String field, @RequestBody Map<String, Object> valueMap) {
        mealService.updateMealField(owner, petName, date, field, valueMap.get("value"));
    }
}
