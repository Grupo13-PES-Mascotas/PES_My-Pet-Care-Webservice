package org.pesmypetcare.webservice.controller.petmanager;


import org.pesmypetcare.webservice.service.WeightService;
import org.pesmypetcare.webservice.entity.WeightEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
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
@RequestMapping("/weight")
public class WeightRestController {
    @Autowired
    private WeightService weightService;

    /**
     * Creates a weight of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param weightEntity The parameters of weight
     */
    @PostMapping("/{owner}/{petName}/{petDate}")
    public void createWeight(@PathVariable String owner, @PathVariable String petName, @PathVariable String petDate,
                             @RequestBody WeightEntity weightEntity) {
        weightService.createWeight(owner, petName, petDate, weightEntity);
    }
    /**
     * Deletes all weights of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteAllWeights(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        weightService.deleteAllWeights(owner, petName);
    }
    /**
     * Deletes the weight with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weight instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}/{petDate}")
    public void deleteWeightByDate(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate)  throws DatabaseAccessException {
        weightService.deleteWeightByDate(owner, petName, petDate);
    }
    /**
     * Get one weight of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weight instance
     * @return The WeightEntity with the data of weight
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{petDate}")
    public WeightEntity getWeightByDate(@PathVariable String owner, @PathVariable String petName,
                                        @PathVariable String petDate) throws DatabaseAccessException {
        return weightService.getWeightByDate(owner, petName, petDate);
    }
    /**
     * Gets all the specified weights of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the weights
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllWeight(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        return weightService.getAllWeight(owner, petName);
    }
    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the weights in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/between/{initialDate}/{finalDate}")
    public List<Map<String, Object>> getAllWeightsBetween(@PathVariable String owner, @PathVariable String petName,
                                                          @PathVariable String initialDate,
                                                          @PathVariable String finalDate)
        throws DatabaseAccessException {
        return weightService.getAllWeightsBetween(owner, petName, initialDate, finalDate);
    }
    /**
     * Updates one of the weights specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the weight instance
     * @param value Value of the new weight
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{owner}/{petName}/{petDate}")
    public void updateWeight(@PathVariable String owner, @PathVariable String petName, @PathVariable String petDate,
                             @RequestBody double value) throws DatabaseAccessException {
        weightService.updateWeight(owner, petName, petDate, value);
    }}
