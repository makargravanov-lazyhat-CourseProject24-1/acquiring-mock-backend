package ru.jetlabs.acquiringmockbackend.api.v1;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jetlabs.acquiringmockbackend.model.dto.LoginUserDto;
import ru.jetlabs.acquiringmockbackend.model.dto.RegisterUserDto;
import ru.jetlabs.acquiringmockbackend.service.JWTCookieGenerator;
import ru.jetlabs.acquiringmockbackend.service.JWTGenerator;
import ru.jetlabs.acquiringmockbackend.service.UserService;

@RestController
@RequestMapping("/acquiring-mock/api/v1")
public class UserController {
    private final UserService userService;
    private final JWTCookieGenerator generator;
    private final JWTGenerator jwtGenerator;
    private final ResponseEntity<?> OK = ResponseEntity.ok().build();
    private final ResponseEntity<?> BAD = ResponseEntity.badRequest().build();

    public UserController(UserService userService, JWTCookieGenerator generator, JWTGenerator jwtGenerator) {
        this.userService = userService;
        this.generator = generator;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterUserDto dto) {
        return userService.register(dto) ? OK : BAD;
    }

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginUserDto dto) {
        Pair<Boolean, Long> p = userService.login(dto);
        return p.getFirst() ? OK(p.getSecond()): BAD;
    }

    private ResponseEntity<?> OK(Long id){
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, generator.create(id,"jwt", jwtGenerator)).build();
    }
}
