package ru.jetlabs.acquiringmockbackend.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jetlabs.acquiringmockbackend.model.dto.RegisterUserDto;

@RestController
@RequestMapping("/acquiring-mock/api/v1")
public class UserController {

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterUserDto dto){

    }
}
