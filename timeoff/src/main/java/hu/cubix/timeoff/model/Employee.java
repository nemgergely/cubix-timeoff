package hu.cubix.timeoff.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee")
@NoArgsConstructor
@Getter
@Setter
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

    public Employee(String name, Employee manager,
                    List<Employee> subordinates,
                    List<TimeoffRequest> requests) {
        this.name = name;
        this.manager = manager;
        this.subordinates = subordinates;
        this.requests = requests;
    }

    public Employee(String name, Employee manager) {
        this.name = name;
        this.manager = manager;
    }
}
