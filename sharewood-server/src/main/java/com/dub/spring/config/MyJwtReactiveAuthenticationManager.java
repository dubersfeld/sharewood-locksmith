package com.dub.spring.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.util.Assert;

import reactor.core.publisher.Mono;

public final class MyJwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final ReactiveJwtDecoder jwtDecoder;

    private Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverterAdapter(
            new JwtAuthenticationConverter());

    public MyJwtReactiveAuthenticationManager(ReactiveJwtDecoder jwtDecoder) {
        Assert.notNull(jwtDecoder, "jwtDecoder cannot be null");
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
  
        return Mono.justOrEmpty(authentication)
                .filter((a) -> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap(this.jwtDecoder::decode)
                .flatMap(this.jwtAuthenticationConverter::convert)
                .cast(Authentication.class)
                .onErrorMap(JwtException.class, this::onError);
       
    }

    /**
     * Use the given {@link Converter} for converting a {@link Jwt} into an
     * {@link AbstractAuthenticationToken}.
     * @param jwtAuthenticationConverter the {@link Converter} to use
     */
    public void setJwtAuthenticationConverter(
            Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter) {
        Assert.notNull(jwtAuthenticationConverter, "jwtAuthenticationConverter cannot be null");
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    private AuthenticationException onError(JwtException ex) {
        if (ex instanceof BadJwtException) {
            return new InvalidBearerTokenException(ex.getMessage(), ex);
        }
        return new AuthenticationServiceException(ex.getMessage(), ex);
    }

}