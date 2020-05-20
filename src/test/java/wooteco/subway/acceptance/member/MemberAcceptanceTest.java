package wooteco.subway.acceptance.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.acceptance.AcceptanceTest;
import wooteco.subway.domain.member.MemberConstructException;
import wooteco.subway.service.member.CreateMemberException;
import wooteco.subway.service.member.dto.MemberErrorResponse;
import wooteco.subway.service.member.dto.MemberResponse;

public class MemberAcceptanceTest extends AcceptanceTest {

    @DisplayName("회원가입 성공")
    @Test
    void createMemberSucceed() {}

    @DisplayName("이미 존재하는 이메일로 회원가입")
    @Test
    void createMemberWithEmailAlreadyExist() {
        final String TEST_EMAIL = "test@test.com";
        final String TEST_NAME = "testName";
        final String TEST_PASSWORD = "testPassword";
        createMember(TEST_EMAIL, TEST_NAME, TEST_PASSWORD);

        Map<String, String> params = new HashMap<>();
        params.put("email", TEST_EMAIL);
        params.put("name", TEST_NAME);
        params.put("password", TEST_PASSWORD);

        MemberErrorResponse response =
            given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
            when().
                post("/members").
            then().
                log().all().
                statusCode(HttpStatus.BAD_REQUEST.value()).
                extract().as(MemberErrorResponse.class);
        assertThat(response.getErrorMessage()).isEqualTo(CreateMemberException.WRONG_CREATE_MESSAGE);
    }

    @DisplayName("요구되는 입력에 빈 값이 있는 경우의 회원가입")
    @Test
    void createMemberWithEmptyInput() {
        final String TEST_EMAIL = "test@test.com";
        final String TEST_NAME = "";
        final String TEST_PASSWORD = "testPassword";

        Map<String, String> params = new HashMap<>();
        params.put("email", TEST_EMAIL);
        params.put("name", TEST_NAME);
        params.put("password", TEST_PASSWORD);

        MemberErrorResponse response = given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/members").
                then().
                log().all().
                statusCode(HttpStatus.BAD_REQUEST.value()).
                extract().as(MemberErrorResponse.class);

            assertThat(response.getErrorMessage()).isEqualTo(MemberConstructException.EMPTY_NAME_MESSAGE);
    }

    @DisplayName("회원 관리 기능")
    @Test
    void manageMember() {
        String location = createMember(TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);
        assertThat(location).isNotBlank();

        MemberResponse memberResponse = getMember(TEST_USER_EMAIL);
        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(TEST_USER_EMAIL);
        assertThat(memberResponse.getName()).isEqualTo(TEST_USER_NAME);

        updateMember(memberResponse);
        MemberResponse updatedMember = getMember(TEST_USER_EMAIL);
        assertThat(updatedMember.getName()).isEqualTo("NEW_" + TEST_USER_NAME);

        deleteMember(memberResponse);
    }
}
