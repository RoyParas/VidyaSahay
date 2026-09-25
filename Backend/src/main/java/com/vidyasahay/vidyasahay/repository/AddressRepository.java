package com.vidyasahay.vidyasahay.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vidyasahay.vidyasahay.entity.Address;

public interface AddressRepository extends JpaRepository<Address,UUID> {

}