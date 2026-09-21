package com.mentorship.hanakoleh.domain.user.controller;

import com.mentorship.hanakoleh.config.SecurityConfig;
import com.mentorship.hanakoleh.domain.user.dto.AddressDetailsDto;
import com.mentorship.hanakoleh.domain.user.dto.CreateAddressRequest;
import com.mentorship.hanakoleh.domain.user.dto.GetCustomerAddressesResponse;
import com.mentorship.hanakoleh.domain.user.dto.UpdateAddressRequest;
import com.mentorship.hanakoleh.domain.user.exception.AddressNotFoundException;
import com.mentorship.hanakoleh.domain.user.mapper.AddressMapper;
import com.mentorship.hanakoleh.domain.user.model.Address;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.service.AddressService;
import com.mentorship.hanakoleh.handler.GlobalExceptionHandler;
import com.mentorship.hanakoleh.security.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AddressController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, GlobalExceptionHandler.class})
@DisplayName("AddressController Integration Tests")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private AddressMapper addressMapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private Integer customerId;
    private Long addressId;
    private UserPrincipal userPrincipal;
    private Customer mockCustomer;
    private Address mockAddress;
    private AddressDetailsDto addressDetailsDto;

    @BeforeEach
    void setUp() {
        customerId = 101;
        addressId = 1L;

        userPrincipal = new UserPrincipal(
                customerId,
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        mockCustomer = Customer.builder()
                .id(customerId)
                .build();

        mockAddress = Address.builder()
                .id(addressId)
                .customer(mockCustomer)
                .streetAddress("123 Main St")
                .buildingNumber("A1")
                .floor("2")
                .apartmentNumber("201")
                .landmark("Near park")
                .districtName("Downtown")
                .isDefault(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        addressDetailsDto = new AddressDetailsDto(
                mockAddress.getId(),
                mockAddress.getStreetAddress(),
                mockAddress.getBuildingNumber(),
                mockAddress.getFloor(),
                mockAddress.getApartmentNumber(),
                mockAddress.getLandmark(),
                mockAddress.getDistrictName(),
                mockAddress.getIsDefault(),
                mockAddress.getCreatedAt(),
                mockAddress.getUpdatedAt()
        );
    }

    @Nested
    @DisplayName("Security & BOLA Elimination Tests")
    class SecurityTests {

        @Test
        @DisplayName("Should return 401 Unauthorized when unauthenticated")
        void endpoints_requireAuthentication() throws Exception {
            mockMvc.perform(post("/api/v1/me/addresses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(CreateAddressRequest.builder().streetAddress("123 St").build()))
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/v1/me/addresses/{addressId}", addressId))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/v1/me/addresses"))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(put("/api/v1/me/addresses/{addressId}", addressId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(UpdateAddressRequest.builder().streetAddress("123 St").isDefault(true).build()))
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(delete("/api/v1/me/addresses/{addressId}", addressId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(patch("/api/v1/me/addresses/{addressId}/default", addressId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should extract identity from UserPrincipal not path variables")
        void identityExtractedFromUserPrincipal() throws Exception {
            when(addressService.createAddress(eq(customerId), any(CreateAddressRequest.class)))
                    .thenReturn(mockAddress);
            when(addressMapper.toDetailsDto(mockAddress)).thenReturn(addressDetailsDto);

            mockMvc.perform(post("/api/v1/me/addresses")
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(CreateAddressRequest.builder()
                                    .streetAddress("123 Main St")
                                    .isDefault(true)
                                    .build())))
                    .andExpect(status().isCreated());

            verify(addressService).createAddress(eq(customerId), any(CreateAddressRequest.class));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/me/addresses")
    class CreateAddressTests {

        @Test
        @DisplayName("Should return 201 Created with valid Location header")
        void createAddress_returns201Created() throws Exception {
            when(addressService.createAddress(eq(customerId), any(CreateAddressRequest.class)))
                    .thenReturn(mockAddress);
            when(addressMapper.toDetailsDto(mockAddress)).thenReturn(addressDetailsDto);

            mockMvc.perform(post("/api/v1/me/addresses")
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(CreateAddressRequest.builder()
                                    .streetAddress("123 Main St")
                                    .buildingNumber("A1")
                                    .districtName("Downtown")
                                    .isDefault(true)
                                    .build())))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(header().string("Location", "http://localhost/api/v1/me/addresses/1"))
                    .andExpect(jsonPath("$.id").value(addressId))
                    .andExpect(jsonPath("$.streetAddress").value("123 Main St"))
                    .andExpect(jsonPath("$.isDefault").value(true));

            verify(addressService).createAddress(eq(customerId), any(CreateAddressRequest.class));
            verify(addressMapper).toDetailsDto(mockAddress);
        }

        @Test
        @DisplayName("Should return 400 Bad Request for invalid input")
        void createAddress_invalidInput_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/me/addresses")
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(CreateAddressRequest.builder()
                                    .streetAddress("")
                                    .build())))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/me/addresses/{addressId}")
    class GetAddressByIdTests {

        @Test
        @DisplayName("Should return 200 OK with AddressDetailsDto")
        void getAddressById_returns200Ok() throws Exception {
            when(addressService.getAddressOrThrow(addressId, customerId)).thenReturn(mockAddress);
            when(addressMapper.toDetailsDto(mockAddress)).thenReturn(addressDetailsDto);

            mockMvc.perform(get("/api/v1/me/addresses/{addressId}", addressId)
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(addressId))
                    .andExpect(jsonPath("$.streetAddress").value("123 Main St"));

            verify(addressService).getAddressOrThrow(addressId, customerId);
            verify(addressMapper).toDetailsDto(mockAddress);
        }

        @Test
        @DisplayName("Should return 404 Not Found when address does not exist")
        void getAddressById_notFound_returns404() throws Exception {
            when(addressService.getAddressOrThrow(addressId, customerId))
                    .thenThrow(new AddressNotFoundException("Address not found with ID: " + addressId));

            mockMvc.perform(get("/api/v1/me/addresses/{addressId}", addressId)
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal)))
                    .andExpect(status().isNotFound());

            verify(addressService).getAddressOrThrow(addressId, customerId);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/me/addresses")
    class GetAllAddressesTests {

        @Test
        @DisplayName("Should return 200 OK with address list")
        void getAllAddresses_returns200Ok() throws Exception {
            Address address2 = Address.builder()
                    .id(2L)
                    .customer(mockCustomer)
                    .streetAddress("456 Second St")
                    .isDefault(false)
                    .build();

            AddressDetailsDto dto2 = new AddressDetailsDto(
                    address2.getId(),
                    address2.getStreetAddress(),
                    address2.getBuildingNumber(),
                    address2.getFloor(),
                    address2.getApartmentNumber(),
                    address2.getLandmark(),
                    address2.getDistrictName(),
                    address2.getIsDefault(),
                    address2.getCreatedAt(),
                    address2.getUpdatedAt()
            );

            List<Address> addresses = List.of(mockAddress, address2);
            List<AddressDetailsDto> dtos = List.of(addressDetailsDto, dto2);

            GetCustomerAddressesResponse response = GetCustomerAddressesResponse.builder()
                    .data(dtos)
                    .build();

            when(addressService.getAllAddressesByCustomerId(customerId)).thenReturn(addresses);
            when(addressMapper.toGetCustomerAddressesResponse(addresses)).thenReturn(response);

            mockMvc.perform(get("/api/v1/me/addresses")
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value(addressId))
                    .andExpect(jsonPath("$.data[1].id").value(2L));

            verify(addressService).getAllAddressesByCustomerId(customerId);
            verify(addressMapper).toGetCustomerAddressesResponse(addresses);
        }

        @Test
        @DisplayName("Should return 200 OK with empty list when no addresses")
        void getAllAddresses_emptyList_returns200Ok() throws Exception {
            GetCustomerAddressesResponse response = GetCustomerAddressesResponse.builder()
                    .data(Collections.emptyList())
                    .build();

            when(addressService.getAllAddressesByCustomerId(customerId)).thenReturn(Collections.emptyList());
            when(addressMapper.toGetCustomerAddressesResponse(Collections.emptyList())).thenReturn(response);

            mockMvc.perform(get("/api/v1/me/addresses")
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));

            verify(addressService).getAllAddressesByCustomerId(customerId);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/me/addresses/{addressId}")
    class UpdateAddressTests {

        @Test
        @DisplayName("Should return 200 OK with updated details")
        void updateAddress_returns200Ok() throws Exception {
            when(addressService.updateAddress(eq(addressId), eq(customerId), any(UpdateAddressRequest.class)))
                    .thenReturn(mockAddress);
            when(addressMapper.toDetailsDto(mockAddress)).thenReturn(addressDetailsDto);

            mockMvc.perform(put("/api/v1/me/addresses/{addressId}", addressId)
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(UpdateAddressRequest.builder()
                                    .streetAddress("456 Updated St")
                                    .buildingNumber("B2")
                                    .districtName("Uptown")
                                    .isDefault(true)
                                    .build())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(addressId));

            verify(addressService).updateAddress(eq(addressId), eq(customerId), any(UpdateAddressRequest.class));
            verify(addressMapper).toDetailsDto(mockAddress);
        }

        @Test
        @DisplayName("Should return 404 Not Found when address does not exist")
        void updateAddress_notFound_returns404() throws Exception {
            when(addressService.updateAddress(eq(addressId), eq(customerId), any(UpdateAddressRequest.class)))
                    .thenThrow(new AddressNotFoundException("Address not found with ID: " + addressId));

            mockMvc.perform(put("/api/v1/me/addresses/{addressId}", addressId)
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(UpdateAddressRequest.builder()
                                    .streetAddress("456 Updated St")
                                    .isDefault(true)
                                    .build())))
                    .andExpect(status().isNotFound());

            verify(addressService).updateAddress(eq(addressId), eq(customerId), any(UpdateAddressRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request for invalid input")
        void updateAddress_invalidInput_returns400() throws Exception {
            mockMvc.perform(put("/api/v1/me/addresses/{addressId}", addressId)
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(UpdateAddressRequest.builder()
                                    .streetAddress("")
                                    .isDefault(true)
                                    .build())))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/me/addresses/{addressId}")
    class DeleteAddressTests {

        @Test
        @DisplayName("Should return 204 No Content")
        void deleteAddress_returns204NoContent() throws Exception {
            doNothing().when(addressService).deleteAddress(addressId, customerId);

            mockMvc.perform(delete("/api/v1/me/addresses/{addressId}", addressId)
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(addressService).deleteAddress(addressId, customerId);
        }

        @Test
        @DisplayName("Should return 404 Not Found when address does not exist")
        void deleteAddress_notFound_returns404() throws Exception {
            doThrow(new AddressNotFoundException("Address not found with ID: " + addressId))
                    .when(addressService).deleteAddress(addressId, customerId);

            mockMvc.perform(delete("/api/v1/me/addresses/{addressId}", addressId)
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf()))
                    .andExpect(status().isNotFound());

            verify(addressService).deleteAddress(addressId, customerId);
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/me/addresses/{addressId}/default")
    class SetDefaultAddressTests {

        @Test
        @DisplayName("Should return 204 No Content")
        void setDefaultAddress_returns204NoContent() throws Exception {
            doNothing().when(addressService).setDefaultAddress(addressId, customerId);

            mockMvc.perform(patch("/api/v1/me/addresses/{addressId}/default", addressId)
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(addressService).setDefaultAddress(addressId, customerId);
        }

        @Test
        @DisplayName("Should return 404 Not Found when address does not exist")
        void setDefaultAddress_notFound_returns404() throws Exception {
            doThrow(new AddressNotFoundException("Address not found with ID: " + addressId))
                    .when(addressService).setDefaultAddress(addressId, customerId);

            mockMvc.perform(patch("/api/v1/me/addresses/{addressId}/default", addressId)
                            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                            .with(csrf()))
                    .andExpect(status().isNotFound());

            verify(addressService).setDefaultAddress(addressId, customerId);
        }
    }
}
