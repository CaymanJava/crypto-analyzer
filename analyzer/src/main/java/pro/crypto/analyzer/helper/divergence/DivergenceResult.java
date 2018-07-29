package pro.crypto.analyzer.helper.divergence;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivergenceResult /*implements Comparable<DivergenceResult>*/ {

    private DivergenceType divergenceType;

    private DivergenceClass divergenceClass;

    private int indexFrom;

    private int indexTo;

//    @Override
//    public int compareTo(DivergenceResult another) {
//        return this.getIndexFrom() > 0;
//    }
}
