package net.pladema.snowstorm.services.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.pladema.sgx.restclient.services.domain.LoginResponse;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SnowstormLoginResponse implements LoginResponse {

    private String token;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public SnowstormLoginResponse(@JsonProperty("token") String token) {
        this.token = token;
    }
}
