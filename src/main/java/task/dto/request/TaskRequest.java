package task.dto.request;

import lombok.Builder;

@Builder
public record TaskRequest(String title, String description) {
}
