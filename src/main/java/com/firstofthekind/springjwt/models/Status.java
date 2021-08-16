package com.firstofthekind.springjwt.models;


import javax.persistence.*;

@Entity
@Table(name = "statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EStatus name;

    public Status() {

    }

    public Status(EStatus name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EStatus getName() {
        return name;
    }

    public String getShortName() {
        return name.toString().substring(7);
    }

    public void setName(EStatus name) {
        this.name = name;
    }
}
