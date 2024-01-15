package test;

import com.codeborne.selenide.Condition;
import data.DataGenerator;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static data.DataGenerator.Registration.getRegisteredUser;
import static data.DataGenerator.getRandomLogin;
import static data.DataGenerator.getRandomPassword;
import static io.restassured.RestAssured.given;

// спецификация нужна для того, чтобы переиспользовать настройки в разных запросах
class AuthTest {
    @BeforeEach
    void setup() {
        open("http://localhost:9999");

    }

    @Test
    @DisplayName("Should successfully login active registered user")
    void ShouldSuccessfullyLoginActiveRegisteredUser () {
        var validUser = getRegisteredUser("active");
        $("[data-test-id=login] input").setValue(validUser.getLogin());
        $("[data-test-id=password] input").setValue(validUser.getPassword());
        $("button").click();
        $(".heading").shouldHave(Condition.text("  Личный кабинет"), Duration.ofSeconds(5))
                .shouldBe(Condition.visible);
    }
    @Test
    @DisplayName("Should display error message when user unregistered")
    void ShouldDisplayErrorMessageWhenUserUnregistered () {
        var invalidUser = DataGenerator.Registration.getUser("active");
        $("[data-test-id=login] input").setValue(invalidUser.getLogin());
        $("[data-test-id=password] input").setValue(invalidUser.getPassword());
        $("button").click();
        $("[data-test-id=error-notification]")
                .shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(5))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser() {
        var blockedUser = getRegisteredUser("blocked");
        $("[data-test-id=login] input").setValue(blockedUser.getLogin());
        $("[data-test-id=password] input").setValue(blockedUser.getPassword());
        $("button").click();
        $("[data-test-id=error-notification]")
                .shouldHave(Condition.text("Пользователь заблокирован"), Duration.ofSeconds(5))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should get error message if login with wrong login")
    void shouldGetErrorIfWrongLogin() {
        var registeredUser = getRegisteredUser("active");
        var wrongLogin = getRandomLogin();
        $("[data-test-id=login] input").setValue(wrongLogin);
        $("[data-test-id=password] input").setValue(registeredUser.getPassword());
        $("button").click();
        $("[data-test-id=error-notification]")
                .shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(5))
                .shouldBe(Condition.visible);
    }
    @Test
    @DisplayName("Should get error message if login with wrong password")
    void shouldGetErrorIfWrongPassword() {
        var registeredUser = getRegisteredUser("active");
        var wrongPassword = getRandomPassword();
        $("[data-test-id=login] input").setValue(registeredUser.getLogin());
        $("[data-test-id=password] input").setValue(wrongPassword);
        $("button").click();
        $("[data-test-id=error-notification]")
                .shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(5))
                .shouldBe(Condition.visible);
    }
}