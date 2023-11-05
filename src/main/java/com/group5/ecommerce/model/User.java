package com.group5.ecommerce.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "First name is required")
    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private String address;

    @Pattern(regexp = "^(Male|Femaile|Other)$", message = "Invalid gender value")
    private String gender;

    private String phone;

    @NotEmpty(message = "Email is required")
    @Column(nullable = false, unique = true)
    @Email(message = "{errors.invalid_email}")
    private String email;

    private String password;

    private Integer accumulatePoints;

    private String cardId;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @OrderBy(value = "id ASC")
    private List<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private UserStore store;

    public User(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.roles = user.getRoles();
    }

    public User() {

    }
}
