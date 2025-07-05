package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.Admin;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.AdminRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import com.dailycodework.universalpetcare.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminFactory {
    private final AdminRepository adminRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;


    public Admin createAdmin(RegistrationRequest request) {
        Admin admin = new Admin();
        admin.setRoles(roleService.setUserRole("ADMIN"));
        userAttributesMapper.setCommonAttributes(request, admin);
        return adminRepository.save(admin);
    }
}
