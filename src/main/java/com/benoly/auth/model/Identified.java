package com.benoly.auth.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
abstract class Identified {
    @Id
    private String id;
}
