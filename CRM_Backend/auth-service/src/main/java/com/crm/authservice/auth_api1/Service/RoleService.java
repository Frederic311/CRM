package com.crm.authservice.auth_api1.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.authservice.auth_api1.Repository.RoleRepository;
import com.crm.authservice.auth_api1.models.Role;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
    }
}
