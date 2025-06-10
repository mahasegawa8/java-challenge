package jp.co.axa.apidemo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.junit.Before;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Before
    public void setUp() {
        employeeRepository.deleteAll();
    }

    // Test case for getting all employees (public access)
    @Test
    public void getEmployees_shouldReturnAllEmployees() throws Exception {
        // Use the POST endpoint to create a new employee, authenticating as admin.
        Employee newEmployee = new Employee(null, "Test Employee", 80000, "Engineering");

        mockMvc.perform(post("/api/v1/employees")
                .with(httpBasic("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)));

        // Perform the GET request and check the result.
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].name").value("Test Employee"));
    }

    // Test case for getting an employee by ID (invalid ID)
    @Test
    public void getEmployeeById_shouldReturnNotFoundForInvalidId() throws Exception {
        // Act & Assert: GET endpoints are public, so no auth needed here.
        mockMvc.perform(get("/api/v1/employees/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    // Test case for getting an employee by ID (valid ID)
    @Test
    public void getEmployeeById_shouldReturnCorrectEmployee() throws Exception {
        // Arrange: Save an employee directly using the service to get a valid ID.
        Employee employee = new Employee(null, "Find Me", 55000, "QA");
        employeeService.saveEmployee(employee);

        // Act & Assert: GET is public, no auth needed.
        mockMvc.perform(get("/api/v1/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Find Me"))
                .andExpect(jsonPath("$.department").value("QA"));
    }

    // Test case for saving a new employee
    @Test
    public void saveEmployee_shouldCreateEmployeeAndReturnSuccessMessage() throws Exception {
        // Arrange
        Employee newEmployee = new Employee(null, "Peter Jones", 70000, "Sales");

        // Act & Assert: Add admin authentication to the POST request.
        mockMvc.perform(post("/api/v1/employees")
                .with(httpBasic("admin", "password")) // Add admin authentication
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isOk());
    }

    // Test case for updating an existing employee
    @Test
    public void updateEmployee_shouldUpdateExistingEmployee() throws Exception {
        // Arrange: Save an initial employee.
        Employee initialEmployee = new Employee(null, "Original Name", 60000, "R&D");
        employeeService.saveEmployee(initialEmployee);

        // Prepare the updated employee data.
        Employee updatedDetails = new Employee(null, "Updated Name", 65000, "Research");

        // Act & Assert: Perform the PUT request and check the response body directly.
        mockMvc.perform(put("/api/v1/employees/{id}", initialEmployee.getId())
                .with(httpBasic("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.salary").value(65000));
    }

    // Test case for deleting an employee
    @Test
    public void deleteEmployee_shouldRemoveEmployee() throws Exception {
        // Arrange: Save an employee to have something to delete.
        Employee employeeToDelete = new Employee(null, "To Be Deleted", 10000, "Temporary");
        employeeService.saveEmployee(employeeToDelete);
        Long employeeId = employeeToDelete.getId();

        // Act: Perform the delete request with admin authentication.
        mockMvc.perform(delete("/api/v1/employees/{id}", employeeId)
                .with(httpBasic("admin", "password"))) // Add admin authentication
                .andExpect(status().isOk());

        // Assert: Verify that getting the employee by ID now results in a 404 Not Found.
        mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isNotFound());
    }
}