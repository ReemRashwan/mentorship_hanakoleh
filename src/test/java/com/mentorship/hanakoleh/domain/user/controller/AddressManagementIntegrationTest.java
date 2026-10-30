package com.mentorship.hanakoleh.domain.user.controller;

import com.mentorship.hanakoleh.TestcontainersConfiguration;
import com.mentorship.hanakoleh.domain.user.dto.CreateAddressRequest;
import com.mentorship.hanakoleh.domain.user.dto.UpdateAddressRequest;
import com.mentorship.hanakoleh.security.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end address management tests against a real PostgreSQL container.
 * Flyway runs the real migrations, and requests go through the real security filter chain
 * with genuine JWTs. Assertions check the database state, including the
 * "one default address per customer" rule enforced by a partial unique index.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DisplayName("Address management integration tests (Testcontainers)")
class AddressManagementIntegrationTest {

    private static final String ADDRESSES_URL = "/api/v1/me/addresses";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final List<Integer> seededUserIds = new ArrayList<>();

    @AfterEach
    void deleteSeededUsers() {
        // Deleting the user cascades to customer and address rows.
        seededUserIds.forEach(userId -> jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", userId));
        seededUserIds.clear();
    }

    @Nested
    @DisplayName("POST /api/v1/me/addresses")
    class CreateAddressTests {

        @Test
        @DisplayName("First address becomes the default even when isDefault is not sent")
        void firstAddress_becomesDefault() throws Exception {
            SeededCustomer customer = seedCustomer();

            mockMvc.perform(post(ADDRESSES_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearer(customer))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(addressRequest("Main St", null))))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.isDefault").value(true));

            assertThat(defaultAddressIds(customer)).hasSize(1);
        }

        @Test
        @DisplayName("New address marked as default replaces the previous default")
        void newDefault_replacesExistingDefault() throws Exception {
            SeededCustomer customer = seedCustomer();
            long first = createAddress(customer, "First St", true);

            long second = createAddress(customer, "Second St", true);

            assertThat(defaultAddressIds(customer)).containsExactly(second);
            assertThat(isDefault(first)).isFalse();
        }

        @Test
        @DisplayName("New non-default address keeps the existing default")
        void nonDefault_keepsExistingDefault() throws Exception {
            SeededCustomer customer = seedCustomer();
            long first = createAddress(customer, "First St", true);

            createAddress(customer, "Second St", false);

            assertThat(defaultAddressIds(customer)).containsExactly(first);
        }

        @Test
        @DisplayName("Invalid input returns 400 and persists nothing")
        void invalidInput_persistsNothing() throws Exception {
            SeededCustomer customer = seedCustomer();

            mockMvc.perform(post(ADDRESSES_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearer(customer))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(addressRequest("", null))))
                    .andExpect(status().isBadRequest());

            assertThat(addressCount(customer)).isZero();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/me/addresses")
    class ReadAddressTests {

        @Test
        @DisplayName("Lists only the authenticated customer's addresses")
        void getAll_returnsOnlyOwnAddresses() throws Exception {
            SeededCustomer customer = seedCustomer();
            SeededCustomer other = seedCustomer();
            long mine1 = createAddress(customer, "Mine 1", true);
            long mine2 = createAddress(customer, "Mine 2", false);
            createAddress(other, "Theirs", true);

            mockMvc.perform(get(ADDRESSES_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearer(customer)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value(mine1))
                    .andExpect(jsonPath("$.data[1].id").value(mine2));
        }

        @Test
        @DisplayName("Another customer's address returns 404")
        void getById_ofAnotherCustomer_returns404() throws Exception {
            SeededCustomer owner = seedCustomer();
            SeededCustomer intruder = seedCustomer();
            long addressId = createAddress(owner, "Private St", true);

            mockMvc.perform(get(ADDRESSES_URL + "/{id}", addressId)
                            .header(HttpHeaders.AUTHORIZATION, bearer(intruder)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/me/addresses/{addressId}")
    class UpdateAddressTests {

        @Test
        @DisplayName("Updates address details")
        void updateDetails_persists() throws Exception {
            SeededCustomer customer = seedCustomer();
            long first = createAddress(customer, "First St", true);
            createAddress(customer, "Second St", false);
            long second = createAddress(customer, "Old St", false);

            mockMvc.perform(put(ADDRESSES_URL + "/{id}", second)
                            .header(HttpHeaders.AUTHORIZATION, bearer(customer))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest("New St", false))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.streetAddress").value("New St"));

            assertThat(streetOf(second)).isEqualTo("New St");
            assertThat(defaultAddressIds(customer)).containsExactly(first);
        }

        @Test
        @DisplayName("Keeps the default address as default when it is updated with isDefault=true")
        void updateDefault_withIsDefaultTrue_keepsDefault() throws Exception {
            SeededCustomer customer = seedCustomer();
            long only = createAddress(customer, "Only St", true);

            // Same values as the seeded address, as a "save unchanged form" request would send.
            UpdateAddressRequest unchanged = UpdateAddressRequest.builder()
                    .streetAddress("Only St")
                    .buildingNumber("A1")
                    .districtName("Downtown")
                    .isDefault(true)
                    .build();

            mockMvc.perform(put(ADDRESSES_URL + "/{id}", only)
                            .header(HttpHeaders.AUTHORIZATION, bearer(customer))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(unchanged)))
                    .andExpect(status().isOk());

            assertThat(defaultAddressIds(customer)).containsExactly(only);
        }

        @Test
        @DisplayName("Another customer's address returns 404 and is left unchanged")
        void updateOtherCustomersAddress_returns404() throws Exception {
            SeededCustomer owner = seedCustomer();
            SeededCustomer intruder = seedCustomer();
            long addressId = createAddress(owner, "Private St", true);

            mockMvc.perform(put(ADDRESSES_URL + "/{id}", addressId)
                            .header(HttpHeaders.AUTHORIZATION, bearer(intruder))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest("Hacked St", true))))
                    .andExpect(status().isNotFound());

            assertThat(streetOf(addressId)).isEqualTo("Private St");
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/me/addresses/{addressId}/default")
    class SetDefaultAddressTests {

        @Test
        @DisplayName("Moves the default flag to the selected address")
        void setDefault_movesDefault() throws Exception {
            SeededCustomer customer = seedCustomer();
            createAddress(customer, "First St", true);
            long second = createAddress(customer, "Second St", false);

            mockMvc.perform(patch(ADDRESSES_URL + "/{id}/default", second)
                            .header(HttpHeaders.AUTHORIZATION, bearer(customer))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            assertThat(defaultAddressIds(customer)).containsExactly(second);
        }

        @Test
        @DisplayName("Another customer's address returns 404")
        void setDefault_ofAnotherCustomer_returns404() throws Exception {
            SeededCustomer owner = seedCustomer();
            SeededCustomer intruder = seedCustomer();
            long ownerDefault = createAddress(owner, "Owner St", true);
            long intruderDefault = createAddress(intruder, "Intruder St", true);

            mockMvc.perform(patch(ADDRESSES_URL + "/{id}/default", ownerDefault)
                            .header(HttpHeaders.AUTHORIZATION, bearer(intruder))
                            .with(csrf()))
                    .andExpect(status().isNotFound());

            assertThat(defaultAddressIds(owner)).containsExactly(ownerDefault);
            assertThat(defaultAddressIds(intruder)).containsExactly(intruderDefault);
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/me/addresses/{addressId}")
    class DeleteAddressTests {

        @Test
        @DisplayName("Deleting a non-default address keeps the default")
        void deleteNonDefault_keepsDefault() throws Exception {
            SeededCustomer customer = seedCustomer();
            long first = createAddress(customer, "First St", true);
            long second = createAddress(customer, "Second St", false);

            mockMvc.perform(delete(ADDRESSES_URL + "/{id}", second)
                            .header(HttpHeaders.AUTHORIZATION, bearer(customer))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            assertThat(addressCount(customer)).isEqualTo(1);
            assertThat(defaultAddressIds(customer)).containsExactly(first);
        }

        @Test
        @DisplayName("Deleting the default promotes another address to default")
        void deleteDefault_promotesAnotherAddress() throws Exception {
            SeededCustomer customer = seedCustomer();
            long first = createAddress(customer, "First St", true);
            long second = createAddress(customer, "Second St", false);

            mockMvc.perform(delete(ADDRESSES_URL + "/{id}", first)
                            .header(HttpHeaders.AUTHORIZATION, bearer(customer))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            assertThat(addressCount(customer)).isEqualTo(1);
            assertThat(defaultAddressIds(customer)).containsExactly(second);
        }

        @Test
        @DisplayName("Another customer's address returns 404 and is not deleted")
        void deleteOtherCustomersAddress_returns404() throws Exception {
            SeededCustomer owner = seedCustomer();
            SeededCustomer intruder = seedCustomer();
            long addressId = createAddress(owner, "Private St", true);

            mockMvc.perform(delete(ADDRESSES_URL + "/{id}", addressId)
                            .header(HttpHeaders.AUTHORIZATION, bearer(intruder))
                            .with(csrf()))
                    .andExpect(status().isNotFound());

            assertThat(addressCount(owner)).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Authentication")
    class AuthenticationTests {

        @Test
        @DisplayName("Requests without a token return 401")
        void withoutToken_returns401() throws Exception {
            mockMvc.perform(get(ADDRESSES_URL))
                    .andExpect(status().isUnauthorized());
        }
    }

    private SeededCustomer seedCustomer() {
        String email = "address-it-" + UUID.randomUUID() + "@example.com";
        String phoneNumber = "+20" + String.format("%09d", ThreadLocalRandom.current().nextInt(1_000_000_000));

        Integer userId = jdbcTemplate.queryForObject("""
                INSERT INTO users (user_type_id, user_email, user_phone_number, user_first_name,
                                   user_last_name, user_language_id, user_password_hash)
                SELECT ut.user_type_id, ?, ?, 'Address', 'Tester', l.language_id, 'not-used'
                FROM user_type ut, language l
                WHERE ut.user_type_name = 'customer' AND l.language_code = 'en'
                RETURNING user_id
                """, Integer.class, email, phoneNumber);
        jdbcTemplate.update("INSERT INTO customer (user_id, customer_notification_status) VALUES (?, TRUE)", userId);

        seededUserIds.add(userId);
        return new SeededCustomer(userId, email);
    }

    private String bearer(SeededCustomer customer) {
        return "Bearer " + jwtTokenProvider.generateToken(customer.email(), customer.id());
    }

    private long createAddress(SeededCustomer customer, String street, Boolean isDefault) throws Exception {
        MvcResult result = mockMvc.perform(post(ADDRESSES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(customer))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest(street, isDefault))))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private CreateAddressRequest addressRequest(String street, Boolean isDefault) {
        return CreateAddressRequest.builder()
                .streetAddress(street)
                .buildingNumber("A1")
                .districtName("Downtown")
                .isDefault(isDefault)
                .build();
    }

    private UpdateAddressRequest updateRequest(String street, boolean isDefault) {
        return UpdateAddressRequest.builder()
                .streetAddress(street)
                .buildingNumber("B2")
                .districtName("Uptown")
                .isDefault(isDefault)
                .build();
    }

    private List<Long> defaultAddressIds(SeededCustomer customer) {
        return jdbcTemplate.queryForList(
                "SELECT address_id FROM address WHERE customer_id = ? AND is_default = true ORDER BY address_id",
                Long.class, customer.id());
    }

    private int addressCount(SeededCustomer customer) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM address WHERE customer_id = ?", Integer.class, customer.id());
        return count == null ? 0 : count;
    }

    private boolean isDefault(long addressId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT is_default FROM address WHERE address_id = ?", Boolean.class, addressId));
    }

    private String streetOf(long addressId) {
        return jdbcTemplate.queryForObject(
                "SELECT street FROM address WHERE address_id = ?", String.class, addressId);
    }

    private record SeededCustomer(Integer id, String email) {
    }
}
