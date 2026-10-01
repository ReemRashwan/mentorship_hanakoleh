package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.dto.CreateAddressRequest;
import com.mentorship.hanakoleh.domain.user.dto.UpdateAddressRequest;
import com.mentorship.hanakoleh.domain.user.exception.AddressNotFoundException;
import com.mentorship.hanakoleh.domain.user.exception.CustomerNotFoundException;
import com.mentorship.hanakoleh.domain.user.mapper.AddressMapper;
import com.mentorship.hanakoleh.domain.user.model.Address;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Unit Tests")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AddressService addressService;

    private Integer customerId;
    private Long addressId;
    private Customer mockCustomer;
    private Address mockAddress;
    private CreateAddressRequest createRequest;
    private UpdateAddressRequest updateRequest;

    @BeforeEach
    void setUp() {
        customerId = 101;
        addressId = 1L;

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
                .isDefault(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        createRequest = CreateAddressRequest.builder()
                .streetAddress("123 Main St")
                .buildingNumber("A1")
                .floor("2")
                .apartmentNumber("201")
                .landmark("Near park")
                .districtName("Downtown")
                .isDefault(false)
                .build();

        updateRequest = UpdateAddressRequest.builder()
                .streetAddress("456 New St")
                .buildingNumber("B2")
                .floor("3")
                .apartmentNumber("301")
                .landmark("Near mall")
                .districtName("Uptown")
                .isDefault(true)
                .build();
    }

    @Nested
    @DisplayName("Tests for createAddress")
    class CreateAddressTests {

        @Test
        @DisplayName("Should set isDefault=true when creating customer's first address")
        void createAddress_firstAddress_setsDefaultTrue() {
            when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(mockCustomer));
            when(addressRepository.existsByCustomerId(customerId)).thenReturn(false);
            when(addressMapper.toEntity(createRequest, mockCustomer)).thenReturn(mockAddress);
            when(addressRepository.save(any(Address.class))).thenReturn(mockAddress);

            Address result = addressService.createAddress(customerId, createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getIsDefault()).isTrue();
            verify(addressRepository, never()).clearDefaultAddressByCustomerId(customerId);
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("Should clear previous defaults when creating a new default address")
        void createAddress_defaultAddress_clearsPreviousDefaults() {
            when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(mockCustomer));
            when(addressRepository.existsByCustomerId(customerId)).thenReturn(true);
            when(addressMapper.toEntity(createRequest, mockCustomer)).thenReturn(mockAddress);
            when(addressRepository.save(any(Address.class))).thenReturn(mockAddress);

            createRequest.setIsDefault(true);
            Address result = addressService.createAddress(customerId, createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getIsDefault()).isTrue();
            verify(addressRepository, times(1)).clearDefaultAddressByCustomerId(customerId);
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("Should not clear defaults when creating non-default address with existing addresses")
        void createAddress_nonDefaultAddress_doesNotClearDefaults() {
            when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(mockCustomer));
            when(addressRepository.existsByCustomerId(customerId)).thenReturn(true);
            when(addressMapper.toEntity(createRequest, mockCustomer)).thenReturn(mockAddress);
            when(addressRepository.save(any(Address.class))).thenReturn(mockAddress);

            createRequest.setIsDefault(false);
            Address result = addressService.createAddress(customerId, createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getIsDefault()).isFalse();
            verify(addressRepository, never()).clearDefaultAddressByCustomerId(customerId);
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
        void createAddress_customerNotFound_throwsException() {
            when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.createAddress(customerId, createRequest))
                    .isInstanceOf(CustomerNotFoundException.class)
                    .hasMessageContaining("Customer not found with ID: " + customerId);

            verify(addressRepository, never()).save(any(Address.class));
        }
    }

    @Nested
    @DisplayName("Tests for getAddressOrThrow")
    class GetAddressOrThrowTests {

        @Test
        @DisplayName("Should return address when addressId belongs to customerId")
        void getAddressOrThrow_validAddress_returnsAddress() {
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.of(mockAddress));

            Address result = addressService.getAddressOrThrow(addressId, customerId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(addressId);
            assertThat(result.getCustomer().getId()).isEqualTo(customerId);
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when address does not exist")
        void getAddressOrThrow_addressNotFound_throwsException() {
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.getAddressOrThrow(addressId, customerId))
                    .isInstanceOf(AddressNotFoundException.class)
                    .hasMessageContaining("Address not found with ID: " + addressId);
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when address belongs to another customer")
        void getAddressOrThrow_wrongCustomer_throwsException() {
            Integer otherCustomerId = 999;
            when(addressRepository.findByIdAndCustomerId(addressId, otherCustomerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.getAddressOrThrow(addressId, otherCustomerId))
                    .isInstanceOf(AddressNotFoundException.class)
                    .hasMessageContaining("Address not found with ID: " + addressId);
        }
    }

    @Nested
    @DisplayName("Tests for deleteAddress")
    class DeleteAddressTests {

        @Test
        @DisplayName("Should delete non-default address without changing default status of remaining addresses")
        void deleteAddress_nonDefaultAddress_deletesWithoutChangingDefaults() {
            mockAddress.setIsDefault(false);
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.of(mockAddress));
            doNothing().when(addressRepository).delete(any(Address.class));

            addressService.deleteAddress(addressId, customerId);

            verify(addressRepository).delete(mockAddress);
            verify(addressRepository, never()).findFirstByCustomerIdAndIdNotOrderByIdDesc(eq(customerId), anyLong());
        }

        @Test
        @DisplayName("Should promote remaining address to default when deleting default address")
        void deleteAddress_defaultAddress_promotesRemainingToDefault() {
            Address nextDefaultAddress = Address.builder()
                    .id(2L)
                    .customer(mockCustomer)
                    .streetAddress("456 Second St")
                    .isDefault(false)
                    .build();

            mockAddress.setIsDefault(true);
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.of(mockAddress));
            when(addressRepository.findFirstByCustomerIdAndIdNotOrderByIdDesc(customerId, addressId))
                    .thenReturn(Optional.of(nextDefaultAddress));
            doNothing().when(addressRepository).delete(any(Address.class));

            addressService.deleteAddress(addressId, customerId);

            assertThat(nextDefaultAddress.getIsDefault()).isTrue();
            verify(addressRepository).delete(mockAddress);
            verify(addressRepository, times(1)).findFirstByCustomerIdAndIdNotOrderByIdDesc(customerId, addressId);
        }

        @Test
        @DisplayName("Should delete last remaining address safely without throwing NPE")
        void deleteAddress_lastAddress_deletesSafely() {
            mockAddress.setIsDefault(true);
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.of(mockAddress));
            when(addressRepository.findFirstByCustomerIdAndIdNotOrderByIdDesc(customerId, addressId))
                    .thenReturn(Optional.empty());
            doNothing().when(addressRepository).delete(any(Address.class));

            addressService.deleteAddress(addressId, customerId);

            verify(addressRepository).delete(mockAddress);
            verify(addressRepository, times(1)).findFirstByCustomerIdAndIdNotOrderByIdDesc(customerId, addressId);
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when deleting non-existent address")
        void deleteAddress_addressNotFound_throwsException() {
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.deleteAddress(addressId, customerId))
                    .isInstanceOf(AddressNotFoundException.class)
                    .hasMessageContaining("Address not found with ID: " + addressId);

            verify(addressRepository, never()).delete(any(Address.class));
        }
    }

    @Nested
    @DisplayName("Tests for updateAddress")
    class UpdateAddressTests {

        @Test
        @DisplayName("Should update address successfully")
        void updateAddress_validRequest_updatesAddress() {
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.of(mockAddress));
            when(addressRepository.existsByCustomerId(customerId)).thenReturn(true);

            Address result = addressService.updateAddress(addressId, customerId, updateRequest);

            assertThat(result).isNotNull();
            verify(addressMapper).updateEntityFromRequest(eq(updateRequest), eq(mockAddress), anyBoolean());
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when updating non-existent address")
        void updateAddress_addressNotFound_throwsException() {
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.updateAddress(addressId, customerId, updateRequest))
                    .isInstanceOf(AddressNotFoundException.class)
                    .hasMessageContaining("Address not found with ID: " + addressId);

            verify(addressMapper, never()).updateEntityFromRequest(any(), any(), anyBoolean());
        }
    }

    @Nested
    @DisplayName("Tests for setDefaultAddress")
    class SetDefaultAddressTests {

        @Test
        @DisplayName("Should set address as default and clear previous default")
        void setDefaultAddress_validAddress_setsDefault() {
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.of(mockAddress));

            addressService.setDefaultAddress(addressId, customerId);

            assertThat(mockAddress.getIsDefault()).isTrue();
            verify(addressRepository, times(1)).clearDefaultAddressByCustomerId(customerId);
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when setting default on non-existent address")
        void setDefaultAddress_addressNotFound_throwsException() {
            when(addressRepository.findByIdAndCustomerId(addressId, customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.setDefaultAddress(addressId, customerId))
                    .isInstanceOf(AddressNotFoundException.class)
                    .hasMessageContaining("Address not found with ID: " + addressId);

            verify(addressRepository, never()).clearDefaultAddressByCustomerId(customerId);
        }
    }

    @Nested
    @DisplayName("Tests for getAllAddressesByCustomerId")
    class GetAllAddressesTests {

        @Test
        @DisplayName("Should return all addresses for customer")
        void getAllAddressesByCustomerId_validCustomer_returnsAddresses() {
            Address address2 = Address.builder()
                    .id(2L)
                    .customer(mockCustomer)
                    .streetAddress("789 Third St")
                    .isDefault(false)
                    .build();

            when(addressRepository.findAllByCustomerId(customerId))
                    .thenReturn(List.of(mockAddress, address2));

            List<Address> result = addressService.getAllAddressesByCustomerId(customerId);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(mockAddress, address2);
        }

        @Test
        @DisplayName("Should return empty list when customer has no addresses")
        void getAllAddressesByCustomerId_noAddresses_returnsEmptyList() {
            when(addressRepository.findAllByCustomerId(customerId)).thenReturn(List.of());

            List<Address> result = addressService.getAllAddressesByCustomerId(customerId);

            assertThat(result).isEmpty();
        }
    }
}
