package com.group5.ecommerce.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "employee")
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "First name must not be empty")
    @NotNull(message = "First name must be set")
    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    @Column(unique = true)
    private String cardId;

    private String phone;

    private String address;

    @Email(message = "{errors.invalid_email}")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "Password must be set")
    @NotNull(message = "Password must not be null")
    private String password;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @OrderBy(value = "id ASC")
    private List<Role> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName) && Objects.equals(cardId, employee.cardId) && Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, cardId, email);
    }
}
