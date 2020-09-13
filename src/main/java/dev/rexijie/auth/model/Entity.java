package dev.rexijie.auth.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class Entity extends Identified {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
