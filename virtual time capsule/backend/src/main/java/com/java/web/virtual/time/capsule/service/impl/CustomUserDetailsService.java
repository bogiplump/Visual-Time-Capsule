package com.java.web.virtual.time.capsule.service.impl; // Adjust package to match your project structure

import com.java.web.virtual.time.capsule.model.UserModel; // Assuming your User entity is in this package
import com.java.web.virtual.time.capsule.repository.UserRepository; // Assuming you have a UserRepository
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // For simple empty authorities list

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may be case-sensitive, or case-insensitive depending on how the
     * implementation instance is configured.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (Spring Security's UserDetails interface)
     * @throws UsernameNotFoundException if the user could not be found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user in your database
        UserModel user = userRepository.findByUsername(username);
        log.info("User: " + user.toString());

        // Build and return Spring Security's UserDetails object
        // You can add roles/authorities here if your User model has them
        return new User(
            user.getUsername(),
            user.getPassword(), // CORRECTED: Use getPassword() to match your User entity
            new ArrayList<>() // Empty list for authorities (roles), add actual roles if your app uses them
        );
    }
}
