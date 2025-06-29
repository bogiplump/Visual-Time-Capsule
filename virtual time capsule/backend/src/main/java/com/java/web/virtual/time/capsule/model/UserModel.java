package com.java.web.virtual.time.capsule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Getter
@Setter
@Table(name = "users")
public class UserModel implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "family_name")
    private String lastName;

    @Column(name = "created_at")
    private LocalDate creationDate;

    private String username;
    private String password;
    private String email;

    @OneToMany(mappedBy = "creator")
    private Set<Goal> goals;

    @OneToMany(mappedBy = "creator")
    private Set<Memory> memoryEntries;

    @OneToMany(mappedBy = "creator")
    private Set<Capsule> capsules;

    private static int count = 0;

    @Override
    public boolean equals(Object o) {
        count++;
        log.info(String.valueOf(count));
        if (this == o) return true;
        if (o == null || (getClass() != o.getClass() && !(o instanceof HibernateProxy))) return false;
        UserModel userModel = (UserModel) (o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getImplementation() : o);
        log.info("UserModel is equal {}", Objects.equals(id, userModel.id));
        return id != null && Objects.equals(id, userModel.id);
    }

    @Override
    public int hashCode() {
        // Use a constant or just the ID for hashCode for entities.
        // Avoid accessing lazy-loaded fields.
        return getClass().hashCode(); // Good default for entities, or a constant prime number
        // OR return id != null ? id.hashCode() : 0; (if id is generated quickly)
        // For new entities, id might be null, so getClass().hashCode() is safer.
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
