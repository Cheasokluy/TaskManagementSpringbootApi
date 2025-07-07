package com.taskManagement.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TeamSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private Integer memberCount;

}
