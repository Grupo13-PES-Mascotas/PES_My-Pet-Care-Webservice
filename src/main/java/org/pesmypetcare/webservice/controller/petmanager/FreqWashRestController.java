package org.pesmypetcare.webservice.controller.petmanager;


import org.pesmypetcare.webservice.entity.FreqWashEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.FreqWashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/freqWash")
public class FreqWashRestController {
    @Autowired
    private FreqWashService freqWashService;

    /**
     * Creates a freqWash of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param freqWashEntity The parameters of freqWash
     */
    @PostMapping("/{owner}/{petName}/{petDate}")
    public void createFreqWash(@PathVariable String owner, @PathVariable String petName, @PathVariable String petDate,
                             @RequestBody FreqWashEntity freqWashEntity) {
        freqWashService.createFreqWash(owner, petName, petDate, freqWashEntity);
    }

    /**
     * Deletes all freqWashs of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteAllFreqWashs(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        freqWashService.deleteAllFreqWashs(owner, petName);
    }

    /**
     * Deletes the freqWash with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the freqWash instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}/{petDate}")
    public void deleteFreqWashByDate(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate) throws DatabaseAccessException {
        freqWashService.deleteFreqWashByDate(owner, petName, petDate);
    }

    /**
     * Get one freqWash of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the freqWash instance
     * @return The FreqWashEntity with the data of freqWash
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{petDate}")
    public FreqWashEntity getFreqWashByDate(@PathVariable String owner, @PathVariable String petName,
                                        @PathVariable String petDate) throws DatabaseAccessException {
        return freqWashService.getFreqWashByDate(owner, petName, petDate);
    }

    /**
     * Gets all the specified freqWashs of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the freqWashs
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllFreqWash(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        return freqWashService.getAllFreqWash(owner, petName);
    }

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the freqWashs in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/between/{initialDate}/{finalDate}")
    public List<Map<String, Object>> getAllFreqWashsBetween(@PathVariable String owner, @PathVariable String petName,
                                                          @PathVariable String initialDate,
                                                          @PathVariable String finalDate)
        throws DatabaseAccessException {
        return freqWashService.getAllFreqWashsBetween(owner, petName, initialDate, finalDate);
    }

    /**
     * Updates one of the freqWashs specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the freqWash instance
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                 key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{owner}/{petName}/{petDate}")
    public void updateFreqWash(@PathVariable String owner, @PathVariable String petName, @PathVariable String petDate,
                             @RequestBody Map<String, Object> valueMap) throws DatabaseAccessException {
        freqWashService.updateFreqWash(owner, petName, petDate, valueMap.get("freqWashValue"));
    }

}
