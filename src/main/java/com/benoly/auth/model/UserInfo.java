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
public class UserInfo extends Entity {
    @JsonProperty("given_name")
    private String firstName;

    @JsonProperty("family_name")
    private String lastName;

    @JsonProperty("preferred_username")
    private String username;

    private String email;
//    private boolean emailVerified;

    @JsonProperty("phone_number")
    private String phoneNumber;
//    private boolean phonenumberVerified;

    @JsonProperty("birthdate")
    private String dataOfBirth;


    @JsonProperty("name")
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        return sb.append(firstName)
                .append(" ")
                .append(lastName)
                .toString();
    }
}
