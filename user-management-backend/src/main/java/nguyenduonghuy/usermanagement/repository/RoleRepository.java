package nguyenduonghuy.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nguyenduonghuy.usermanagement.domain.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{

}
