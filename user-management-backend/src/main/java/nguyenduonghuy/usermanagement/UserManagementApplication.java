package nguyenduonghuy.usermanagement;

import static nguyenduonghuy.usermanagement.constant.FileConstant.USER_FOLDER;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManagementApplication.class, args);
		new File(USER_FOLDER).mkdirs();
	}
}