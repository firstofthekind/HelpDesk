package com.firstofthekind.springjwt.controllers;

import com.firstofthekind.springjwt.models.EStatus;
import com.firstofthekind.springjwt.models.Ticket;
import com.firstofthekind.springjwt.models.User;
import com.firstofthekind.springjwt.repository.StatusRepository;
import com.firstofthekind.springjwt.repository.TicketRepository;
import com.firstofthekind.springjwt.repository.UserRepository;
import com.firstofthekind.springjwt.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/operator/ticket")
public class OperatorTicketController {
    @Autowired
    StatusRepository statusRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('OPERATOR')")
    public List<String> showTicketList(Principal principal) {
        List<Ticket> ticket = ticketRepository.findAllByStatus(statusRepository.findByName(EStatus.STATUS_SENT)
                .orElseThrow(() -> new RuntimeException("Error: Status is not found.")));
        List<String> out = ticket.stream().map(Ticket::toString).collect(Collectors.toList());
        out.add(0, "Заявки, отправленные на рассмотрение:");
        return out;
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR')")
    public String showTicket( @PathVariable(value = "id") Long id) {
        Ticket ticket = ticketRepository.findById(id).get();
        return ticket.toFullString();
    }

    @RequestMapping(value = "/{id}/accept", method = RequestMethod.POST)
    @PreAuthorize("hasRole('OPERATOR')")
    public String ticketAccept(
            @PathVariable(value = "id") Long id) {
        Ticket ticket = ticketRepository.findById(id).get();
        if (!ticket.getStatus().getShortName().equals("SENT")) {
            return "Заявка не в статусе \"На рассмотрении\". Изменить статус невоможно.";
        }
        ticket.setStatus(statusRepository.findByName(EStatus.STATUS_ACCEPTED)
                .orElseThrow(() -> new RuntimeException("Error: Status is not found.")));

        ticketRepository.save(ticket);
        return "Заявка ID " + ticket.getId() + " принята.";
    }


    @RequestMapping(value = "/{id}/decline", method = RequestMethod.POST)
    @PreAuthorize("hasRole('OPERATOR')")
    public String ticketDecline(
            @PathVariable(value = "id") Long id) {
        Ticket ticket = ticketRepository.findById(id).get();
        if (!ticket.getStatus().getShortName().equals("SENT")) {
            return "Заявка не в статусе \"На рассмотрении\". Изменить статус невоможно.";
        }
        ticket.setStatus(statusRepository.findByName(EStatus.STATUS_DECLINED)
                .orElseThrow(() -> new RuntimeException("Error: Status is not found.")));

        ticketRepository.save(ticket);
        return "Заявка ID " + ticket.getId() + " отклонена.";
    }
}
