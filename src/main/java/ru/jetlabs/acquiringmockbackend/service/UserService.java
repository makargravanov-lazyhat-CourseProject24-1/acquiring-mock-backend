package ru.jetlabs.acquiringmockbackend.service;

import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.jetlabs.acquiringmockbackend.entity.UserEntity;
import ru.jetlabs.acquiringmockbackend.model.dto.LoginUserDto;
import ru.jetlabs.acquiringmockbackend.model.dto.RegisterUserDto;
import ru.jetlabs.acquiringmockbackend.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public boolean register(RegisterUserDto dto) {
        try {
            userRepository.save(new UserEntity(dto.email(), dto.name(), encoder.encode(dto.password())));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Pair<Boolean, Long> login(LoginUserDto dto) {
        Optional<UserEntity> opt = userRepository.findByEmail(dto.email());
        return opt.map(userEntity ->
                Pair.of(encoder.matches(dto.password(), userEntity.getPassword()), userEntity.getId()))
                .orElseGet(() -> Pair.of(false, -1L));
    }
}
