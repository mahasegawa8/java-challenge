package jp.co.axa.apidemo.config;

import jp.co.axa.apidemo.exceptions.ResourceNotFoundException;
import jp.co.axa.apidemo.services.EmployeeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock the service layer to isolate tests to the security configuration.
    @MockBean
    private EmployeeService employeeService;

    // Test case for GET endpoint (permitAll)
    @Test
    @WithAnonymousUser
    public void getEmployees_shouldSucceedWithNoAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk());
    }

    // Test cases for POST endpoint with no authentication
    @Test
    public void saveEmployee_shouldFailWith401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\", \"salary\":50000, \"department\":\"IT\"}"))
                .andExpect(status().isUnauthorized());
    }

    // Test cases for POST endpoint with USER role
    @Test
    public void saveEmployee_shouldFailWith403_WhenAuthenticatedAsUser() throws Exception {
        mockMvc.perform(post("/api/v1/employees")
                        .with(httpBasic("user", "password")) // Authenticate as 'user'
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\", \"salary\":50000, \"department\":\"IT\"}"))
                .andExpect(status().isForbidden());
    }

    // Test cases for POST endpoint with ADMIN role
    @Test
    public void saveEmployee_shouldSucceed_WhenAuthenticatedAsAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/employees")
                        .with(httpBasic("admin", "password")) // Authenticate as 'admin'
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\", \"salary\":50000, \"department\":\"IT\"}"))
                .andExpect(status().isOk());
    }

    // Test cases for PUT endpoint with USER role
    @Test
    public void updateEmployee_shouldFailWith403_WhenAuthenticatedAsUser() throws Exception {
        mockMvc.perform(put("/api/v1/employees/1")
                        .with(httpBasic("user", "password")) // Authenticate as 'user'
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"updated\", \"salary\":60000, \"department\":\"IT\"}"))
                .andExpect(status().isForbidden());
    }

    // Test cases for PUT endpoint with ADMIN role
    @Test
    public void updateEmployee_shouldSucceed_WhenAuthenticatedAsAdmin() throws Exception {
        // Mock the service to throw the exception for a non-existent employee.
        when(employeeService.getEmployee(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(put("/api/v1/employees/1")
            .with(httpBasic("admin", "password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"updated\", \"salary\":60000, \"department\":\"IT\"}"))
            .andExpect(status().isNotFound());
    }

    // Test cases for DELETE endpoint with USER role
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void deleteEmployee_shouldFailWith403_WhenAuthenticatedAsUser() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isForbidden());
    }

    // Test cases for DELETE endpoint with ADMIN role
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteEmployee_shouldSucceed_WhenAuthenticatedAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isOk());
    }
}