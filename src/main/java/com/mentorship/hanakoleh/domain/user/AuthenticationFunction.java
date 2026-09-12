package com.mentorship.hanakoleh.domain.user;

import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;

public class AuthenticationFunction {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Integer extractID(String authorizationHeader) {
        String tokenPrefix = "Bearer ";
        if (authorizationHeader == null
                || !authorizationHeader.startsWith(tokenPrefix)
                || (StringUtils.countOccurrencesOf(authorizationHeader, ".") < 1)) {
            throw new IllegalArgumentException("Token cannot be empty");
        }
        String token = authorizationHeader.replace(tokenPrefix, "");
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        return (MAPPER.readTree(payload)).get("customerID").asInt();
    }
}