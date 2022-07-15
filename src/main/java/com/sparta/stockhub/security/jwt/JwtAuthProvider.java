package com.sparta.stockhub.security.jwt;

import com.sparta.stockhub.domain.User;
import com.sparta.stockhub.repository.UserRepository;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthProvider implements AuthenticationProvider {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = (String) authentication.getPrincipal();
        String username = jwtDecoder.decodeUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("No user"));

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return JwtPreProcessingToken.class.isAssignableFrom(authentication);
    }
}