package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.lang.RuntimeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee1;
    private Employee employee2;

    @Before
    public void setUp() {
        employee1 = new Employee(1L, "John Doe", 50000, "IT");
        employee2 = new Employee(2L, "Jane Smith", 60000, "HR");
    }

    @Test
    public void retrieveEmployees_shouldReturnAllEmployees() {
        // Arrange: Define the mock's behavior
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        // Act: Call the method to be tested
        List<Employee> employees = employeeService.retrieveEmployees();

        // Assert: Verify the result
        assertEquals(2, employees.size());
        verify(employeeRepository, times(1)).findAll(); // Verify that findAll() was called once
    }

    @Test
    public void getEmployee_shouldReturnEmployeeWhenFound() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        // Act
        Employee foundEmployee = employeeService.getEmployee(1L);

        // Assert
        assertEquals("John Doe", foundEmployee.getName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    public void saveEmployee_shouldCallSaveRepositoryMethod() {
        // Act
        employeeService.saveEmployee(employee1);

        // Assert
        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    public void getEmployee_shouldThrowExceptionWhenNotFound_tryCatch() {
        // Arrange: Set up the scenario where the employee is not found
        long nonExistentId = 999L;
        String expectedMessage = "Employee not found with id: " + nonExistentId;
        when(employeeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            employeeService.getEmployee(nonExistentId);
            // If no exception is thrown, the test should fail
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (RuntimeException e) {
            // Assert: Verify that the exception message is as expected
            assertEquals(expectedMessage, e.getMessage());
            verify(employeeRepository, times(1)).findById(nonExistentId);
        }
    }
}