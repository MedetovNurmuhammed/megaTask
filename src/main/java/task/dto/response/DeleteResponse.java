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
public class DeleteResponse {
    private String message;
    private Long deletedId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public static DeleteResponse success(Long id) {
        return DeleteResponse.builder()
                .message("Задача успешно удалена")
                .deletedId(id)
                .timestamp(LocalDateTime.now())
                .build();
    }
}