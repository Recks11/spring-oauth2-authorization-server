package com.benoly.auth.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"firstname", "lastname", "fullname", "email", "dob"})
public class UserProfile extends Entity {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("dob")
    private String dataOfBirth;


    @JsonProperty("name")
    private String getFullName() {
        StringBuilder sb = new StringBuilder();
        return sb.append(firstName)
                .append(" ")
                .append(lastName)
                .toString();
    }
}
