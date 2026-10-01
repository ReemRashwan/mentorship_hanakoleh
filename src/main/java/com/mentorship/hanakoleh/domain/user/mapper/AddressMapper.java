package com.mentorship.hanakoleh.domain.user.mapper;

import com.mentorship.hanakoleh.domain.user.dto.AddressDetailsDto;
import com.mentorship.hanakoleh.domain.user.dto.CreateAddressRequest;
import com.mentorship.hanakoleh.domain.user.dto.GetCustomerAddressesResponse;
import com.mentorship.hanakoleh.domain.user.dto.UpdateAddressRequest;
import com.mentorship.hanakoleh.domain.user.model.Address;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressMapper {

    public AddressDetailsDto toDetailsDto(Address address) {
        return new AddressDetailsDto(
                address.getId(),
                address.getStreetAddress(),
                address.getBuildingNumber(),
                address.getFloor(),
                address.getApartmentNumber(),
                address.getLandmark(),
                address.getDistrictName(),
                address.getIsDefault(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }

    public void updateEntityFromRequest(UpdateAddressRequest request, Address address, boolean isDefault) {
        address.setStreetAddress(request.getStreetAddress());
        address.setBuildingNumber(request.getBuildingNumber());
        address.setFloor(request.getFloor());
        address.setApartmentNumber(request.getApartmentNumber());
        address.setLandmark(request.getLandmark());
        address.setDistrictName(request.getDistrictName());
        address.setIsDefault(isDefault);
    }

    public Address toEntity(CreateAddressRequest request, Customer customer) {
        return Address.builder()
                .customer(customer)
                .streetAddress(request.getStreetAddress())
                .buildingNumber(request.getBuildingNumber())
                .floor(request.getFloor())
                .apartmentNumber(request.getApartmentNumber())
                .landmark(request.getLandmark())
                .districtName(request.getDistrictName())
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .build();
    }

    public GetCustomerAddressesResponse toGetCustomerAddressesResponse(List<Address> addresses) {
        return GetCustomerAddressesResponse.builder().data(addresses.stream().map(this::toDetailsDto).toList()).build();
    }
}