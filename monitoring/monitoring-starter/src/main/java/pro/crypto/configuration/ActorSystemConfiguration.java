package pro.crypto.configuration;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.MonitoringProperties;
import pro.crypto.aktor.DecisionMakerActor;
import pro.crypto.aktor.SignalSenderActor;
import pro.crypto.aktor.StrategyCalculationActor;
import pro.crypto.aktor.StrategyMonitoringActor;
import pro.crypto.routing.CommonRouter;
import pro.crypto.routing.PropsFactory;
import pro.crypto.routing.supervisor.DecisionMakerSupervisor;
import pro.crypto.routing.supervisor.SignalSenderSupervisor;
import pro.crypto.routing.supervisor.StrategyCalculationSupervisor;
import pro.crypto.routing.supervisor.StrategyMonitoringSupervisor;

@Configuration
@EnableConfigurationProperties({MonitoringProperties.class})
public class ActorSystemConfiguration {

    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create("crypto-analyzer-actor-system", ConfigFactory.load());
    }

    @Configuration
    @Import({StrategyMonitoringActor.class, StrategyCalculationActor.class,
            DecisionMakerActor.class, SignalSenderActor.class})
    public static class ActorsConfiguration {
    }

    @Configuration
    @Import({StrategyMonitoringSupervisor.class, StrategyCalculationSupervisor.class,
            DecisionMakerSupervisor.class, SignalSenderSupervisor.class})
    public static class SupervisorsConfiguration {
    }

    @Configuration
    @Import({CommonRouter.class, PropsFactory.class})
    public static class ActorsContextConfiguration {
    }

}
