package ru.practicum.ewm.user;

import ru.practicum.ewm.user.permission.policy.StateSecurity;

public interface IdUserToSecurityState {
    Long getIdUser();

    StateSecurity getSecurityState();
}
