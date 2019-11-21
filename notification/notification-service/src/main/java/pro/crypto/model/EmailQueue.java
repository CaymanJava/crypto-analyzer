package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmailQueue {

    private Long memberId;

    private String memberName;

    private String email;

    private String subject;

    private String body;

    private boolean html;

}
