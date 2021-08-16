package com.firstofthekind.springjwt.repository;

import com.firstofthekind.springjwt.models.EStatus;
import com.firstofthekind.springjwt.models.Status;
import com.firstofthekind.springjwt.models.Ticket;
import com.firstofthekind.springjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTitle(String title);

    Optional<Ticket> findById(Long id);

    Optional<Ticket> findTicketByStatus(Status status);

    Boolean existsByStatus(Status status);

    Boolean existsByUser(User user);

    List<Ticket> findAllByUser(User user);

    List<Ticket> findAllByStatus(Status status);


}
