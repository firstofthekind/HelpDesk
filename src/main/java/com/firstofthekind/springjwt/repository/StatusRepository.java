package com.firstofthekind.springjwt.repository;


import com.firstofthekind.springjwt.models.EStatus;
import com.firstofthekind.springjwt.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Optional<Status> findByName(EStatus name);
}
