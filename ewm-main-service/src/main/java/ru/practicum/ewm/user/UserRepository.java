package ru.practicum.ewm.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(" select case when count(u) > 0 then true else false end from User u where u.name = :name or u.email = :email")
    boolean existsByNameOrByEmail(@Param("name") String name, @Param("email") String email);

    List<User> findAllByIdIn(List<Long> ids, Pageable pageable);
}
