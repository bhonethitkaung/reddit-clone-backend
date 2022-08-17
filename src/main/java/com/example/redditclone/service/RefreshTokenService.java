package com.example.redditclone.service;

import com.example.redditclone.entity.RefreshToken;
import com.example.redditclone.entity.User;
import com.example.redditclone.exceptions.SpringRedditCloneException;
import com.example.redditclone.repository.RefreshTokenRepository;
import com.example.redditclone.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken generateRefreshToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditCloneException("User with username " + username + " is not found"));
        Optional<RefreshToken> refreshTokenFound = refreshTokenRepository.findByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .createdDate(Instant.now())
                .user(user)
                .build();

        if(refreshTokenFound.isPresent()) {
            refreshToken.setRefreshTokenId(refreshTokenFound.get().getRefreshTokenId());
        }
        return refreshTokenRepository.save(refreshToken);
    }

    public void validateRefreshToken(String token, String username) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new SpringRedditCloneException("Refresh Token is not valid"));
        if(!refreshToken.getUser().getUsername().equals(username)) {
            throw new SpringRedditCloneException("Refresh Token and username is not valid");
        }
    }

    public void deleteRefreshToken(String token, String username) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new SpringRedditCloneException("Refresh Token is not valid"));
        if(!refreshToken.getUser().getUsername().equals(username)) {
            throw new SpringRedditCloneException("Refresh Token and username is not valid");
        }
        refreshTokenRepository.deleteByToken(token);
    }

}
