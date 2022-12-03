package ru.practicum.ewm.user.permission.policy;

import ru.practicum.ewm.user.User;

public class PermissionPolicyMapper {
    public static PermissionPolicy toPermissionPolicy(User userById, StateSecurity state) {
        PermissionPolicy permissionPolicy = new PermissionPolicy();
        permissionPolicy.setUser(userById);
        permissionPolicy.setStateSecurity(state);

        return permissionPolicy;
    }
}
