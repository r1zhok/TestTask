package org.springapp.testtask.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springapp.testtask.entity.UserEntity;
import org.springapp.testtask.exception.BirthdateRangeException;
import org.springapp.testtask.exception.NoUserException;
import org.springapp.testtask.exception.UserCreatedException;
import org.springapp.testtask.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersControllerTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.28");

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UsersService userService;

    // TODO if start all tests, this test can fall, because create method start earlier, this method expected 2 elements but got 3
    // NOTE if comment create test method or start only this method it will work
    // I use orders because tests not disturb each other
    @Test
    @Order(1)
    public void getAllUsersTest() throws Exception {
        List<UserEntity> users = List.of(
                new UserEntity(1L, "John", "Doe", "john.doe@example.com",
                        Date.valueOf("1990-01-01"), "123 Main Street", "555-1234"),
                new UserEntity(2L, "Jane", "Smith", "jane.smith@example.com",
                        Date.valueOf("1995-02-15"), "456 Elm Street", "555-5678")
        );

        given(userService.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/users")).
                andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  {"id":1,"firstName":"John","lastName":"Doe","email":"john.doe@example.com","dateOfBirth":"1990-01-01","address":"123 Main Street","phoneNumber":"555-1234"},
                                  {"id":2,"firstName":"Jane","lastName":"Smith","email":"jane.smith@example.com","dateOfBirth":"1995-02-15","address":"456 Elm Street","phoneNumber":"555-5678"}
                                ]
                                """)
                );
    }

    @Test
    @Order(2)
    public void getUserTestSuccess() throws Exception {
        given(userService.getUser(1L)).willReturn(new UserEntity(1L, "John", "Doe", "john.doe@example.com",
                Date.valueOf("1990-01-01"), "123 Main Street", "555-1234"));

        mockMvc.perform(get("/users/1")).
                andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                  {"id":1,"firstName":"John","lastName":"Doe","email":"john.doe@example.com","dateOfBirth":"1990-01-01","address":"123 Main Street","phoneNumber":"555-1234"}
                                """)
                );
    }

    @Test
    public void getUserTestThrowException() throws Exception {
        given(userService.getUser(10L)).willThrow(new NoUserException("No user by this id"));

        mockMvc.perform(get("/users/10")).
                andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().string("No user by this id")
                );
    }

    @Test
    public void getUsersByBirthDateRangeSuccessTest() throws Exception {
        Date fromDate = Date.valueOf("2024-01-01");
        Date toDate = Date.valueOf("2024-12-31");

        given(userService.getUsersByBirthDateRange(fromDate, toDate)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/users/range")
                        .param("from", fromDate.toString())
                        .param("to", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void getUsersByBirthDateRangeThrowExceptionTest() throws Exception {
        Date fromDate = Date.valueOf("2025-01-01");
        Date toDate = Date.valueOf("2024-12-31");

        given(userService.getUsersByBirthDateRange(fromDate, toDate)).willThrow(new BirthdateRangeException("Range can't be equal or less than 0"));

        mockMvc.perform(get("/users/range")
                        .param("from", fromDate.toString())
                        .param("to", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Range can't be equal or less than 0"));
    }

    @Test
    public void createUserSuccess() throws Exception {
        var newUserJson = """
        {
          "id": 3,
          "firstName": "Alice",
          "lastName": "Johnson",
          "email": "alice.johnson@example.com",
          "dateOfBirth": "1992-08-25",
          "address": "789 Oak Street",
          "phoneNumber": "555-9012"
        }
        """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                    {
                      "id": 3,
                      "firstName": "Alice",
                      "lastName": "Johnson",
                      "email": "alice.johnson@example.com",
                      "dateOfBirth": "1992-08-25",
                      "address": "789 Oak Street",
                      "phoneNumber": "555-9012"
                    }
                    """));
    }


    @Test
    @Order(3)
    public void createUserThrowUserCreatedException() throws Exception {
        var newUser = new UserEntity(
                1L, "John", "Doe", "john.doe@example.com",
                Date.valueOf("1990-01-01"), "123 Main Street", "555-1234"
        );
        given(userService.createUser(newUser)).willThrow(new UserCreatedException("User already created"));

        mockMvc.perform(post("/users").content("""
                        {"id":1,"firstName":"John","lastName":"Doe","email":"john.doe@example.com","dateOfBirth":"1990-01-01","address":"123 Main Street","phoneNumber":"555-1234"}
                        """).contentType(MediaType.APPLICATION_JSON)).
                andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().string("User already created")
                );
    }

    @Test
    public void createUserYearsExceptionException() throws Exception {
        var newUser = new UserEntity(
                1L, "John", "Doe", "11111111@example.com",
                Date.valueOf("2024-01-01"), "123 Main Street", "555-1234"
        );
        given(userService.createUser(newUser)).willThrow(new UserCreatedException("User must be 18 years or older"));

        mockMvc.perform(post("/users").content("""
                        {"id":1,"firstName":"John","lastName":"Doe","email":"11111111@example.com","dateOfBirth":"2024-01-01","address":"123 Main Street","phoneNumber":"555-1234"}
                        """).contentType(MediaType.APPLICATION_JSON)).
                andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().string("User must be 18 years or older")
                );
    }

    @Test
    @Order(4)
    public void updateAllUserInfoSuccess() throws Exception {
        var updatedUserData = new UserEntity(
                1L, "Updated", "User", "updated.user@example.com",
                Date.valueOf("1990-01-01"), "Updated Address", "555-5555"
        );
        var updatedUserJson = """
            {
              "id": 1,
              "firstName": "Updated",
              "lastName": "User",
              "email": "updated.user@example.com",
              "dateOfBirth": "1990-01-01",
              "address": "Updated Address",
              "phoneNumber": "555-5555"
            }
            """;

        given(userService.updateAllUserInfo(1L, updatedUserData)).willReturn(updatedUserData);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(updatedUserJson));
    }

    @Test
    public void updateAllUserInfoThrowNoUserException() throws Exception {
        var updatedUserData = new UserEntity(
                1L, "Updated", "User", "updated.user@example.com",
                Date.valueOf("1990-01-01"), "Updated Address", "555-5555"
        );
        var updatedUserJson = """
            {
              "id": 1,
              "firstName": "Updated",
              "lastName": "User",
              "email": "updated.user@example.com",
              "dateOfBirth": "1990-01-01",
              "address": "Updated Address",
              "phoneNumber": "555-5555"
            }
            """;

        given(userService.updateAllUserInfo(10L, updatedUserData)).willThrow(new NoUserException("No user by this id"));

        mockMvc.perform(put("/users/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("User not found with id: 10"));
    }

    @Test
    @Order(11)
    public void deleteUserSuccess() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("User deleted"));
    }
}
