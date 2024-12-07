package ru.jetlabs.acquiringmockbackend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.jetlabs.acquiringmockbackend.entity.UserEntity;
import ru.jetlabs.acquiringmockbackend.model.dto.RegisterUserDto;
import ru.jetlabs.acquiringmockbackend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    private boolean register(RegisterUserDto dto){
        try {
            userRepository.save(new UserEntity(dto.email(), dto.name(), encoder.encode(dto.password())));
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
