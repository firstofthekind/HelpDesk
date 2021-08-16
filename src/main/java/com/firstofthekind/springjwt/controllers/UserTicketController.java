package com.firstofthekind.springjwt.controllers;

import com.firstofthekind.springjwt.models.EStatus;
import com.firstofthekind.springjwt.models.Status;
import com.firstofthekind.springjwt.models.Ticket;
import com.firstofthekind.springjwt.models.User;
import com.firstofthekind.springjwt.payload.request.TicketRequest;
import com.firstofthekind.springjwt.payload.response.MessageResponse;
import com.firstofthekind.springjwt.repository.StatusRepository;
import com.firstofthekind.springjwt.repository.TicketRepository;
import com.firstofthekind.springjwt.repository.UserRepository;
import com.firstofthekind.springjwt.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ticket")
public class UserTicketController {
    @Autowired
    StatusRepository statusRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketRequest ticketRequest, Principal principal) {
        // Create new ticket account
        Ticket ticket = new Ticket(ticketRequest.getTitle(),
                ticketRequest.getDescription());

        String strStatus = ticketRequest.getStatus();
        Status status;
        ticket.setUser(userRepository.findUserByUsername(principal.getName()));

        if (Objects.equals(strStatus, "sent")) {
            status = statusRepository.findByName(EStatus.STATUS_SENT)
                    .orElseThrow(() -> new RuntimeException("Error: Status is not found. "));
            ticket.setStatus(status);
            ticketRepository.save(ticket);
            return ResponseEntity.ok(new MessageResponse("Заявка успешно создана и отправлена на рассмотрение!" +
                    " ID заявки: " + ticket.getId()));
        } else {
            status = statusRepository.findByName(EStatus.STATUS_DRAFT)
                    .orElseThrow(() -> new RuntimeException("Error: Status is not found."));
            ticket.setStatus(status);
            ticketRepository.save(ticket);
            return ResponseEntity.ok(new MessageResponse("Заявка сохранена как черновик" + " ID заявки: " + ticket.getId()));
        }
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public String showTicket( @PathVariable(value = "id") Long id, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        Ticket ticket = ticketRepository.findById(id).get();
        if (!ticket.getUser().getId().equals(user.getId())) {
            return "Просматривать можно только свои заявки.";
        }
        return ticket.toFullString();
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER')")
    public List<String> showTicketList(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        List<Ticket> ticket = ticketRepository.findAllByUser(user);
        List<String> out = ticket.stream().map(Ticket::toString).collect(Collectors.toList());
        out.add(0, "Для пользователя " + user.getUsername() + " существуют следующие заявки:");
        return out;
    }

    @RequestMapping(value = "/{id}/send", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> draftToSent(
            @PathVariable(value = "id") Long id, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        Ticket ticket = ticketRepository.findById(id).get();
        if (!ticket.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok("Отправить на рассмотрение можно только свои заявки.");
        }
        if (!ticket.getStatus().getShortName().equals("DRAFT")) {
            return ResponseEntity.ok("Заявка не в статусе \"Черновик\". Отправить на рассмотрение невозможно.");
        }
        ticket.setStatus(statusRepository.findByName(EStatus.STATUS_SENT)
                .orElseThrow(() -> new RuntimeException("Error: Status is not found.")));

        ticketRepository.save(ticket);
        return ResponseEntity.ok("Заявка отправлена на рассмотрение.");
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> editTicket(
            @PathVariable(value = "id") Long id, @RequestBody TicketRequest ticketRequest, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        Ticket ticket = ticketRepository.findById(id).get();
        if (!ticket.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok("Редактировать можно только свои заявки.");
        }
        if (!ticket.getStatus().getShortName().equals("DRAFT")) {
            return ResponseEntity.ok("Заявка не в статусе \"Черновик\". Редактирование невозможно.");
        }
        ticket.setTitle(ticketRequest.getTitle());
        ticket.setDescription(ticketRequest.getDescription());
        String strStatus = ticketRequest.getStatus();
        Status status;

        if (Objects.equals(strStatus, "sent")) {
            status = statusRepository.findByName(EStatus.STATUS_SENT)
                    .orElseThrow(() -> new RuntimeException("Error: Status is not found. "));
            ticket.setStatus(status);
            ticketRepository.save(ticket);
            return ResponseEntity.ok("Заявка успешно отредактирована и отправлена на рассмотрение!" +
                    " ID заявки: " + ticket.getId());
        } else {
            status = statusRepository.findByName(EStatus.STATUS_DRAFT)
                    .orElseThrow(() -> new RuntimeException("Error: Status is not found."));
            ticket.setStatus(status);
            ticketRepository.save(ticket);
            return ResponseEntity.ok("Заявка успешно отредактирована! ID заявки: " + ticket.getId());
        }
    }
}