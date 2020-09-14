package dev.rexijie.auth.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public abstract class Identified {
    @Id
    private String id;
}
