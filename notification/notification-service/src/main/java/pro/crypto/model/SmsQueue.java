package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "body")
@Data
public class SmsQueue {

    private Long memberId;

    private String memberName;

    private String phone;

    private String body;

}
