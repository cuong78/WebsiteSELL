package com.example.demo.initializer;

import com.example.demo.entity.Account;
import com.example.demo.enums.RoleEnum;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
  AccountRepository accountRepository;
    @Autowired
     PasswordEncoder passwordEncoder;


    public DataInitializer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu dữ liệu đã tồn tại thì không cần khởi tạo lại
        if (accountRepository.count() == 0) {

            // Tạo tài khoản Admin
            Account admin = new Account();
            admin.setFullName("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456")); // Mã hóa mật khẩu
            admin.setRoleEnum(RoleEnum.ADMIN);

            // Tạo tài khoản Customer
            Account customer = new Account();
            customer.setFullName("Customer");
            customer.setEmail("customer@gmail.com");
            customer.setUsername("customer");
            customer.setPassword(passwordEncoder.encode("123456"));
            customer.setRoleEnum(RoleEnum.CUSTOMER);

            // Tạo tài khoản Staff
            Account staff = new Account();
            staff.setFullName("Staff");
            staff.setEmail("staff@gmail.com");
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("123456"));
            staff.setRoleEnum(RoleEnum.STAFF);

            // Lưu các tài khoản vào database
            accountRepository.save(admin);
            accountRepository.save(customer);
            accountRepository.save(staff);
        }
    }
}
