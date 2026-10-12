
package app.service;

import app.dto.LoginRequest;
import app.dto.RegisterRequest;
import app.entity.User;
import app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email must not be empty"
            );
        }

        return userRepository.existsByEmail(
                email.trim().toLowerCase()
        );
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Registration details are required"
            );
        }

        String fullName = request.getFullName() == null
                ? "" : request.getFullName().trim();

        String email = request.getEmail() == null
                ? "" : request.getEmail().trim().toLowerCase();

        String password = request.getPassword();

        if (fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Full name is required"
            );
        }

        if (email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (password == null || password.length() < 8
                || password.length() > 72) {
            throw new IllegalArgumentException(
                    "Password must contain 8 to 72 characters"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);
    }

    public boolean login(LoginRequest request) {
        if (request == null
                || request.getEmail() == null
                || request.getEmail().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Email and password are required"
            );
        }

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid email or password"
                ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        return true;
    }
}
