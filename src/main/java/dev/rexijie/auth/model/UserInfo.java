package dev.rexijie.auth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"name","firstname", "lastname", "fullname", "email", "dob"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo extends Entity {
    @JsonProperty("given_name")
    private String firstName;
    @JsonProperty("family_name")
    private String lastName;
    @JsonProperty("preferred_username")
    private String username;
    private String email;
    private boolean emailVerified;
    private OidcAddress address;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("phone_number_verified")
    private boolean phoneNumberVerified;
    @JsonProperty("birthdate")
    private LocalDate dateOfBirth;

    @JsonProperty("name")
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        return sb.append(firstName)
                .append(" ")
                .append(lastName)
                .toString();
    }
}
