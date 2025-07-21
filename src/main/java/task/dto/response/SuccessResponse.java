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
public class SuccessResponse {
    private String message;
    private Object data;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public static SuccessResponse created(String message, Object data) {
        return SuccessResponse.builder()
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static SuccessResponse updated(String message, Object data) {
        return SuccessResponse.builder()
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}