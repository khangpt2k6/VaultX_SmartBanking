package com.bankmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    
    @NotNull(message = "Role name is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false, unique = true)
    private RoleName roleName;
    
    @Column(name = "description")
    private String description;

    public enum RoleName {
        ROLE_ADMIN, ROLE_MANAGER, ROLE_TELLER, ROLE_CUSTOMER
    }

    // Default constructor
    public Role() {
    }

    // Constructor with role name
    public Role(RoleName roleName) {
        this.roleName = roleName;
    }

    // Constructor with all fields
    public Role(RoleName roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    // Getters and Setters
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName=" + roleName +
                ", description='" + description + '\'' +
                '}';
    }
}
