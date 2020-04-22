package org.pesmypetcare.webservice.controller.petmanager;


import org.pesmypetcare.webservice.entity.WeekTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.WeekTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weekTraining")
public class WeekTrainingRestController {
    @Autowired
    private WeekTrainingService weekTrainingService;

    /**
     * Creates a weekTraining of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param weekTrainingEntity The parameters of weekTraining
     */
    @PostMapping("/{owner}/{petName}/{petDate}")
    public void createWeekTraining(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate, @RequestBody WeekTrainingEntity weekTrainingEntity) {
        weekTrainingService.createWeekTraining(owner, petName, petDate, weekTrainingEntity);
    }

    /**
     * Deletes all weekTrainings of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteAllWeekTrainings(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        weekTrainingService.deleteAllWeekTrainings(owner, petName);
    }

    /**
     * Deletes the weekTraining with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weekTraining instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}/{petDate}")
    public void deleteWeekTrainingByDate(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate) throws DatabaseAccessException {
        weekTrainingService.deleteWeekTrainingByDate(owner, petName, petDate);
    }

    /**
     * Get one weekTraining of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weekTraining instance
     * @return The WeekTrainingEntity with the data of weekTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{petDate}")
    public WeekTrainingEntity getWeekTrainingByDate(@PathVariable String owner, @PathVariable String petName,
                                        @PathVariable String petDate) throws DatabaseAccessException {
        return weekTrainingService.getWeekTrainingByDate(owner, petName, petDate);
    }

    /**
     * Gets all the specified weekTrainings of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the weekTrainings
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllWeekTraining(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        return weekTrainingService.getAllWeekTraining(owner, petName);
    }

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the weekTrainings in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/between/{initialDate}/{finalDate}")
    public List<Map<String, Object>> getAllWeekTrainingsBetween(@PathVariable String owner,
                                                                @PathVariable String petName,
                                                                @PathVariable String initialDate,
                                                                @PathVariable String finalDate)
        throws DatabaseAccessException {
        return weekTrainingService.getAllWeekTrainingsBetween(owner, petName, initialDate, finalDate);
    }

    /**
     * Updates one of the weekTrainings specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weekTraining instance
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                 key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{owner}/{petName}/{petDate}")
    public void updateWeekTraining(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate, @RequestBody Map<String, Object> valueMap)
        throws DatabaseAccessException {
        weekTrainingService.updateWeekTraining(owner, petName, petDate, valueMap.get("weekTrainingValue"));
    }

}
