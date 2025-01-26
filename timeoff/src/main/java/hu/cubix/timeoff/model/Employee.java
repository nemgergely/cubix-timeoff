package hu.cubix.timeoff.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private Employee manager;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Employee> subordinates = new ArrayList<>();

    @OneToMany(mappedBy = "requester", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TimeoffRequest> requests = new ArrayList<>();

    @OneToMany(mappedBy = "approver", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TimeoffRequest> approvals = new ArrayList<>();
}
