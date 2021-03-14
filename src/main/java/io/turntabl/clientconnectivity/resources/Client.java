package io.turntabl.clientconnectivity.resources;

import javax.persistence.*;

@Table
@Entity(name="Client")
public class Client {
    @Id
    @SequenceGenerator(
            name= "client_sequence",
            sequenceName = "client_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "client_sequence"
    )
    @Column(
            nullable=false,
            updatable = false
    )
    private Long id;
    @Column(
            nullable=false
    )
    private String email;
    @Column(
            nullable=false
    )
    private String password;
    @Column(
            nullable=false
    )
    private String name;

//    public Client(Long id, String email, String password, String name) {
//        this.id = id;
//        this.email = email;
//        this.password = password;
//        this.name = name;
//    }

    public Client() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
