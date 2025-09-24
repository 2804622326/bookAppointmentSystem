package com.dailycodework.universalpetcare.service.role;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.repository.RoleRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;


    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName).orElse(null);
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);

    }

    @Override
    public Set<Role> setUserRole(String userType) {
        String normalizedType = normalizeUserType(userType);
        Set<Role> userRoles = new HashSet<>();
        roleRepository.findByName("ROLE_" + normalizedType)
                .ifPresentOrElse(userRoles::add, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.ROLE_NOT_FOUND);
                });
        return userRoles;
    }

    private String normalizeUserType(String userType) {
        if (userType == null) {
            throw new IllegalArgumentException("User type is required");
        }
        String trimmed = userType.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("User type must not be blank");
        }
        return trimmed.toUpperCase(Locale.ENGLISH);
    }
}
