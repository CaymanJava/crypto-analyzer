package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.MemberStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberFindRequest {

    private String query;

    private MemberStatus status;

}
