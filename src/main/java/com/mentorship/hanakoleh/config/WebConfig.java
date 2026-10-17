package com.mentorship.hanakoleh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/**
 * Serializes Spring Data {@code Page} responses via a stable DTO envelope
 * ({@code content} + a slim {@code page} block) instead of the raw PageImpl shape,
 * which Spring Boot warns is not guaranteed stable across versions.
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class WebConfig {
}
