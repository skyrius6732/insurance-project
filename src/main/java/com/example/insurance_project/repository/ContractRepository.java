
package com.example.insurance_project.repository;

import com.example.insurance_project.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
