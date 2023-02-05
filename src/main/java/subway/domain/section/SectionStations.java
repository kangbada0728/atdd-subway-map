package subway.domain.section;

import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.dto.domain.UpAndDownStationsDto;
import subway.domain.sectionstation.Direction;
import subway.domain.sectionstation.SectionStation;
import subway.domain.station.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Embeddable
public class SectionStations {
    @OneToMany(mappedBy = "section")
    private List<SectionStation> sectionStations = new ArrayList<>();

    public UpAndDownStationsDto getUpAndDownStations() {
        Station upStation = sectionStations.stream()
                .filter(sectionStation -> sectionStation.getDirection().equals(Direction.UP))
                .findFirst()
                .map(SectionStation::getStation)
                .orElseThrow();
        Station downStation = sectionStations.stream()
                .filter(sectionStation -> sectionStation.getDirection().equals(Direction.DOWN))
                .findFirst()
                .map(SectionStation::getStation)
                .orElseThrow();
        return new UpAndDownStationsDto(upStation, downStation);
    }

}
