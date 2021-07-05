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
        private String roleName;

        public Role() {
        }

        public Role(String roleName) {
            this.id=0L;
            this.roleName = roleName;
        }

        @Override
        public String getAuthority() {
            return roleName;
        }

        public static final Role ANONYMOUS = new Role("ANONYMOUS");
    }