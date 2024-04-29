package org.springapp.testtask.controller;

import org.springapp.testtask.entity.UserEntity;
import org.springapp.testtask.exception.BirthdateRangeException;
import org.springapp.testtask.exception.NoUserException;
import org.springapp.testtask.exception.UserCreatedException;
import org.springapp.testtask.exception.UserYearsException;
import org.springapp.testtask.service.UsersService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService service;

    public UsersController(UsersService userService) {
        this.service = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUser(@PathVariable Long id) throws NoUserException {
        return ResponseEntity.ok(service.getUser(id));
    }

    @GetMapping("/range")
    public ResponseEntity<List<UserEntity>> getUsersByBirthDateRange(@RequestParam("from") Date fromDate,
                                                                     @RequestParam("to") Date toDate) throws BirthdateRangeException {
        return ResponseEntity.ok(service.getUsersByBirthDateRange(fromDate, toDate));
    }

    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity newUser) throws UserCreatedException, UserYearsException, NoUserException {
        return ResponseEntity.ok(service.createUser(newUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateAllUserInfo(@PathVariable Long id, @RequestBody UserEntity updatedUser) throws UserYearsException, UserCreatedException, NoUserException {
        return ResponseEntity.ok(service.updateAllUserInfo(id, updatedUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserEntity> updateUserInfo(@PathVariable Long id, @RequestBody UserEntity updatedUser) throws UserYearsException, UserCreatedException, NoUserException {
        return ResponseEntity.ok(service.updateUserInfo(id, updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws NoUserException {
        service.deleteUser(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("User deleted");
    }
}
