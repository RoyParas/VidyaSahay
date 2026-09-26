package com.vidyasahay.vidyasahay.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vidyasahay.vidyasahay.entity.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    @Query("select distinct a.state from Address a order by a.state")
    List<String> findDistinctStates();

    @Query("select distinct a.district from Address a where lower(a.state) = lower(:state) order by a.district")
    List<String> findDistinctDistrictsByState(@Param("state") String state);

    @Query("select a from Address a where lower(a.state) = lower(:state) and lower(a.district) = lower(:district) order by lower(a.city), a.id")
    List<Address> findCitiesByStateAndDistrict(
            @Param("state") String state,
            @Param("district") String district);
}
