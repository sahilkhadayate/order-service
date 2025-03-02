package org.swiggy.order.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Username is required")
    @Column(name = "username", unique = true)
    private String username;

    public User(String username, String encodedPassword) {
        this.username = username;
        this.password = encodedPassword;
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public boolean verifyUser(String userName) {
        return Objects.equals(this.username, userName);
    }
}
