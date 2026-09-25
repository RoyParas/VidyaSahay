// package com.vidyasahay.vidyasahay.repository;

// import java.util.Optional;
// import java.util.UUID;

// import org.springframework.data.jpa.repository.EntityGraph;
// import org.springframework.data.jpa.repository.JpaRepository;

// import com.vidyasahay.vidyasahay.entity.User;

// public interface UserRepository extends JpaRepository<User, UUID>{
	
// 	@EntityGraph(attributePaths = "role")
// 	Optional<User> findByEmailIgnoreCase(String email);
	
//     boolean existsByEmailIgnoreCase(String email);

// 	boolean existsByMobile(String mobile);
// }

package com.vidyasahay.vidyasahay.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMobile(String mobile);

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            UUID userId
    );

    boolean existsByMobileAndIdNot(
            String mobile,
            UUID userId
    );
}