package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import com.dailycodework.universalpetcare.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;

    @GetMapping("/all-roles")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/role/get-by-id/role")
    public Role getRoleById(Long id) {
        return roleService.getRoleById(id);
    }

    @GetMapping("/role/get-by-name")
    public Role getRoleByName(String roleName) {
        return roleService.getRoleByName(roleName);
    }
}
