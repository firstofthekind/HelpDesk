package com.firstofthekind.springjwt.controllers;

import com.firstofthekind.springjwt.models.*;
import com.firstofthekind.springjwt.payload.request.SignupRequest;
import com.firstofthekind.springjwt.payload.request.TicketRequest;
import com.firstofthekind.springjwt.payload.response.MessageResponse;
import com.firstofthekind.springjwt.repository.RoleRepository;
import com.firstofthekind.springjwt.repository.StatusRepository;
import com.firstofthekind.springjwt.repository.TicketRepository;
import com.firstofthekind.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/users/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String showUserList() {
        List<User> user = userRepository.findAll();
        List<String> out = user.stream().map(User::toString).collect(Collectors.toList());

        return "Список пользователей:\n" + out;
    }


    @RequestMapping(value = "users/{id}/getrights", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOperatorRights(
            @PathVariable(value = "id") Long id) {
        User user = userRepository.getOne(id);
        if (user.getRoles().stream().map(Role::getShortName).collect(Collectors.toList()).contains("OPERATOR")) {
            return ResponseEntity.ok("У данного пользователя с ID " + user.getId() + " уже есть права оператора.");
        }
        Set<Role> roles = user.getRoles();
        roles.add(roleRepository.findByName(ERole.ROLE_OPERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok("Пользователю с ID " + user.getId() + " назначены права оператора.");
    }
}
