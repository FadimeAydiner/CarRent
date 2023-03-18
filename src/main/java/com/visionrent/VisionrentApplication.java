package com.visionrent;

import com.visionrent.domain.Role;
import com.visionrent.domain.User;
import com.visionrent.domain.enums.RoleType;
import com.visionrent.repository.RoleRepository;
import com.visionrent.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@SpringBootApplication
public class VisionrentApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisionrentApplication.class, args);
		//codes from me
		//last try


	}

}

@Component
@AllArgsConstructor
class DemoCommentLineRunner implements CommandLineRunner{

	RoleRepository roleRepository;
	PasswordEncoder passwordEncoder;
	UserRepository userRepository;
	@Override
	public void run(String... args) throws Exception {
		if(!roleRepository.findByType(RoleType.ROLE_CUSTOMER).isPresent()){
			Role roleCustomer=new Role();
			roleCustomer.setType(RoleType.ROLE_CUSTOMER);
			roleRepository.save(roleCustomer);
		}
		if(!roleRepository.findByType(RoleType.ROLE_ADMIN).isPresent()){
			Role roleAdmin=new Role();
			roleAdmin.setType(RoleType.ROLE_ADMIN);
			roleRepository.save(roleAdmin);
		}
		if(!userRepository.findByEmail("superadmin@gmail.com").isPresent()){
			User admin=new User();
			Role role=roleRepository.findByType(RoleType.ROLE_ADMIN).get();
			admin.setRoles(new HashSet<>(Collections.singletonList(role)));
			admin.setAddress("super user address");
			admin.setEmail("superadmin@gmail.com");
			admin.setFirstName("superadminfirstname");
			admin.setLastName("superadminlastname");
			admin.setZipCode("123456");
			admin.setPassword(passwordEncoder.encode("Ankara06"));
			admin.setPhoneNumber("(541) 317-8828");
			userRepository.save(admin);
		}

		if(!userRepository.findByEmail("supercustomer@gmail.com").isPresent()){
			User customer=new User();
			Role role=roleRepository.findByType(RoleType.ROLE_CUSTOMER).get();
			customer.setRoles(new HashSet<>(Collections.singletonList(role)));
			customer.setAddress("super customer address");
			customer.setEmail("supercustomer@gmail.com");
			customer.setFirstName("supercustomerfirstname");
			customer.setLastName("supercustomerlastname");
			customer.setZipCode("987654");
			customer.setPassword(passwordEncoder.encode("Ankara06"));
			customer.setPhoneNumber("(541) 317-8828");
			userRepository.save(customer);
		}


	}
}
