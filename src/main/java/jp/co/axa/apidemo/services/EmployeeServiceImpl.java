package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Cacheable("employees") // Cache the list of all employees
    public List<Employee> retrieveEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }

    @Cacheable(value = "employee", key = "#employeeId") // Cache a single employee by their ID
    public Employee getEmployee(Long employeeId) {
        Optional<Employee> optEmp = employeeRepository.findById(employeeId);
        if (!optEmp.isPresent()) {
            throw new RuntimeException("Employee not found with id: " + employeeId);
        }
        return optEmp.get();
    }

    @CacheEvict(value = {"employee", "employees"}, allEntries = true) // Evict all caches when a new employee is saved
    public void saveEmployee(Employee employee){
        employeeRepository.save(employee);
    }

    @CacheEvict(value = {"employee", "employees"}, allEntries = true) // Evict all caches when an employee is deleted
    public void deleteEmployee(Long employeeId){
        employeeRepository.deleteById(employeeId);
    }

    @CachePut(value = "employee", key = "#employee.id") // Update the 'employee' cache
    @CacheEvict(value = "employees", allEntries = true) // Evict the 'employees' list cache
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
}