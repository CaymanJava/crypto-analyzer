package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.TimeFrame;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TickTimeFindRequest {

    private Long marketId;

    private TimeFrame timeFrame;

    private LocalDateTime from;

    private LocalDateTime to;

}
