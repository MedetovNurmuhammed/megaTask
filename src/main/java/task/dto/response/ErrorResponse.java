package task.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public static ErrorResponse notFound(String message, String path) {
        return ErrorResponse.builder()
                .status(404)
                .error("Not Found")
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse badRequest(String message, String path) {
        return ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}