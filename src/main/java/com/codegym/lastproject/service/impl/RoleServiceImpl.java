package com.codegym.lastproject.service.impl;

import com.codegym.lastproject.model.Role;
import com.codegym.lastproject.model.RoleName;
import com.codegym.lastproject.repository.RoleRepository;
import com.codegym.lastproject.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RoleServiceImpl implements RoleService{
    @Autowired
    RoleRepository roleRepository;


    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findByName(RoleName roleName) {
        return roleRepository.findByName(roleName);
    }
}