package ru.practicum.ewm.user.permission.policy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.user.IdUserToSecurityState;

import java.util.List;

@Repository
public interface PermissionPolicyRepository extends JpaRepository<PermissionPolicy, Long> {

    @Query(value = "SELECT up.user_id AS idUser, up.status AS securityState FROM users_permission_policy AS up WHERE user_id IN ?1", nativeQuery = true)
    List<IdUserToSecurityState> getIdsUserAndSecurityState(List<Long> ids);

    boolean existsByUser_Id(long userId);
}
