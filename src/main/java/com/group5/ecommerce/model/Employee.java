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

    private String phone;

    private String address;

    @Email(message = "{errors.invalid_email}")
    @Column(unique = true, length = 200)
    private String email;

    @NotEmpty(message = "Password must be set")
    @NotNull(message = "Password must not be null")
    private String password;
}
