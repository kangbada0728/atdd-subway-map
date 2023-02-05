package subway.dto.line;

import lombok.Getter;

@Getter
public class LineExtendResponse {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

}
