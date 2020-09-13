package dev.rexijie.auth.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
abstract class Identified {
    @Id
    private String id;
}
