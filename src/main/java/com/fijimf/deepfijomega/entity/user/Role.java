package com.fijimf.deepfijomega.entity.user;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;


    @Entity
    @Table(name = "role")
    public class Role implements GrantedAuthority {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(name = "role")
        private String role;

        public Role() {
        }

        public Role(String role) {
            this.id=0L;
            this.role = role;
        }

        @Override
        public String getAuthority() {
            return role;
        }

        public static final Role ANONYMOUS = new Role("ANONYMOUS");
    }