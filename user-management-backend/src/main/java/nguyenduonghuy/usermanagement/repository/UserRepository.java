package nguyenduonghuy.usermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import nguyenduonghuy.usermanagement.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select u from User u where u.id != ?1 and u.isDeleted = ?2 "
			+ "and (u.username like %?3% or u.firstName like %?3% or u.lastName like %?3% or u.email like %?3%)")
	Page<User> findAllPaging(Long id, boolean isDeleted, String keyword, Pageable pageable);

	Page<User> findAllByIsDeleted(boolean isDeleted, Pageable pageable);

	User findByIdAndIsDeleted(Long id, boolean isDeleted);

	User findByUsernameAndIsDeleted(String username, boolean isDeleted);

	User findByEmailAndIsDeleted(String email, boolean isDeleted);
}
