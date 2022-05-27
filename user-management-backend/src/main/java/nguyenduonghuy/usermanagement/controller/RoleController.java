package nguyenduonghuy.usermanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nguyenduonghuy.usermanagement.domain.entity.Role;
import nguyenduonghuy.usermanagement.service.RoleService;

@RestController
@RequestMapping("/role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@GetMapping
	public ResponseEntity<List<Role>> getAll() {
		return new ResponseEntity<List<Role>>(roleService.getAll(), HttpStatus.OK);
	}
}
