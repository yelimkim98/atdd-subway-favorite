package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.service.member.dto.MemberResponse;
import wooteco.subway.service.member.dto.TokenResponse;

public class AuthAcceptanceTest extends AcceptanceTest {
    @DisplayName("Basic Auth")
    @Test
    void myInfoWithBasicAuth() {
        createMember(TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);

        MemberResponse memberResponse = myInfoWithBasicAuth(TEST_USER_EMAIL, TEST_USER_PASSWORD);

        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(TEST_USER_EMAIL);
        assertThat(memberResponse.getName()).isEqualTo(TEST_USER_NAME);
    }

    @DisplayName("Session")
    @Test
    void myInfoWithSession() {
        createMember(TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);

        String sessionId = sessionLogin(TEST_USER_EMAIL, TEST_USER_PASSWORD);

        MemberResponse memberResponse = myInfoWithSession(sessionId);

        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(TEST_USER_EMAIL);
        assertThat(memberResponse.getName()).isEqualTo(TEST_USER_NAME);
    }

    @DisplayName("Bearer Auth")
    @Test
    void myInfoWithBearerAuth() {
        createMember(TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);
        TokenResponse tokenResponse = login(TEST_USER_EMAIL, TEST_USER_PASSWORD);

        MemberResponse memberResponse = myInfoWithBearerAuth(tokenResponse);
        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(TEST_USER_EMAIL);
        assertThat(memberResponse.getName()).isEqualTo(TEST_USER_NAME);
    }

    private MemberResponse myInfoWithBasicAuth(String email, String password) {
        return
            given()
                .auth()
                .preemptive()
                .basic(email, password)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/me/basic")
            .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract().as(MemberResponse.class);
    }

    private MemberResponse myInfoWithSession(String sessionId) {
        // TODO: form auth를 활용하여 /me/session 요청하여 내 정보 조회
        return given()
            .sessionId(sessionId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/me/session")
        .then()
            .log().all()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .extract().as(MemberResponse.class);
    }

    private MemberResponse myInfoWithBearerAuth(TokenResponse tokenResponse) {
        // TODO: oauth2 auth(bearer)를 활용하여 /me/bearer 요청하여 내 정보 조회
        return given()
            .auth()
            .oauth2(tokenResponse.getAccessToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/me/bearer")
        .then()
            .log().all()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .extract().as(MemberResponse.class);
    }

    public TokenResponse login(String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        return
                given().
                        body(params).
                        contentType(MediaType.APPLICATION_JSON_VALUE).
                        accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                        post("/oauth/token").
                then().
                        log().all().
                        statusCode(HttpStatus.OK.value()).
                        extract().as(TokenResponse.class);
    }

    public String sessionLogin(String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        return given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
        when().
            post("/login").
        then().
            log().all().
            statusCode(HttpStatus.OK.value()).extract().sessionId();
    }
}