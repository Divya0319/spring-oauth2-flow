package com.dailycodebuffer.oauthserver.service;

import com.dailycodebuffer.oauthserver.entity.AppUser;
import com.dailycodebuffer.oauthserver.entity.Status;
import com.dailycodebuffer.oauthserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username);
        if(user == null) {
            throw  new UsernameNotFoundException("No User Found");
        }

        boolean userEnabled = user.getStatus() == Status.ACTIVE;
        return new User(
                user.getEmail(),
                user.getPassword(),
                userEnabled,
                true,
                true,
                true,
                getAuthorities(List.of(user.getRole()))
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority>  authorities = new ArrayList<>();
        for(String role: roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
