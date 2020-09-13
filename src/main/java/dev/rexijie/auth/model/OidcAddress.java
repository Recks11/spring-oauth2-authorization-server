package dev.rexijie.auth.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Rex Ijiekhuamen
 * 09 Sep 2020
 */
@JsonRootName("address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OidcAddress {
    private String streetAddress;
    private String locality; // city
    private String region; // state
    private String postalCode;// zip/postcode
    private String country;

    @Override
    public String toString() {
        return "{" +
                "\"streetAddress\": \"" + streetAddress + '\"' +
                ", \"locality\": \"" + locality + '\"' +
                ", \"region\": \"" + region + '\"' +
                ", \"postalCode\": \"" + postalCode + '\"' +
                ", \"country\": \"" + country + '\"' +
                '}';
    }
}
