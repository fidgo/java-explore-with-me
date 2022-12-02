package ru.practicum.ewm.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(" SELECT CASE WHEN count(u) > 0 THEN true ELSE false END FROM User u WHERE" +
            " u.name = :name OR u.email = :email")
    boolean existsByNameOrByEmail(@Param("name") String name, @Param("email") String email);

    List<User> findAllByIdIn(List<Long> ids, Pageable pageable);
}
