package com.dub.spring.config;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.util.Assert;

/**
 * Reactive version of {@link JwtAuthenticationConverter} for converting a {@link Jwt} to
 * a {@link AbstractAuthenticationToken Mono&lt;AbstractAuthenticationToken&gt;}.
 *
 * @author Eric Deandrea
 * @since 5.2
 */
public final class MyReactiveJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter 
    				= new ReactiveJwtGrantedAuthoritiesConverterAdapter(
            new JwtGrantedAuthoritiesConverter());

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        
        return this.jwtGrantedAuthoritiesConverter.convert(jwt)
                .collectList()
                .map((authorities) -> new JwtAuthenticationToken(jwt, authorities));
        
    }

    /**
     * Sets the {@link Converter Converter&lt;Jwt, Flux&lt;GrantedAuthority&gt;&gt;} to
     * use. Defaults to a reactive {@link JwtGrantedAuthoritiesConverter}.
     * @param jwtGrantedAuthoritiesConverter The converter
     * @see JwtGrantedAuthoritiesConverter
     */
    public void setJwtGrantedAuthoritiesConverter(
            Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter) {
        Assert.notNull(jwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null");
        this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
    }

}