package subway.dto.line;

import lombok.Getter;

@Getter
public class LineStationCreateResponse {
    private final Long id;
    private final String name;

    public LineStationCreateResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
