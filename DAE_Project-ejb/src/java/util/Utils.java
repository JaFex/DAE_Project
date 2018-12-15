package util;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class Utils {
    public static String getConstraintViolationMessages(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> cvs = e.getConstraintViolations();
        StringBuilder errorMessages = new StringBuilder();
        
        cvs.forEach(cv -> {
            errorMessages.append(cv.getMessage());
            errorMessages.append("; ");
        });
        
        return errorMessages.toString();
    }
}
