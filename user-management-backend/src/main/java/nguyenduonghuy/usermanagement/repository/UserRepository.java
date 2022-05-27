package nguyenduonghuy.usermanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import nguyenduonghuy.usermanagement.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Page<User> findAllByIsDeleted(boolean isDeleted, Pageable pageable);

	User findByIdAndIsDeleted(Long id, boolean isDeleted);

	User findByUsernameAndIsDeleted(String username, boolean isDeleted);

	User findByEmailAndIsDeleted(String email, boolean isDeleted);
}
