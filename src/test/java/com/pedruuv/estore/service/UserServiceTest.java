package com.pedruuv.estore.service;

import com.pedruuv.estore.data.UserRepository;
import com.pedruuv.estore.model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;

    @Mock
    EmailVerificationServiceImpl emailVerificationService;

    String firstName, lastName, email, password, repeatPassword;

    @BeforeEach
    void setUp(){
        firstName = "Camila";
        lastName = "Nascimento";
        email = "camilanascimento@teste.com";
        password = "12345678";
        repeatPassword = "12345678";
    }

    @DisplayName("🙇🏻User object created")
    @Test
    void testCreateUser_whenUserDetailsProvided_returnUserObject(){
        //Arrange
        when(userRepository.save(any(User.class))).thenReturn(true);

        //Act
        User user = userService.createUser(firstName, lastName, email, password, repeatPassword);

        //Assert
        assertAll(() -> assertNotNull(user,"The createUser() method should not have returned null" ),
                () -> assertEquals(firstName, user.getFirstName(), "User's First Name is incorrect"),
            () -> assertEquals(lastName, user.getLastName(), "User's last name is incorrect"),
                () -> assertEquals(email, user.getEmail(), "User's email is invalid"),
                () -> assertNotNull(user.getId(), "User id is missing"));

        verify(userRepository).save(any(User.class));

    }

    @DisplayName("🩻Empty first name causes correct exception")
    @Test
    void testCreateUser_whenFirstNameIsEmpty_throwsIllegalArgumentException(){
        //Arrange
        String firstName = "", expectedErrorMessage = "User's first name is empty";
        //Act & Assert'
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Empty first name should cause Illegal Argument Exception");

        //Assert
        assertEquals(expectedErrorMessage, exception.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("🩻Empty last name causes correct exception")
    @Test
    void testCreateUser_whenLastNameIsEmpty_throwsIllegalArgumentException(){
        String lastName = "";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Empty last name should cause Illegal Argument Exception");
    }

    @DisplayName("✉️Empty or invalid email causes correct exception")
    @Test
    void testCreateUser_whenEmailIsEmptyOrInvalid_throwsIllegalArgumentException(){
        String email = "";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Invalid email should cause Illegal Argument Exception");
    }

    @DisplayName("📛If save() method causes RuntimeException, a UserServiceException is thrown")
    @Test
    void testCreateUser_whenSaveMethodThrowsException_thenThrowsUserServiceException(){
        //Arrange
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class);

        //Act & Assert
        assertThrows(UserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Should have thrown UserServiceException instead");
        //Assert
    }

    @Test
    @DisplayName("EmailNotificationException is handled")
    void testCreateUser_whenEmailNotificationExceptionThrown_throwsUserServiceException(){
        when(userRepository.save(any(User.class))).thenReturn(true);

        doThrow(EmailNotificationServiceException.class)
                .when(emailVerificationService)
                .scheduleEmailConfirmation(any(User.class));

        assertThrows(UserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Should have thrown UserServiceException instead");

        verify(emailVerificationService, times(1)).
                scheduleEmailConfirmation(any(User.class));


    }

    @DisplayName("Schedule Email Confirmation is executed")
    @Test
    void testCreateUser_whenUserCreated_scheduleEmailConfirmation(){
        when(userRepository.save(any(User.class))).thenReturn(true);

        doCallRealMethod().when(emailVerificationService).scheduleEmailConfirmation(any(User.class));

        userService.createUser(firstName, lastName, email, password, repeatPassword);

        verify(emailVerificationService, times(1)).scheduleEmailConfirmation(any(User.class));
    }
}
