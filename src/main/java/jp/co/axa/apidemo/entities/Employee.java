package jp.co.axa.apidemo.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EMPLOYEE")
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name="EMPLOYEE_NAME")
    @Getter
    @Setter
    private String name;

    @Column(name="EMPLOYEE_SALARY")
    @Getter
    @Setter
    private Integer salary;

    @Column(name="DEPARTMENT")
    @Getter
    @Setter
    private String department;

    // Constructor for creating a new employee
    public Employee(String name, Integer salary, String department) {
        this.name = name;
        this.salary = salary;
        this.department = department;
    }
    // Constructor for updating an existing employee
    public Employee(Long id, String name, Integer salary, String department) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.department = department;
    }
}
