package pro.crypto.configuration.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.quartz.Job;

@Data
@Builder
@AllArgsConstructor
public class JobInfo {

    private String name;

    private String groupName;

    private String description;

    private Class<? extends Job> jobClass;

    private String scheduleExpression;

}
