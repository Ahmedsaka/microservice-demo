package io.medalytics.demo.service;

import io.medalytics.demo.exception.DuplicateException;
import io.medalytics.demo.model.AppUser;
import io.medalytics.demo.model.ConfirmationToken;
import io.medalytics.demo.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByEmail(s)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", s)));
    }

    public String signUp(AppUser appUser) {
        if (userRepository.existsByEmail(appUser.getEmail())){
            throw new DuplicateException(
                    String.format("User %s already exist", appUser.getEmail())
            );
        }
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        userRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setAppUser(appUser);
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        confirmationToken.setToken(token);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

//        TODO: Send email to user

        return token;
    }
}
