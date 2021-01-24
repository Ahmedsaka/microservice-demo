package io.medalytics.demo.service;

import io.medalytics.demo.model.ConfirmationToken;
import io.medalytics.demo.repository.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository tokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        tokenRepository.save(token);
    }

    public Optional<ConfirmationToken> retrieveToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void setConfirmed(String token, LocalDateTime confirmationTime) {
        tokenRepository.setConfirmed(token, confirmationTime);
    }
}
