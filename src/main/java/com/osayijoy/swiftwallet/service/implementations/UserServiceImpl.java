package com.osayijoy.swiftwallet.service.implementations;

import com.osayijoy.swiftwallet.constants.KycLevel;
import com.osayijoy.swiftwallet.constants.RoleEnum;
import com.osayijoy.swiftwallet.dtos.request.EmailDto;
import com.osayijoy.swiftwallet.dtos.request.UpdateUserRequestDto;
import com.osayijoy.swiftwallet.dtos.request.UserRegistrationRequestDto;
import com.osayijoy.swiftwallet.dtos.response.RegistrationResponseDto;
import com.osayijoy.swiftwallet.entity.Role;
import com.osayijoy.swiftwallet.entity.User;
import com.osayijoy.swiftwallet.entity.VerificationToken;
import com.osayijoy.swiftwallet.entity.Wallet;
import com.osayijoy.swiftwallet.exceptions.CustomException;
import com.osayijoy.swiftwallet.exceptions.ResourceCreationException;
import com.osayijoy.swiftwallet.exceptions.ResourceNotFoundException;
import com.osayijoy.swiftwallet.repository.RoleRepository;
import com.osayijoy.swiftwallet.repository.UserRepository;
import com.osayijoy.swiftwallet.repository.VerificationTokenRepository;
import com.osayijoy.swiftwallet.repository.WalletRepository;
import com.osayijoy.swiftwallet.service.SendMailService;
import com.osayijoy.swiftwallet.service.UserService;
import com.osayijoy.swiftwallet.utils.AccountNumberUtil;
import com.osayijoy.swiftwallet.utils.AppUtil;
import com.osayijoy.swiftwallet.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final SendMailService sendMailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final WalletRepository walletRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReentrantLock lock = new ReentrantLock(true);


    @Override
    @Transactional
    public RegistrationResponseDto registerUser(UserRegistrationRequestDto registrationRequestDto) {
        lock.lock();
        log.info("register user and create account");
        try {
            if (doesUserAlreadyExist(registrationRequestDto.getEmail())) {
                throw new ResourceCreationException("User already exist");
            }
            User newUser = saveNewUser(registrationRequestDto);
            Wallet newWallet = createNewWalletAccount(newUser);
            sendRegistrationConfirmationEmail(newUser, registrationRequestDto.getEmail());
            return buildRegistrationResponse(newWallet);
        } finally {
            lock.unlock();
        }
    }

    private RegistrationResponseDto buildRegistrationResponse(Wallet newWallet) {
        return new RegistrationResponseDto(newWallet.getAccountNumber());
    }

    private User saveNewUser(UserRegistrationRequestDto registrationRequestDto) {
        User newUser = new User();
        Role role = new Role();
        role.setName(RoleEnum.USER);

        ModelMapperUtils.map(registrationRequestDto, newUser);
        newUser.addRole(role);
        newUser.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));
        newUser.setKycLevel(KycLevel.TIER_1);


        return userRepository.saveAndFlush(newUser);
    }

    private Wallet createNewWalletAccount(User user) {
        Wallet newAccount = new Wallet();
        long newAccountNumber = getNewAccountNumber();
        newAccount.setAccountNumber(newAccountNumber);
        newAccount.setUser(user);
        return walletRepository.save(newAccount);
    }

    private void sendRegistrationConfirmationEmail(User user, String email) {
        String token = generateVerificationToken(user);
         sendMailService.sendEmail(EmailDto.builder()
                .sender("noreply@gmail.com")
                .subject("Please Activate Your Account")
                .body("Thank you for Creating your account with us " +
                        "please click on the link below to activate your account : " +
                        "http://localhost:9099/api/v1/account/user/verify-account/" + token)
                .recipient(email)
                .build());
    }

    private String generateVerificationToken(User user) {
        log.info("inside generateVerificationToken, generating token for {}", user.getEmail());
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);

        log.info("Saving token to database");
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Override
    public void updateUser(UpdateUserRequestDto updateUserDto, String id) {
        log.info("service updateUser - updating user with id :: [{}] ::", id);
        User user = userRepository.findById(id).<ResourceNotFoundException>orElseThrow(
                () -> {
                    throw new ResourceNotFoundException("user does not exist");
                }
        );
        if (StringUtils.isNoneBlank(updateUserDto.getFirstName()))
            user.setFirstName(updateUserDto.getFirstName());
        if (StringUtils.isNoneBlank(updateUserDto.getLastName()))
            user.setLastName(updateUserDto.getLastName());
        if (StringUtils.isNoneBlank(updateUserDto.getPhoneNumber()))
            user.setPhoneNumber(updateUserDto.getPhoneNumber());

        userRepository.save(user);
    }

    @Override
    public String verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken.isPresent()) {
            Boolean isVerified = verificationToken.get().getUser().getIsVerified();
            if (isVerified) {
                throw new CustomException("token used, please request another activation token", HttpStatus.BAD_REQUEST);
            }
            return fetchUserAndEnable(verificationToken.get());
        }
        throw new CustomException("token invalid");
    }

    @Override
    public User getLoggedInUser() {
        String loggedInUser = AppUtil.getPrincipal();
        if (loggedInUser.equalsIgnoreCase("system"))
            throw new ResourceNotFoundException("user not found");
        log.info("AccountServiceImpl getLoggedInUserAccountDetails- logged In user :: [{}]", loggedInUser);
        return userRepository.getUserByEmail(loggedInUser).orElseThrow(
                () -> {
                    throw new ResourceNotFoundException("user not found");
                }
        );
    }

    private String fetchUserAndEnable(VerificationToken verificationToken) {
        User user = verificationToken.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("User with token not found");
        }
        user.setIsVerified(true);
        userRepository.save(user);
        return "Account verified successfully";
    }

    private boolean doesAccountAlreadyExit(long accountNumber) {
        return walletRepository.getAccountByAccountNumber(accountNumber).isPresent();
    }

    private boolean doesUserAlreadyExist(String email) {
        return userRepository.getUserByEmail(email).isPresent();
    }

    private long getNewAccountNumber() {
        long newAccountNumber = AccountNumberUtil.generateAccountNumber();
        while (doesAccountAlreadyExit(newAccountNumber)) newAccountNumber = AccountNumberUtil.generateAccountNumber();
        return newAccountNumber;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("userService loadUserByUserName - email :: [{}] ::", email);
        log.info("User ==> [{}]", userRepository.getUserByEmail(email));
        User user = userRepository.getUserByEmail(email)
                .orElseThrow(
                        () -> {
                            throw new ResourceNotFoundException("user does not exist");
                        }
                );

        Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

}
