package com.mentorship.hanakoleh.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * A class that represents the authenticated user's principal and exposes
 * the customer ID for use in controllers via @AuthenticationPrincipal.
 */
@Getter
public class UserPrincipal extends User {

    private final Integer id;

    public UserPrincipal(Integer id,
                         String username,
                         String password,
                         Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public UserPrincipal(Integer id,
                         String username,
                         String password,
                         boolean enabled,
                         boolean accountNonExpired,
                         boolean credentialsNonExpired,
                         boolean accountNonLocked,
                         Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }
}
