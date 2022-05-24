package wooteco.subway.ui;

import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.exception.IllegalSectionException;
import wooteco.subway.exception.NotExistException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(IllegalSectionException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(NotExistException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(DuplicateKeyException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("이미 존재합니다."));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> requestBodyInvalidError(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(getMessage(e)));
    }

    private String getMessage(MethodArgumentNotValidException e) {
        return e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" "));
    }
}
