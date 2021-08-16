package com.firstofthekind.springjwt.models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String title;

    @NotBlank
    @Size(max = 500)
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "ticket_owner",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "ticket_status",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "status_id"))
    private Status status;

    public Ticket(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Ticket() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String toString() {
        return "ID заявки: " + this.getId() + ". Заголовок: " + this.getTitle() +
                ". Статус: " + this.getStatus().getShortName();
    }

    public String toFullString() {
        return "ID заявки: " + this.getId() + ". Статус: " + this.getStatus().getShortName() +
                ". Заголовок: " + this.getTitle() + ". Описание: " + this.getDescription();
    }

}