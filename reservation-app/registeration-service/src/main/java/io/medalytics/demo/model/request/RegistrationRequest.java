package io.medalytics.demo.model.request;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class RegistrationRequest {
    @NotNull(message = "Field cannot be left empty !")
    private final String firstName;
    @NotNull(message = "Field cannot be left empty !")
    private final String lastName;
    @Nullable
    private final String username;
    @Email(message = "This must be a valid email")
    private final String email;
    private final String password;
}
