package com.benoly.auth.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
abstract class Identified implements Serializable {
    @Id
    private String id;
}
