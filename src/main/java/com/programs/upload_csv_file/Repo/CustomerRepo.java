package com.programs.upload_csv_file.Repo;

import com.programs.upload_csv_file.Entiy.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {


    Optional<Customer> findByPhone1(String phone1);
}
