package com.example.shopappangular.services.Impl;

import com.example.shopappangular.models.Role;
import com.example.shopappangular.repositories.RoleRepository;
import com.example.shopappangular.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}

