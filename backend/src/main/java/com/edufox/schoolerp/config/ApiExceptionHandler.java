package com.edufox.schoolerp.config;
import com.edufox.schoolerp.auth.AuthDtos;import org.springframework.http.ResponseEntity;import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.annotation.*;import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@RestControllerAdvice public class ApiExceptionHandler{
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<AuthDtos.ErrorResponse> status(ResponseStatusException e){return ResponseEntity.status(e.getStatusCode()).body(new AuthDtos.ErrorResponse(e.getStatusCode().toString(),e.getReason()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<AuthDtos.ErrorResponse> validation(MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getDefaultMessage()).orElse("Invalid request");return ResponseEntity.badRequest().body(new AuthDtos.ErrorResponse("VALIDATION_ERROR",m));}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<AuthDtos.ErrorResponse> badArgument(IllegalArgumentException e){return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthDtos.ErrorResponse("BAD_REQUEST",e.getMessage()));}
}