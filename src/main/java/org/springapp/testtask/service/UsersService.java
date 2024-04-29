package org.springapp.testtask.service;

import org.springapp.testtask.entity.UserEntity;
import org.springapp.testtask.exception.BirthdateRangeException;
import org.springapp.testtask.exception.NoUserException;
import org.springapp.testtask.exception.UserCreatedException;
import org.springapp.testtask.exception.UserYearsException;
import org.springapp.testtask.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository repository;

    public UsersService(UsersRepository repository) {
        this.repository = repository;
    }

    public List<UserEntity> getAllUsers() {
        return repository.findAll();
    }

    public UserEntity getUser(Long id) throws NoUserException {
        return repository.findById(id).orElseThrow(() -> new NoUserException("No user by this id"));
    }

    public List<UserEntity> getUsersByBirthDateRange(Date fromDate,
                                                     Date toDate) throws BirthdateRangeException {
        if (toDate.compareTo(fromDate) <= 0) {
            throw new BirthdateRangeException("Range can't be equal or less than 0");
        }
        return repository.getUserEntitiesByDateOfBirthBetween(fromDate, toDate);
    }

    public UserEntity createUser(UserEntity newUser) throws UserCreatedException, UserYearsException, NoUserException {
        isUserHasNoTrouble(0L, newUser);
        return repository.save(newUser);
    }

    public UserEntity updateAllUserInfo(Long id, UserEntity updatedUser) throws UserYearsException, UserCreatedException, NoUserException {
        isUserHasNoTrouble(id, updatedUser);
        updatedUser.setId(id);
        return repository.save(updatedUser);
    }

    public UserEntity updateUserInfo(Long id, UserEntity updatedUser) throws UserYearsException, UserCreatedException, NoUserException {
        UserEntity existingUser = isUserHasNoTrouble(id, updatedUser);

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

        return repository.save(existingUser);
    }

    public void deleteUser(Long id) throws NoUserException {
        Optional<UserEntity> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new NoUserException("No user by this id");
        }
        repository.deleteById(id);
    }

    private UserEntity isUserHasNoTrouble(Long id, UserEntity user) throws UserCreatedException, UserYearsException, NoUserException {
        Optional<UserEntity> oldUser = Optional.empty();
        if (id != 0L) {
            oldUser = repository.findById(id);
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new UserCreatedException("User already created");
        } else if (oldUser.isEmpty() && id != 0) {
            throw new NoUserException("User not found with id: " + id);
        } else {
            LocalDate currentDate = LocalDate.now();
            LocalDate eighteenYearsAgo = currentDate.minusYears(18);
            LocalDate userBirthDate = user.getDateOfBirth().toLocalDate();

            if (userBirthDate.isAfter(eighteenYearsAgo)) {
                throw new UserYearsException("User must be 18 years or older");
            }
        }

        return oldUser.orElse(null);
    }
}
