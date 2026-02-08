package com.doodle.scheduler.application.adapter.in.rest.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Pagination",
        description = "Pagination parameters for search results"
)
public class PaginationDto {

    @JsonProperty("page")
    @Min(value = 0, message = "page must be greater than or equal to 0")
    @Schema(
            description = "Page number (zero-based)",
            example = "0",
            defaultValue = "0"
    )
    private Integer page = 0;

    @JsonProperty("size")
    @Min(value = 1, message = "size must be greater than 0")
    @Schema(
            description = "Number of items per page",
            example = "10",
            defaultValue = "10"
    )
    private Integer size = 10;
}
