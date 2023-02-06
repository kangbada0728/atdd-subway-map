package subway.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.controller.StationUtils.*;

@DisplayName("지하철 역 노선 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {
    Map<String, Object> SIN_BUN_DANG_STATION_LINE = new HashMap<>();
    Map<String, Object> ONE_STATION_LINE = new HashMap<>();

    @BeforeEach
    void setup() {
        StationUtils.createStation(GANG_NAM_STATION);
        StationUtils.createStation(SIN_SA_STATION);

        StationUtils.createColor(LINE_RED);
        StationUtils.createColor(LINE_BLUE);

        SIN_BUN_DANG_STATION_LINE.put("name", SIN_BUN_DANG_LINE_NAME);
        SIN_BUN_DANG_STATION_LINE.put("color", LINE_RED);
        SIN_BUN_DANG_STATION_LINE.put("upStationId", 1);
        SIN_BUN_DANG_STATION_LINE.put("downStationId", 2);
        SIN_BUN_DANG_STATION_LINE.put("distance", 10);

        StationUtils.createStation(GURO_STATION);
        StationUtils.createStation(SINDORIM_STATION);

        ONE_STATION_LINE.put("name", ONE_LINE_NAME);
        ONE_STATION_LINE.put("color", LINE_BLUE);
        ONE_STATION_LINE.put("upStationId", 3);
        ONE_STATION_LINE.put("downStationId", 4);
        ONE_STATION_LINE.put("distance", 20);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    void createStationLine() {
        ExtractableResponse<Response> response = StationUtils.createLine(SIN_BUN_DANG_STATION_LINE);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/1");
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        JsonPath jsonPath = response.jsonPath();

        assertThat(jsonPath.getLong("id")).isEqualTo(1L);
        assertThat(jsonPath.getString("name")).isEqualTo(SIN_BUN_DANG_LINE_NAME);
        assertThat(jsonPath.getString("color")).isEqualTo(LINE_RED);
    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void selectStationLineList() {
        StationUtils.createLine(SIN_BUN_DANG_STATION_LINE);
        StationUtils.createLine(ONE_STATION_LINE);

        ExtractableResponse<Response> response =
                RestAssured
                        .given().spec(StationUtils.getRequestSpecification()).log().all()
                        .when().get("/lines")
                        .then().log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        JsonPath jsonPath = response.jsonPath();

        assertThat(jsonPath.getList("id", Integer.class)).containsExactly(1, 2);
        assertThat(jsonPath.getList("name", String.class)).containsExactly(SIN_BUN_DANG_LINE_NAME, ONE_LINE_NAME);
        assertThat(jsonPath.getList("color", String.class)).containsExactly(LINE_RED, LINE_BLUE);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void selectStationLine() {
        StationUtils.createLine(SIN_BUN_DANG_STATION_LINE);

        ExtractableResponse<Response> response =
                RestAssured
                        .given().spec(getRequestSpecification()).log().all()
                        .when().get("/lines/1")
                        .then().log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        JsonPath jsonPath = response.jsonPath();

        assertThat(jsonPath.getLong("id")).isEqualTo(1L);
        assertThat(jsonPath.getString("name")).isEqualTo(SIN_BUN_DANG_LINE_NAME);
        assertThat(jsonPath.getString("color")).isEqualTo(LINE_RED);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void updateStationLine() {
        StationUtils.createLine(SIN_BUN_DANG_STATION_LINE);

        Map<String, Object> body = new HashMap<>();
        body.put("name", ONE_LINE_NAME);
        body.put("color", LINE_BLUE);

        ExtractableResponse<Response> response =
                RestAssured
                        .given().spec(getRequestSpecification()).body(body).log().all()
                        .when().put("/lines/1")
                        .then().log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        JsonPath jsonPath = StationUtils.selectLine(1L).jsonPath();

        assertThat(jsonPath.getString("name")).isEqualTo(ONE_LINE_NAME);
        assertThat(jsonPath.getString("color")).isEqualTo(LINE_BLUE);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void deleteStationLine() {
        StationUtils.createLine(SIN_BUN_DANG_STATION_LINE);

        ExtractableResponse<Response> response =
                RestAssured
                        .given().spec(getRequestSpecification()).log().all()
                        .when().delete("/lines/1")
                        .then().log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(StationUtils.selectLine(1).statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
