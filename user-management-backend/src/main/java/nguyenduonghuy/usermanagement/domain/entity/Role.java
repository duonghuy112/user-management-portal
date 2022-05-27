package nguyenduonghuy.usermanagement.domain.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "role")
@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Role implements Serializable {

	public static final Integer USER = 1;
	public static final Integer HR = 2;
	public static final Integer MANAGER = 3;
	public static final Integer ADMIN = 4;
	public static final Integer SUPER_ADMIN = 5;
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "roles_authorities", 
			joinColumns = @JoinColumn(name = "role_id"), 
			inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private List<Authority> authorities;
}
