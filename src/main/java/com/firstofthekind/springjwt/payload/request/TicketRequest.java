package com.firstofthekind.springjwt.payload.request;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

public class TicketRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String title;

    @NotBlank
    @Size(max = 500)
    private String description;

    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}