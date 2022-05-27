package nguyenduonghuy.usermanagement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nguyenduonghuy.usermanagement.domain.entity.Role;
import nguyenduonghuy.usermanagement.repository.RoleRepository;
import nguyenduonghuy.usermanagement.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public List<Role> getAll() {
		return roleRepository.findAll();
	}
}
