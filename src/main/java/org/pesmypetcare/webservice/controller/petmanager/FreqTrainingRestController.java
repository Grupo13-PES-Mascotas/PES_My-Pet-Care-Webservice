package org.pesmypetcare.webservice.controller.petmanager;


import org.pesmypetcare.webservice.entity.petmanager.FreqTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.petmanager.FreqTrainingService;
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
@RequestMapping("/freqTraining")
public class FreqTrainingRestController {
    @Autowired
    private FreqTrainingService freqTrainingService;

    /**
     * Creates a freqTraining ofDocument the pet on the database.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Creation date ofDocument the instance
     * @param freqTrainingEntity The parameters ofDocument freqTraining
     */
    @PostMapping("/{owner}/{petName}/{petDate}")
    public void createFreqTraining(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate, @RequestBody FreqTrainingEntity freqTrainingEntity) {
        freqTrainingService.createFreqTraining(owner, petName, petDate, freqTrainingEntity);
    }

    /**
     * Deletes all freqTrainings ofDocument the pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteAllFreqTrainings(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        freqTrainingService.deleteAllFreqTrainings(owner, petName);
    }

    /**
     * Deletes the freqTraining with the specified pet and date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the freqTraining instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}/{petDate}")
    public void deleteFreqTrainingByDate(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate) throws DatabaseAccessException {
        freqTrainingService.deleteFreqTrainingByDate(owner, petName, petDate);
    }

    /**
     * Get one freqTraining ofDocument the pet with the specified date.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the freqTraining instance
     * @return The FreqTrainingEntity with the data ofDocument freqTraining
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{petDate}")
    public FreqTrainingEntity getFreqTrainingByDate(@PathVariable String owner, @PathVariable String petName,
                                        @PathVariable String petDate) throws DatabaseAccessException {
        return freqTrainingService.getFreqTrainingByDate(owner, petName, petDate);
    }

    /**
     * Gets all the specified freqTrainings ofDocument one pet.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @return The list with all ofDocument the freqTrainings
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllFreqTraining(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        return freqTrainingService.getAllFreqTraining(owner, petName);
    }

    /**
     * Gets the data from all the freqtrainings by the pet between the initial and final date not including them.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the freqTrainings in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/between/{initialDate}/{finalDate}")
    public List<Map<String, Object>> getAllFreqTrainingsBetween(@PathVariable String owner,
                                                                @PathVariable String petName,
                                                                @PathVariable String initialDate,
                                                                @PathVariable String finalDate)
        throws DatabaseAccessException {
        return freqTrainingService.getAllFreqTrainingsBetween(owner, petName, initialDate, finalDate);
    }

    /**
     * Updates one ofDocument the freqTrainings specified with the date and the pet name.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param petDate Date ofDocument the freqTraining instance
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                 key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{owner}/{petName}/{petDate}")
    public void updateFreqTraining(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate, @RequestBody Map<String, Object> valueMap)
        throws DatabaseAccessException {
        freqTrainingService.updateFreqTraining(owner, petName, petDate, valueMap.get("freqTrainingValue"));
    }

}
