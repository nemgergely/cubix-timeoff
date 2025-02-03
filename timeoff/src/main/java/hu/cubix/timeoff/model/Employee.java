package hu.cubix.timeoff.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "employee")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private Employee manager;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Employee> subordinates = new ArrayList<>();

    @OneToMany(mappedBy = "requester", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TimeoffRequest> requests = new ArrayList<>();

    @OneToMany(mappedBy = "approver", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TimeoffRequest> approvals = new ArrayList<>();

    public Employee(String name, String username, String password, List <String> roles, Employee manager) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.manager = manager;
    }
}
