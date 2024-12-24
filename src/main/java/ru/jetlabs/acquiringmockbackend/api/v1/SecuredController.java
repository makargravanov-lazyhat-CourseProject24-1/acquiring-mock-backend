package ru.jetlabs.acquiringmockbackend.api.v1;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jetlabs.acquiringmockbackend.model.enumerations.AccountTypes;
import ru.jetlabs.acquiringmockbackend.service.JWTCookieGenerator;
import ru.jetlabs.acquiringmockbackend.service.JWTGenerator;
import ru.jetlabs.acquiringmockbackend.service.UserService;

@RestController
@RequestMapping("/acquiring-mock-backend/api/v1/secured")
public class SecuredController {

    private final UserService userService;
    private final JWTCookieGenerator generator;
    private final JWTGenerator jwtGenerator;
    private final ResponseEntity<?> OK = ResponseEntity.ok().build();
    private final ResponseEntity<?> BAD = ResponseEntity.badRequest().build();

    public SecuredController(UserService userService, JWTCookieGenerator generator, JWTGenerator jwtGenerator) {
        this.userService = userService;
        this.generator = generator;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/create/individual")
    ResponseEntity<?> createIndividualAccount(HttpServletRequest request) {
        return userService.createAccount(extractId(request), AccountTypes.INDIVIDUAL) ?
                OK(extractId(request)) : BAD;
    }

    @PostMapping("/create/corporate")
    ResponseEntity<?> createCorporateAccount(HttpServletRequest request) {
        return userService.createAccount(extractId(request), AccountTypes.CORPORATE) ?
                OK(extractId(request)) : BAD;
    }

    @GetMapping("/accounts/my")
    ResponseEntity<?> getMyAccounts(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAccounts(extractId(request)));
    }

    @PutMapping("/account={id}/cash/add{sum}/")
    ResponseEntity<?> addCash(HttpServletRequest request, @PathVariable Long id, @PathVariable Double sum) {
        return ResponseEntity.ok(userService.addCash(extractId(request), id, sum));
    }

    private ResponseEntity<?> OK(Long id) {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, generator.create(id, "jwt", jwtGenerator)).build();
    }

    private static Long extractId(HttpServletRequest request) {
        return Long
                .valueOf(String
                        .valueOf(request
                                .getAttribute("userIdFromFilter")));
    }
}
