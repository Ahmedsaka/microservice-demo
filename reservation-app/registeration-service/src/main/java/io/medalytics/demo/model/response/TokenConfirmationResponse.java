package io.medalytics.demo.model.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class TokenConfirmationResponse {

    private String token;
    @Nullable
    private LocalDateTime confirmationTime;

    public String getToken() {
        return token;
    }

    @Nullable
    public LocalDateTime getConfirmationTime() {
        return LocalDateTime.now();
    }
}
