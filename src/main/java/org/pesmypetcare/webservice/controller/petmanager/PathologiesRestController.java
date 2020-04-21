package org.pesmypetcare.webservice.controller.petmanager;


import org.pesmypetcare.webservice.entity.PathologiesEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.PathologiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pathologies")
public class PathologiesRestController {
    @Autowired
    private PathologiesService pathologiesService;

    /**
     * Creates a pathologies of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param pathologiesEntity The parameters of pathologies
     */
    @PostMapping("/{owner}/{petName}/{petDate}")
    public void createPathologies(@PathVariable String owner, @PathVariable String petName, @PathVariable String petDate,
                             @RequestBody PathologiesEntity pathologiesEntity) {
        pathologiesService.createPathologies(owner, petName, petDate, pathologiesEntity);
    }

    /**
     * Deletes all pathologiess of the pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteAllPathologiess(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        pathologiesService.deleteAllPathologiess(owner, petName);
    }

    /**
     * Deletes the pathologies with the specified pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the pathologies instance
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}/{petDate}")
    public void deletePathologiesByDate(@PathVariable String owner, @PathVariable String petName,
                                   @PathVariable String petDate) throws DatabaseAccessException {
        pathologiesService.deletePathologiesByDate(owner, petName, petDate);
    }

    /**
     * Get one pathologies of the pet with the specified date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the pathologies instance
     * @return The PathologiesEntity with the data of pathologies
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{petDate}")
    public PathologiesEntity getPathologiesByDate(@PathVariable String owner, @PathVariable String petName,
                                        @PathVariable String petDate) throws DatabaseAccessException {
        return pathologiesService.getPathologiesByDate(owner, petName, petDate);
    }

    /**
     * Gets all the specified pathologiess of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return The list with all of the pathologiess
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllPathologies(@PathVariable String owner, @PathVariable String petName)
        throws DatabaseAccessException {
        return pathologiesService.getAllPathologies(owner, petName);
    }

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the pathologiess in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/between/{initialDate}/{finalDate}")
    public List<Map<String, Object>> getAllPathologiessBetween(@PathVariable String owner, @PathVariable String petName,
                                                          @PathVariable String initialDate,
                                                          @PathVariable String finalDate)
        throws DatabaseAccessException {
        return pathologiesService.getAllPathologiessBetween(owner, petName, initialDate, finalDate);
    }

    /**
     * Updates one of the pathologiess specified with the date and the pet name.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param petDate Date of the pathologies instance
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                 key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{owner}/{petName}/{petDate}")
    public void updatePathologies(@PathVariable String owner, @PathVariable String petName, @PathVariable String petDate,
                             @RequestBody Map<String, Object> valueMap) throws DatabaseAccessException {
        pathologiesService.updatePathologies(owner, petName, petDate, valueMap.get("pathologiesValue"));
    }

}
