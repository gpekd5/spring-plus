package org.example.expert.domain.comment.dto.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
    private final String status;
    private final int code;
    private final String message;

    private ErrorResponse(HttpStatus status, String message) {
        this.status = status.name();
        this.code = status.value();
        this.message = message;
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status, message);
    }
}
