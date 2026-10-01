package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.dto.CreateAddressRequest;
import com.mentorship.hanakoleh.domain.user.dto.UpdateAddressRequest;
import com.mentorship.hanakoleh.domain.user.exception.AddressNotFoundException;
import com.mentorship.hanakoleh.domain.user.exception.CustomerNotFoundException;
import com.mentorship.hanakoleh.domain.user.mapper.AddressMapper;
import com.mentorship.hanakoleh.domain.user.model.Address;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final static Logger log = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final CustomerService customerService;

    @Transactional
    public Address createAddress(Integer customerId, CreateAddressRequest request) {
        Customer customer = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));

        boolean shouldBeDefault = resolveAndHandleDefaultStatus(customerId, request.getIsDefault());

        Address address = addressMapper.toEntity(request, customer);
        address.setIsDefault(shouldBeDefault);

        Address savedAddress = addressRepository.save(address);
        log.info("Address created with ID: {} for customer {}", savedAddress.getId(), customerId);

        return savedAddress;
    }

    private boolean resolveAndHandleDefaultStatus(Integer customerId, Boolean requestedIsDefault) {
        boolean hasExistingAddresses = addressRepository.existsByCustomerId(customerId);
        boolean shouldBeDefault = Boolean.TRUE.equals(requestedIsDefault) || !hasExistingAddresses;

        if (shouldBeDefault && hasExistingAddresses) {
            removeDefaultAddressIfExist(customerId);
        }

        return shouldBeDefault;
    }


    @Transactional(readOnly = true)
    public Address getAddressOrThrow(Long addressId, Integer customerId) {
        Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));

        return address;
    }

    @Transactional(readOnly = true)
    public List<Address> getAllAddressesByCustomerId(Integer customerId) {
        return addressRepository.findAllByCustomerId(customerId);
    }

    @Transactional
    public Address updateAddress(Long addressId, Integer customerId, UpdateAddressRequest request) {
        Address address = getAddressOrThrow(addressId, customerId);
        resolveAndHandleDefaultStatus(customerId, request.getIsDefault());
        addressMapper.updateEntityFromRequest(request, address, request.getIsDefault());
        return address;
    }

    @Transactional
    public void deleteAddress(Long addressId, Integer customerId) {
        Address address = getAddressOrThrow(addressId, customerId);
        if (address.getIsDefault()) {
            markAnyExistingAddressAsDefault(addressId, customerId);
        }

        addressRepository.delete(address);
        log.info("Address deleted with ID: {}", addressId);
    }

    private void markAnyExistingAddressAsDefault(Long addressId, Integer customerId) {
        addressRepository.findFirstByCustomerIdAndIdNotOrderByIdDesc(customerId, addressId)
                .ifPresent(nextDefault -> nextDefault.setIsDefault(true));
    }

    @Transactional
    public void setDefaultAddress(Long addressId, Integer customerId) {
        Address address = getAddressOrThrow(addressId, customerId);
        removeDefaultAddressIfExist(customerId);
        address.setIsDefault(true);
        log.info("Default address set to ID: {} for customer {}", addressId, customerId);
    }

    private void removeDefaultAddressIfExist(Integer customerId) {
        addressRepository.clearDefaultAddressByCustomerId(customerId);
    }
}
