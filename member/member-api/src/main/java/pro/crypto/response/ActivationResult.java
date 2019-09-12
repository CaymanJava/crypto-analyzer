package pro.crypto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static pro.crypto.response.ActivationStatus.FAILED;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ActivationResult {

    private Long memberId;

    private ActivationStatus status;

    public static ActivationResult failed() {
        return ActivationResult.builder()
                .status(FAILED)
                .build();
    }

}
