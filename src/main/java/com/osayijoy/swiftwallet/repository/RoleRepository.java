package com.osayijoy.swiftwallet.repository;

import com.osayijoy.swiftwallet.constants.RoleEnum;
import com.osayijoy.swiftwallet.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

Role findRoleByName(RoleEnum roleEnum);
}
