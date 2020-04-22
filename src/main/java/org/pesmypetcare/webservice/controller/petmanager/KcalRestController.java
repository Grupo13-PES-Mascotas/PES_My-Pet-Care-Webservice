package org.pesmypetcare.webservice.controller.petmanager;


import org.pesmypetcare.webservice.entity.KcalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.KcalService;
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
@RequestMapping("/kcal")
public class KcalRestController {
    @Autowired
    private KcalService kcalService;

    /**
     * Creates a kcal of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param kcalEntity The parameters of kcal
     */
    @PostMapping("/{owner}/{petName}/{petDate}")
    public void createKcal(@PathVariable String owner, @PathVariable String petName, @PathVariable String petDate,
                             @RequestBody KcalEntity kcalEntity) {
        kcalService.createKcal(owner, petName, petDate, kcalEntity);
    }

    /**
     * Deletes all kcals of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteAllKcals(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        kcalService.deleteAllKcals(owner, petName);
    }

    /**
     * Deletes the kcal with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the kcal instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}/{petDate}")
    public void deleteKcalByDate(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate) throws DatabaseAccessException {
        kcalService.deleteKcalByDate(owner, petName, petDate);
    }

    /**
     * Get one kcal of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the kcal instance
     * @return The KcalEntity with the data of kcal
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{petDate}")
    public KcalEntity getKcalByDate(@PathVariable String owner, @PathVariable String petName,
                                        @PathVariable String petDate) throws DatabaseAccessException {
        return kcalService.getKcalByDate(owner, petName, petDate);
    }

    /**
     * Gets all the specified kcals of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the kcals
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllKcal(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        return kcalService.getAllKcal(owner, petName);
    }

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the kcals in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/between/{initialDate}/{finalDate}")
    public List<Map<String, Object>> getAllKcalsBetween(@PathVariable String owner, @PathVariable String petName,
                                                          @PathVariable String initialDate,
                                                          @PathVariable String finalDate)
        throws DatabaseAccessException {
        return kcalService.getAllKcalsBetween(owner, petName, initialDate, finalDate);
    }

    /**
     * Updates one of the kcals specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the kcal instance
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                 key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{owner}/{petName}/{petDate}")
    public void updateKcal(@PathVariable String owner, @PathVariable String petName, @PathVariable String petDate,
                             @RequestBody Map<String, Object> valueMap) throws DatabaseAccessException {
        kcalService.updateKcal(owner, petName, petDate, valueMap.get("kcalValue"));
    }

}
