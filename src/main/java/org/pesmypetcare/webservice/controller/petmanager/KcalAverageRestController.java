package org.pesmypetcare.webservice.controller.petmanager;


import org.pesmypetcare.webservice.entity.KcalAverageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.KcalAverageService;
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
@RequestMapping("/kcalAverage")
public class KcalAverageRestController {
    @Autowired
    private KcalAverageService kcalAverageService;

    /**
     * Creates a kcalAverage of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Creation date of the instance
     * @param kcalAverageEntity The parameters of kcalAverage
     */
    @PostMapping("/{owner}/{petName}/{petDate}")
    public void createKcalAverage(@PathVariable String owner, @PathVariable String petName,
                                  @PathVariable String petDate, @RequestBody KcalAverageEntity kcalAverageEntity) {
        kcalAverageService.createKcalAverage(owner, petName, petDate, kcalAverageEntity);
    }

    /**
     * Deletes all kcalAverages of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteAllKcalAverages(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        kcalAverageService.deleteAllKcalAverages(owner, petName);
    }

    /**
     * Deletes the kcalAverage with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the kcalAverage instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}/{petDate}")
    public void deleteKcalAverageByDate(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate) throws DatabaseAccessException {
        kcalAverageService.deleteKcalAverageByDate(owner, petName, petDate);
    }

    /**
     * Get one kcalAverage of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the kcalAverage instance
     * @return The KcalAverageEntity with the data of kcalAverage
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{petDate}")
    public KcalAverageEntity getKcalAverageByDate(@PathVariable String owner, @PathVariable String petName,
                                        @PathVariable String petDate) throws DatabaseAccessException {
        return kcalAverageService.getKcalAverageByDate(owner, petName, petDate);
    }

    /**
     * Gets all the specified kcalAverages of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the kcalAverages
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllKcalAverage(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        return kcalAverageService.getAllKcalAverage(owner, petName);
    }

    /**
     * Gets the data from all the kcalaverage by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the kcalAverages in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/between/{initialDate}/{finalDate}")
    public List<Map<String, Object>> getAllKcalAveragesBetween(@PathVariable String owner, @PathVariable String petName,
                                                          @PathVariable String initialDate,
                                                          @PathVariable String finalDate)
        throws DatabaseAccessException {
        return kcalAverageService.getAllKcalAveragesBetween(owner, petName, initialDate, finalDate);
    }

    /**
     * Updates one of the kcalAverages specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the kcalAverage instance
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                 key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{owner}/{petName}/{petDate}")
    public void updateKcalAverage(@PathVariable String owner, @PathVariable String petName,
                                  @PathVariable String petDate, @RequestBody Map<String, Object> valueMap)
        throws DatabaseAccessException {
        kcalAverageService.updateKcalAverage(owner, petName, petDate, valueMap.get("kcalAverageValue"));
    }

}
