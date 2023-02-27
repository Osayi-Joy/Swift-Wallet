package com.zurum.lanefinance.entity;

import com.zurum.lanefinance.constants.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roles", uniqueConstraints= @UniqueConstraint(columnNames = "name"))
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private RoleEnum name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(getName(), role.getName()).append(getUsers(), role.getUsers()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(getName()).append(getUsers()).toHashCode();
    }
}
