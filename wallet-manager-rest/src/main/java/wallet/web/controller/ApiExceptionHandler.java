package wallet.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wallet.web.error.ApiErrorResponse;
import wallet.web.error.GenericServerException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(GenericServerException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
    		GenericServerException ex) {
        ApiErrorResponse response = 
            new ApiErrorResponse("error-0001",
                ex.getMessage() + ex.getId());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
