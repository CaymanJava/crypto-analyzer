package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(schema = "crypto_monitoring")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Signal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(targetClass = Position.class, fetch = EAGER)
    @CollectionTable(name = "signal_position",
            schema = "crypto_monitoring",
            joinColumns = @JoinColumn(name = "signal_id"))
    @Column(name = "position")
    @Enumerated(STRING)
    private Set<Position> positions;

    @NotNull
    private Long memberId;

    @NotNull
    private Long marketId;

    @NotNull
    private Long memberStrategyId;

    @NotNull
    @Enumerated(STRING)
    private StrategyType strategyType;

    @NotNull
    @Enumerated(STRING)
    private TimeFrame timeFrame;

    @NotNull
    private String marketName;

    @NotNull
    @Enumerated(STRING)
    private Stock stock;

    @NotNull
    private String strategyName;

    @NotNull
    private String customStrategyName;

    @NotNull
    private LocalDateTime tickTime;

    @NotNull
    private LocalDateTime creationTime;

    @PrePersist
    public void initCreationTime() {
        this.creationTime = now();
    }

}
