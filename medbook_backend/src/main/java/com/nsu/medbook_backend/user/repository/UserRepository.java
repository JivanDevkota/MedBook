package com.nsu.medbook_backend.user.repository;

import com.nsu.medbook_backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsername(String username);

    @Query("""
            select u from User u
            where lower(u.email) like lower(concat('%', :term, '%'))
               or lower(u.firstName) like lower(concat('%', :term, '%'))
               or lower(u.lastName) like lower(concat('%', :term, '%'))
            """)
    Page<User> searchByTerm(@Param("term") String term, Pageable pageable);
}
