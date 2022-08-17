package com.example.redditclone.service;

import com.example.redditclone.entity.User;
import com.example.redditclone.entity.VerificationToken;
import com.example.redditclone.exceptions.SpringRedditCloneException;
import com.example.redditclone.model.AuthenticationResponse;
import com.example.redditclone.model.LoginRequest;
import com.example.redditclone.model.RefreshTokenRequest;
import com.example.redditclone.model.RegisterRequest;
import com.example.redditclone.repository.UserRepository;
import com.example.redditclone.repository.VerificationTokenRepository;
import com.example.redditclone.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Period;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class AuthService {

    public static final String ACTIVATION_EMAIL = "http://localhost:8080/api/auth/verifyAccount";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private MailContentBuilderService mailContentBuilderService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public void signUp(RegisterRequest registerRequest) {
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(encodedPassword(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .createdTime(Instant.now())
                .enabled(false)
                .build();

        userRepository.save(user);

        String verificationToken = generateVerificationToken(user);

        String message = mailContentBuilderService.build("Thank you for signing up to Reddit Clone, please click on the url to activate your account: " + ACTIVATION_EMAIL + "?token=" + verificationToken);

        log.info(message);

//        mailService.sendMail(new NotificationEmail("Please activate your account", user.getEmail(), message));


    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .expiryDate(Instant.now().plus(Period.ofDays(10)))
                .user(user)
                .build();

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    private String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void verifyAccount(String token) {
        VerificationToken verificationToken =  verificationTokenRepository.findByToken(token)
                                                                        .orElseThrow(() -> new SpringRedditCloneException("Invalid Token"));
        if(verificationToken.getExpiryDate().isBefore(Instant.now())) {
            log.info("now is: " + Instant.now());
            log.info("expirydate is: " + verificationToken.getExpiryDate());
            throw new SpringRedditCloneException("Verification token has expired");
        }

        fetchTheUserAndEnable(verificationToken);

    }

    private void fetchTheUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditCloneException("User not found with username: " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        try {
//            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//            SecurityContextHolder.getContext().setAuthentication(authenticate);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        } catch (DisabledException e) {
            throw new SpringRedditCloneException("User is disabled");
        } catch (BadCredentialsException e) {
            throw new SpringRedditCloneException("Bad credentials from user");
        } catch (Exception e) {
            throw new SpringRedditCloneException("Failed to authenticate the user");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        log.info("After generating token in login: " + SecurityContextHolder.getContext().getAuthentication());

        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .username(loginRequest.getUsername())
                .refreshToken(refreshTokenService.generateRefreshToken(loginRequest.getUsername()).getToken())
                .expiresAt(Instant.now().plusMillis(jwtUtil.getTokenValidityInMilliSecond()))
                .build();

    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken(), refreshTokenRequest.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshTokenRequest.getUsername());
        String jwtToken = jwtUtil.generateToken(userDetails);

        return AuthenticationResponse.builder()
                .authenticationToken(jwtToken)
                .expiresAt(Instant.now().plusMillis(jwtUtil.getTokenValidityInMilliSecond()))
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .username(refreshTokenRequest.getUsername())
                .build();

    }

    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new SpringRedditCloneException("Username not found " + principal.getUsername()));
        return user;
    }

}
