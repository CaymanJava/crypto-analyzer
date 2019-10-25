package pro.crypto.configuration.infrastructure.market;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pro.crypto.configuration.infrastructure.JobInfo;
import pro.crypto.configuration.infrastructure.JobService;
import pro.crypto.scheduler.MarketSynchronisationJob;

import java.util.List;

import static java.util.Collections.singletonList;

@Component
@AllArgsConstructor
public class MarketJobConfigurator implements JobService {

    private static final String MARKET_SYNCHRONISATION_JOB_DESCRIPTION = "Synchronise markets from all stocks";
    private final MarketExpressionProperties marketExpressionProperties;

    @Override
    public List<JobInfo> getJobsToScheduling() {
        return singletonList(
                getJobInfo("MarketSynchronisationJob", "group.Market", MARKET_SYNCHRONISATION_JOB_DESCRIPTION,
                        MarketSynchronisationJob.class, marketExpressionProperties.getMarketSynchronisationExpression())
        );
    }

}
