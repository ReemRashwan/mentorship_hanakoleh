package com.mentorship.hanakoleh.security;

import com.mentorship.hanakoleh.domain.user.model.User;
import com.mentorship.hanakoleh.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                java.util.Collections.emptyList()
        );
    }
}
