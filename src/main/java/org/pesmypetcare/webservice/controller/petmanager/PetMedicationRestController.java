package org.pesmypetcare.webservice.controller.petmanager;

import org.pesmypetcare.webservice.entity.MedicationEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/medication")
public class PetMedicationRestController {
    @Autowired
    private MedicationService medicationService;
    private final String TOKEN = "token";

    /**
     * Creates a Medication eaten by a pet on the database.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param dateName Date + name of the Medication
     * @param medication The Medication entity that contains the attributes of the Medication
     */
    @PostMapping("/{owner}/{petName}/{dateName}")
    public void createMedication(@RequestHeader(TOKEN) String accessToken, @PathVariable String owner,
                                 @PathVariable String petName,
                                 @PathVariable String dateName,
                                 @RequestBody MedicationEntity medication) {
        medicationService.createMedication(owner, petName, dateName, medication);
    }

    /**
     * Deletes all the medication with the specified owner and pet from certain date.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param dateName date + name of the receipt of the medication
     */
    @DeleteMapping("/{owner}/{petName}/{dateName}")
    public void deleteByDateAndName(@RequestHeader(TOKEN) String accessToken,
                                    @PathVariable String owner,
                                    @PathVariable String petName,
                                    @PathVariable  String dateName) {
        medicationService.deleteByDateAndName(owner, petName, dateName);
    }

    /**
     * Deletes all the Medications of the specified pet from database.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteAllMedications(@RequestHeader(TOKEN) String accessToken,
                                     @PathVariable String owner,
                                     @PathVariable String petName)
            throws DatabaseAccessException {
        medicationService.deleteAllMedications(owner, petName);
    }

    /**
     * Gets a Medication identified by its pet and date.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param dateName Date of the Medication
     * @return The MedicationEntity identified by the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{dateName}")
    public MedicationEntity getMedicationData(@RequestHeader(TOKEN) String accessToken,
                                              @PathVariable String owner,
                                              @PathVariable String petName,
                                              @PathVariable String dateName)
            throws DatabaseAccessException {
        return medicationService.getMedicationData(owner, petName, dateName);
    }

    /**
     * Gets the data from all the specified Medications from the database identified by its pet.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @return The List containing all the Medications from the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllMedicationData(@RequestHeader(TOKEN) String accessToken,
                                                          @PathVariable String owner,
                                                          @PathVariable String petName)
            throws DatabaseAccessException, ExecutionException, InterruptedException {
        return medicationService.getAllMedicationData(owner, petName);
    }

    /**
     * Gets the data from all the Medications eaten by the pet between the initial and final date not including them.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the Medications eaten by the pet in the specified time
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/between/{initialDate}/{finalDate}")
    public List<Map<String, Object>> getAllMedicationsBetween(@RequestHeader(TOKEN) String accessToken,
                                                                    @PathVariable String owner,
                                                                    @PathVariable String petName,
                                                                    @PathVariable String initialDate,
                                                                    @PathVariable String finalDate)
            throws DatabaseAccessException, ExecutionException, InterruptedException {
        return medicationService.getAllMedicationsBetween(owner, petName, initialDate, finalDate);
    }

    /**
     * Gets the value for the specified field of the Medication on the database.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param dateName date + name of the Medication
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{dateName}/{field}")
    public Object getMedicationField(@RequestHeader(TOKEN) String accessToken,
                                     @PathVariable String owner,
                                     @PathVariable String petName,
                                     @PathVariable String dateName,
                                     @PathVariable String field)
            throws DatabaseAccessException {
        return medicationService.getMedicationField(owner, petName, dateName, field);
    }

    /**
     * Updates the Medication's field.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param dateName Date + name of the Medication
     * @param field Name of the field to update
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *      *                key "value"
     */
    @PutMapping("/{owner}/{petName}/{dateName}/{field}")
    public void updateMedicationField(@RequestHeader(TOKEN) String accessToken,
                                      @PathVariable String owner,
                                      @PathVariable String petName,
                                      @PathVariable String dateName,
                                      @PathVariable String field,
                                      @RequestBody Map<String, Object> valueMap) {
        medicationService.updateMedicationField(owner, petName, dateName, field, valueMap.get("value"));
    }
}
