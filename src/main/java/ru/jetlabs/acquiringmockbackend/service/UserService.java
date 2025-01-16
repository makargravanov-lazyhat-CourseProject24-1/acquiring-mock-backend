package ru.jetlabs.acquiringmockbackend.service;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jetlabs.acquiringmockbackend.entity.AccountEntity;
import ru.jetlabs.acquiringmockbackend.entity.TransactionEntity;
import ru.jetlabs.acquiringmockbackend.entity.UserEntity;
import ru.jetlabs.acquiringmockbackend.model.dto.*;
import ru.jetlabs.acquiringmockbackend.model.enumerations.AccountTypes;
import ru.jetlabs.acquiringmockbackend.model.enumerations.TransactionStatus;
import ru.jetlabs.acquiringmockbackend.repository.AccountRepository;
import ru.jetlabs.acquiringmockbackend.repository.TransactionRepository;
import ru.jetlabs.acquiringmockbackend.repository.UserRepository;
import ru.jetlabs.acquiringmockbackend.util.ScheduledTaskUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder encoder;
    private final TransactionRepository transactionRepository;
    private final ScheduledTaskUtil scheduledTasksUtil;

    public UserService(UserRepository userRepository, AccountRepository accountRepository, BCryptPasswordEncoder encoder, TransactionRepository transactionRepository, ScheduledTaskUtil scheduledTasksUtil) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.encoder = encoder;
        this.transactionRepository = transactionRepository;
        this.scheduledTasksUtil = scheduledTasksUtil;
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

    public boolean createAccount(Long id, AccountTypes type) {
        Optional<UserEntity> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            accountRepository.save(AccountEntity.createRandomAccount(opt.get(), type));
            return true;
        } else {
            return false;
        }
    }

    public List<AccountDto> getAccounts(Long id) {
        return accountRepository.findByOwnerId(id).stream().map(AccountEntity::toDto).toList();
    }


    public Object addCash(Long user, Long id, Double sum) {
        Optional<UserEntity> opt = userRepository.findById(user);
        if (opt.isPresent()) {
            Optional<AccountEntity> accOpt = accountRepository.findById(id);
            if (accOpt.isPresent() && accOpt.get().getOwner().getId().equals(opt.get().getId())) {
                accOpt.get().addBalance(sum);
                accountRepository.save(accOpt.get());
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public ResponseEntity<?> createPayProcessing(Double amount, String to, String callbackUrl) {
        if (amount <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Optional<AccountEntity> opt = accountRepository.findByNumber(to);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        TransactionEntity saved = transactionRepository.save(TransactionEntity
                .createTransaction(amount,
                        null,
                        opt.get(),
                        TransactionStatus.CREATED,
                        15));
        scheduledTasksUtil.addTask(saved, callbackUrl);
        return ResponseEntity.ok(saved.getUuid());
    }


    public ResponseEntity<?> checkStatusPayProcessing(String uuid) {
        Optional<TransactionEntity> opt = transactionRepository.findByUuid(uuid);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(opt.get().getTransactionStatus());
    }

    public ResponseEntity<?> getPaymentPanel(String uuid) {
        Optional<TransactionEntity> opt = transactionRepository.findByUuid(uuid);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new PaymentDto(opt.get().getTotal(), opt.get().getTransactionDate()));
    }

    @Transactional
    public ResponseEntity<?> pay(String uuid, PayParamDto payParam) {
        Optional<TransactionEntity> opt = transactionRepository.findByUuid(uuid);
        if (opt.isEmpty() || opt.get().getTransactionStatus() != TransactionStatus.CREATED) {
            System.out.println("----------------1");
            System.out.println(opt);
            return ResponseEntity.badRequest().build();
        }
        payParam = new PayParamDto(payParam.number().replace(" ", ""),
                payParam.cvv(),
                payParam.expirationYear(),
                payParam.expirationMonth());

        Optional<AccountEntity> optAcc = accountRepository.findByNumber(payParam.number());
        if (optAcc.isEmpty()) {
            System.out.println("----------------2");
            System.out.println(optAcc);
            System.out.println(payParam);
            return ResponseEntity.badRequest().build();
        }
        AccountEntity a = optAcc.get();
        if (a.getCvv().equals(payParam.cvv()) && a.isActive()) {
            LocalDateTime e = a.getExpirationDate();
            if (String.valueOf(e.getYear()).endsWith(payParam.expirationYear()) &&
                    e.getMonthValue() == Integer.parseInt(payParam.expirationMonth())) {
                System.out.println("----------------3");
                opt.get().setFromAccount(optAcc.get());
                System.out.println("----------------4");
                opt.get().setTransactionStatus(TransactionStatus.APPROVED);
                System.out.println("----------------5");
                scheduledTasksUtil.sendCallbackManually(transactionRepository.save(opt.get()));
                System.out.println("----------------6");
                return ResponseEntity.ok().build();
            }
        }
        System.out.println("----------------7");
        return ResponseEntity.badRequest().build();
    }
}
