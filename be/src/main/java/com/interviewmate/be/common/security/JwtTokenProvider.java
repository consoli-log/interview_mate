package com.interviewmate.be.common.security;

import com.interviewmate.be.common.exception.CustomException;
import com.interviewmate.be.common.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * packageName    : com.interviewmate.be.common.security
 * fileName       : JwtTokenProvider
 * author         : eumsoli
 * date           : 2025-03-07
 * description    : JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    /**
     * 생성자에서 키와 유효기간을 설정
     *
     * @param secret JWT 서명용 비밀키
     * @param accessTokenExpiration Access Token 유효시간
     * @param refreshTokenExpiration Refresh Token 유효시간
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * methodName : generateAccessToken
     * description : Access Token을 생성하는 메서드
     *
     * @param claims JWT에 포함할 사용자 정보 (예: email, provider 등)
     * @return 생성된 Access Token 문자열
     */
    public String generateAccessToken(Map<String, Object> claims) {
        return generateToken(claims, accessTokenExpiration);
    }

    /**
     * methodName : generateRefreshToken
     * description : Refresh Token을 생성하는 메서드
     *
     * @param claims JWT에 포함할 사용자 정보 (최소한의 정보만 포함)
     * @return 생성된 Refresh Token 문자열
     */
    public String generateRefreshToken(Map<String, Object> claims) {
        return generateToken(claims, refreshTokenExpiration);
    }

    /**
     * methodName : generateToken
     * description : JWT 토큰을 생성하는 내부 메서드
     *
     * @param claims JWT에 포함할 사용자 정보
     * @param validity 토큰 유효시간
     * @return 생성된 JWT 토큰 문자열
     */
    private String generateToken(Map<String, Object> claims, long validity) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validity);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * methodName : validateToken
     * description : JWT 토큰을 검증하는 메서드
     *
     * @param token 검증할 JWT 토큰 문자열
     * @return 유효한 토큰이면 true, 그렇지 않으면 false
     * @throws CustomException 토큰이 유효하지 않거나, 서명이 올바르지 않을 때 발생
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
            throw new CustomException(ErrorCode.INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 JWT 토큰입니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * methodName : getClaims
     * description : JWT 토큰에서 모든 Claim을 추출하는 메서드
     *
     * @param token JWT 토큰 문자열
     * @return Claim 정보 (사용자 정보 포함)
     */
    public Map<String, Object> getClaims(String token) {
        return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }

}
