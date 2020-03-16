package org.pesmypetcare.webservice.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
class ApiValidationError extends ApiSubError {
    private final String object;
    private final String field;
    private final Object rejectedValue;
    private final String message;
}
