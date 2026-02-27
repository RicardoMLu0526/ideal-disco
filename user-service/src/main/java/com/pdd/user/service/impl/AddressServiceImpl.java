package com.pdd.user.service.impl;

import com.pdd.user.dto.AddressDTO;
import com.pdd.user.vo.AddressVO;
import com.pdd.user.entity.Address;
import com.pdd.user.repository.AddressRepository;
import com.pdd.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public AddressDTO getAddressById(Long id) {
        Optional<Address> addressOptional = addressRepository.findById(id);
        return addressOptional.map(this::convertToDTO).orElse(null);
    }

    @Override
    public List<AddressDTO> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getDefaultAddressByUserId(Long userId) {
        Optional<Address> addressOptional = addressRepository.findByUserIdAndIsDefault(userId, 1);
        return addressOptional.map(this::convertToDTO).orElse(null);
    }

    @Override
    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO) {
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            clearDefaultAddresses(addressDTO.getUserId());
        }

        Address address = new Address();
        address.setUserId(addressDTO.getUserId());
        address.setName(addressDTO.getName());
        address.setPhone(addressDTO.getPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetail(addressDTO.getDetail());
        address.setIsDefault(addressDTO.getIsDefault() != null ? addressDTO.getIsDefault() : 0);

        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        Optional<Address> addressOptional = addressRepository.findById(id);
        if (!addressOptional.isPresent()) {
            throw new IllegalArgumentException("地址不存在");
        }

        Address address = addressOptional.get();

        if (!address.getUserId().equals(addressDTO.getUserId())) {
            throw new IllegalArgumentException("无权修改此地址");
        }

        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            clearDefaultAddresses(addressDTO.getUserId());
        }

        address.setName(addressDTO.getName());
        address.setPhone(addressDTO.getPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetail(addressDTO.getDetail());
        if (addressDTO.getIsDefault() != null) {
            address.setIsDefault(addressDTO.getIsDefault());
        }

        Address updatedAddress = addressRepository.save(address);
        return convertToDTO(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("地址不存在");
        }
        addressRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        if (!addressRepository.existsByIdAndUserId(addressId, userId)) {
            throw new IllegalArgumentException("地址不存在或无权操作");
        }

        clearDefaultAddresses(userId);

        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isPresent()) {
            Address address = addressOptional.get();
            address.setIsDefault(1);
            addressRepository.save(address);
        }
    }

    @Override
    public AddressDTO convertToDTO(Address address) {
        if (address == null) {
            return null;
        }
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setUserId(address.getUserId());
        dto.setName(address.getName());
        dto.setPhone(address.getPhone());
        dto.setProvince(address.getProvince());
        dto.setCity(address.getCity());
        dto.setDistrict(address.getDistrict());
        dto.setDetail(address.getDetail());
        dto.setIsDefault(address.getIsDefault());
        return dto;
    }

    @Override
    public AddressVO convertToVO(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }
        AddressVO vo = new AddressVO();
        vo.setId(addressDTO.getId());
        vo.setUserId(addressDTO.getUserId());
        vo.setName(addressDTO.getName());
        vo.setPhone(addressDTO.getPhone());
        vo.setProvince(addressDTO.getProvince());
        vo.setCity(addressDTO.getCity());
        vo.setDistrict(addressDTO.getDistrict());
        vo.setDetail(addressDTO.getDetail());
        vo.setIsDefault(addressDTO.getIsDefault());
        return vo;
    }

    private void clearDefaultAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        for (Address address : addresses) {
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                address.setIsDefault(0);
                addressRepository.save(address);
            }
        }
    }
}
