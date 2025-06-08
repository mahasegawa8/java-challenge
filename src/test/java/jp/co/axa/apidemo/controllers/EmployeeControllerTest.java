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

    @Test
    public void getEmployees_shouldReturnAllEmployees() throws Exception {
        // Arrange: Use the POST endpoint to create a new employee
        Employee newEmployee = new Employee(null, "Test Employee", 80000, "Engineering");

        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)));

        // Act & Assert: Now perform the GET request and check the result
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].name").value("Test Employee"));
    }

    @Test
    public void getEmployeeById_shouldReturnNotFoundForInvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/{id}", 999L))
                .andExpect(status().isNotFound());

    }

    @Test
    public void getEmployeeById_shouldReturnCorrectEmployee() throws Exception {
        // Arrange: Save an employee directly using the service to get a valid ID.
        Employee employee = new Employee(null, "Find Me", 55000, "QA");
        employeeService.saveEmployee(employee);

        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Find Me"))
                .andExpect(jsonPath("$.department").value("QA"));
    }

    @Test
    public void saveEmployee_shouldCreateEmployeeAndReturnSuccessMessage() throws Exception {
        // Arrange
        Employee newEmployee = new Employee(null, "Peter Jones", 70000, "Sales");

        // Act & Assert
        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isOk());
    }

    @Test
    public void updateEmployee_shouldUpdateExistingEmployee() throws Exception {
        // Arrange: Save an initial employee.
        Employee initialEmployee = new Employee(null, "Original Name", 60000, "R&D");
        employeeService.saveEmployee(initialEmployee);

        // Prepare the updated employee data.
        Employee updatedDetails = new Employee(null, "Updated Name", 65000, "Research");

        // Act: Perform the PUT request to update the employee.
        mockMvc.perform(put("/api/v1/employees/{id}", initialEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk());

        // Assert: Fetch the employee again to verify the details were updated.
        mockMvc.perform(get("/api/v1/employees/{id}", initialEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.salary").value(65000));
    }

    @Test
    public void deleteEmployee_shouldRemoveEmployee() throws Exception {
        // Arrange: Save an employee to have something to delete.
        Employee employeeToDelete = new Employee(null, "To Be Deleted", 10000, "Temporary");
        employeeService.saveEmployee(employeeToDelete);
        Long employeeId = employeeToDelete.getId();

        // Act: Perform the delete request.
        mockMvc.perform(delete("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isOk());

        // Assert: Verify that getting the employee by ID now results in a 404 Not Found.
        mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isNotFound());
    }
}