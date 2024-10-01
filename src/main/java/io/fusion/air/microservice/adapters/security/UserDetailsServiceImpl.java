package io.fusion.air.microservice.adapters.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Returns Spring UserDetails Object.
     * This service doesnt do any database look and it returns the UserDetails
     * object with the same user name.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User(username, "", new ArrayList<>());
    }

}
