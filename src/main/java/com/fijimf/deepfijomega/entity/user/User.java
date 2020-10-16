package com.fijimf.deepfijomega.entity.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// id                    BIGSERIAL PRIMARY KEY,
//         username              VARCHAR(64) NOT NULL,
//         password              VARCHAR(32) NOT NULL,
//         email                 VARCHAR(96) NOT NULL,
//         locked                BOOLEAN     NOT NULL,
//         activated             BOOLEAN     NOT NULL,
//         expire_credentials_at TIMESTAMP   NULL
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "expire_credentials_at")
    private LocalDateTime expireCredentialsAt;// TIMESTAMP NULL

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="user_role",
            joinColumns = @JoinColumn( name="user_id"),
            inverseJoinColumns = @JoinColumn( name="role_id")
    )
    private List<Role> roles;

    public User() {
    }

    public User(String username, String password, String email) {
        this.id = 0L;
        this.username = username;
        this.password = password;
        this.email = email;
        this.activated = false;
        this.locked = false;
        this.expireCredentialsAt = null;
        this.roles = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        //Accounts never expire
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (expireCredentialsAt!=null){
            return (LocalDateTime.now().isAfter(expireCredentialsAt));
        } else {
            return true;
        }
    }

    @Override
    public boolean isEnabled() {
        return isActivated() && !isLocked();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public LocalDateTime getExpireCredentialsAt() {
        return expireCredentialsAt;
    }

    public void setExpireCredentialsAt(LocalDateTime expireCredentialsAt) {
        this.expireCredentialsAt = expireCredentialsAt;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}