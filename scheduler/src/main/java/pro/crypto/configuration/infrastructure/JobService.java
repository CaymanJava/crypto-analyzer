package pro.crypto.configuration.infrastructure;

import org.quartz.Job;

import java.util.List;

public interface JobService {

    List<JobInfo> getJobsToScheduling();

    default JobInfo getJobInfo(String name, String groupName, String description, Class<? extends Job> jobClass, String scheduleExpression) {
        return JobInfo.builder()
                .name(name)
                .groupName(groupName)
                .description(description)
                .jobClass(jobClass)
                .scheduleExpression(scheduleExpression)
                .build();
    }

}
