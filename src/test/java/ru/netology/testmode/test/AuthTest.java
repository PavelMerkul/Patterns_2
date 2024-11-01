package ru.netology.testmode.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.netology.testmode.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.testmode.data.DataGenerator.Registration.getRegisteredUser ;
import static ru.netology.testmode.data.DataGenerator.Registration.getUser ;

class AuthTest {

    private DataGenerator.RegistrationDto registeredUser ;

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        if (registeredUser  != null) {
            DataGenerator.deleteUser (registeredUser );
        }
    }

    @Test
    @DisplayName("Should successfully login with active registered user")
    void shouldSuccessfulLoginIfRegisteredActiveUser () {
        registeredUser  = getRegisteredUser ("active");
        $("[data-test-id='login'] input").setValue(registeredUser .getLogin());
        $("[data-test-id='password'] input").setValue(registeredUser .getPassword());
        $("button.button").click();
        $("h2").shouldHave(Condition.exactText("Личный кабинет")).shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should get error message if login with not registered user")
    void shouldGetErrorIfNotRegisteredUser () {
        var notRegisteredUser  = getUser ("active");
        $("[data-test-id='login'] input").setValue(notRegisteredUser .getLogin());
        $("[data-test-id='password'] input").setValue(notRegisteredUser .getPassword());
        $("button.button").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"), Duration.ofSeconds(10))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser () {
        registeredUser  = getRegisteredUser ("blocked");
        $("[data-test-id='login'] input").setValue(registeredUser .getLogin());
        $("[data-test-id='password'] input").setValue(registeredUser .getPassword());
        $("button.button").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.text("Ошибка! Пользователь заблокирован"), Duration.ofSeconds(10))
                .shouldBe(Condition.visible);
    }

    @ParameterizedTest
    @ValueSource(strings = {"wrongLogin", "wrongPassword"})
    @DisplayName("Should get error message if login with wrong credentials")
    void shouldGetErrorIfWrongCredentials(String credentialType) {
        registeredUser  = getRegisteredUser ("active");
        String wrongLogin = credentialType.equals("wrongLogin") ? "invalidLogin" : registeredUser .getLogin();
        String wrongPassword = credentialType.equals("wrongPassword") ? "invalidPassword" : registeredUser .getPassword();

        $("[data-test-id='login'] input").setValue(wrongLogin);
        $("[data-test-id='password'] input").setValue(wrongPassword);
        $("button.button").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"), Duration.ofSeconds(10))
                .shouldBe(Condition.visible);
    }
}
