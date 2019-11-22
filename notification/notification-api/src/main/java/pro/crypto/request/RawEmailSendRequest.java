package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(exclude = "body")
@Builder
public class RawEmailSendRequest {

    private Long memberId;

    private String subject;

    private String body;

    private boolean html;

}
