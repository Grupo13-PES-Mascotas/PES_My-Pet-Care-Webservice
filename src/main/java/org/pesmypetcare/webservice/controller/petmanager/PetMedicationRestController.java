package org.pesmypetcare.webservice.controller.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.MedicationEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.petmanager.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medication")
public class PetMedicationRestController {
    @Autowired
    private MedicationService medicationService;
    private final String TOKEN = "token";

    /**
     * Creates a Medication eaten by a pet on the database.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date Date ofDocument the Medication
     * @param name Name ofDocument the Medication
     * @param medication The Medication entity that contains the attributes ofDocument the Medication
     */
    @PostMapping("/{owner}/{petName}/{date}/{name}")
    public void createMedication(@RequestHeader(TOKEN) String accessToken, @PathVariable String owner,
                                 @PathVariable String petName,
                                 @PathVariable String date,
                                 @PathVariable String name,
                                 @RequestBody MedicationEntity medication) {
        medicationService.createMedication(owner, petName, date, name, medication);
    }

    /**
     * Deletes all the medication with the specified owner and pet from certain date.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date Date ofDocument the Medication
     * @param name Name ofDocument the Medication
     */
    @DeleteMapping("/{owner}/{petName}/{date}/{name}")
    public void deleteByDateAndName(@RequestHeader(TOKEN) String accessToken,
                                    @PathVariable String owner,
                                    @PathVariable String petName,
                                    @PathVariable String date,
                                    @PathVariable String name) {
        medicationService.deleteByDateAndName(owner, petName, date, name);
    }

    /**
     * Deletes all the Medications ofDocument the specified pet from database.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
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
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date Date ofDocument the Medication
     * @param name Name ofDocument the Medication
     * @return The MedicationEntity identified by the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{date}/{name}")
    public MedicationEntity getMedicationData(@RequestHeader(TOKEN) String accessToken,
                                              @PathVariable String owner,
                                              @PathVariable String petName,
                                              @PathVariable String date,
                                              @PathVariable String name)
            throws DatabaseAccessException {
        return medicationService.getMedicationData(owner, petName, date, name);
    }

    /**
     * Gets the data from all the specified Medications from the database identified by its pet.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @return The List containing all the Medications from the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Map<String, Object>> getAllMedicationData(@RequestHeader(TOKEN) String accessToken,
                                                          @PathVariable String owner,
                                                          @PathVariable String petName)
            throws DatabaseAccessException {
        return medicationService.getAllMedicationData(owner, petName);
    }

    /**
     * Gets the data from all the Medications eaten by the pet between the initial and final date not including them.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
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
            throws DatabaseAccessException {
        return medicationService.getAllMedicationsBetween(owner, petName, initialDate, finalDate);
    }

    /**
     * Gets the value for the specified field ofDocument the Medication on the database.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param petName Name ofDocument the pet
     * @param date Date ofDocument the Medication
     * @param name  Name ofDocument the Medication
     * @param field Name ofDocument the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}/{date}/{name}/{field}")
    public Object getMedicationField(@RequestHeader(TOKEN) String accessToken,
                                     @PathVariable String owner,
                                     @PathVariable String petName,
                                     @PathVariable String date,
                                     @PathVariable String name,
                                     @PathVariable String field)
            throws DatabaseAccessException {
        return medicationService.getMedicationField(owner, petName, date, name, field);
    }

    /**
     * Updates the Medication's field.
     * @param accessToken oauth2 token needed to access the Database
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param petName Name ofDocument the pet
     * @param date Date ofDocument the Medication
     * @param name  Name ofDocument the Medication
     * @param field Name ofDocument the field to update
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *      *                key "value"
     */
    @PutMapping("/{owner}/{petName}/{date}/{name}/{field}")
    public void updateMedicationField(@RequestHeader(TOKEN) String accessToken,
                                      @PathVariable String owner,
                                      @PathVariable String petName,
                                      @PathVariable String date,
                                      @PathVariable String name,
                                      @PathVariable String field,
                                      @RequestBody Map<String, Object> valueMap) {
        medicationService.updateMedicationField(owner, petName, date, name, field, valueMap.get("value"));
    }
}
