package pro.crypto;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

// TODO remove unused

public final class SpecificationsBuilder<T> {

    private final List<Specification<T>> specificationsList = new ArrayList<>();

    public static <T> SpecificationsBuilder<T> create() {
        return new SpecificationsBuilder<>();
    }

    public Specifications<T> build() {
        return tryBuild().orElse(null);
    }

    public Optional<Specifications<T>> tryBuild() {
        return specificationsList.stream()
                .map(Specifications::where)
                .reduce(Specifications::and);
    }

    public SpecificationsBuilder<T> like(List<SingularAttribute<T, String>> attributes, String value) {
        return predicate((root, builder) -> builder.or(
                attributes.stream()
                        .map(attribute -> builder.like(builder.lower(root.get(attribute)), "%" + normalizeString(value) + "%"))
                        .toArray(Predicate[]::new)
        ), () -> hasText(value));
    }

    public SpecificationsBuilder<T> like(SingularAttribute<T, String> attribute, String value) {
        return this.like((root, builder) -> root.get(attribute), value);
    }

    public SpecificationsBuilder<T> startsWith(SingularAttribute<T, String> attribute, String value) {
        return this.startsWith((root, builder) -> root.get(attribute), value);
    }

    public <K> SpecificationsBuilder<T> in(SingularAttribute<T, K> attribute, List<K> values) {
        return predicate((root, builder) -> root.get(attribute).in(values), () -> nonNull(values));
    }

    public <K> SpecificationsBuilder<T> in(SingularAttribute<T, K> attribute, Set<K> values) {
        return predicate((root, builder) -> root.get(attribute).in(values), () -> nonNull(values));
    }

    public <K> SpecificationsBuilder<T> inOr(SingularAttribute<T, K> attribute, Set<K> firstValues,
                                             Function<Root<T>, Expression<K>> attributeProvider, Set<K> secondValues) {
        return predicate((root, builder) -> {
            Predicate first = root.get(attribute).in(firstValues);
            Predicate second = attributeProvider.apply(root).in(secondValues);
            return builder.or(first, second);
        }, () -> nonNull(firstValues) && nonNull(secondValues));
    }

    public <K> SpecificationsBuilder<T> in(Function<Root<T>, Expression<K>> attributeProvider, Set<K> values) {
        if (nonNull(values) && values.isEmpty()) {
            values.add(null);
        }
        return predicate((root, builder) -> attributeProvider.apply(root).in(values), () -> nonNull(values));
    }

    public <K> SpecificationsBuilder<T> notIn(SingularAttribute<T, K> attribute, Set<K> values) {
        return predicate((root, builder) -> root.get(attribute).in(values).not(), () -> nonNull(values));
    }

    public <K> SpecificationsBuilder<T> contains(SetAttribute<T, K> attribute, Set<K> values) {
        return predicate((root, builder) -> {
            SetJoin<Object, Object> setJoin = root.joinSet(attribute.getName());
            return builder.isTrue(setJoin.in(values));
        });
    }

    public <K> SpecificationsBuilder<T> localizedMembers(Set<K> values) {
        return predicate((root, builder) -> {
            SetJoin<Object, Object> setJoin = root.joinSet("clubIdsAccess");
            return builder.isTrue(setJoin.in(values));
        });
    }

    public SpecificationsBuilder<T> like(Function<Root<T>, Expression<String>> attributeProvider, String value) {
        return like((root, builder) -> attributeProvider.apply(root), value);
    }

    public SpecificationsBuilder<T> like(BiFunction<Root<T>, CriteriaBuilder, Expression<String>> attributeProvider, String value) {
        return predicate((root, builder) -> builder.like(builder.lower(attributeProvider.apply(root, builder)), "%" + normalizeString(value) + "%"), () -> hasText(value));
    }

    public SpecificationsBuilder<T> startsWith(BiFunction<Root<T>, CriteriaBuilder, Expression<String>> attributeProvider, String value) {
        return predicate((root, builder) -> builder.like(builder.lower(attributeProvider.apply(root, builder)),  normalizeString(value) + "%"), () -> hasText(value));
    }

    public <K> SpecificationsBuilder<T> equal(SingularAttribute<T, K> attribute, K value) {
        return equal(root -> root.get(attribute), value);
    }

    public <K> SpecificationsBuilder<T> equal(Function<Root<T>, Expression> attributeProvider, K value) {
        return equal((root, builder) -> attributeProvider.apply(root), value);
    }

    public <K> SpecificationsBuilder<T> equal(BiFunction<Root<T>, CriteriaBuilder, Expression> attributeProvider, K value) {
        return predicate((root, builder) -> builder.equal(attributeProvider.apply(root, builder), value), () -> nonNull(value));
    }

    public <K> SpecificationsBuilder<T> notEqual(SingularAttribute<T, K> attribute, K value) {
        return notEqual(root -> root.get(attribute), value);
    }

    public <K> SpecificationsBuilder<T> notEqual(Function<Root<T>, Expression> attributeProvider, K value) {
        return notEqual((root, builder) -> attributeProvider.apply(root), value);
    }

    public <K> SpecificationsBuilder<T> notEqual(BiFunction<Root<T>, CriteriaBuilder, Expression> attributeProvider, K value) {
        return predicate((root, builder) -> builder.notEqual(attributeProvider.apply(root, builder), value), () -> nonNull(value));
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> lessThan(SingularAttribute<T, K> attribute, K value) {
        return lessThan(root -> root.get(attribute), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> lessThan(Function<Root<T>, Expression<? extends K>> attributeProvider, K value) {
        return lessThan((root, builder) -> attributeProvider.apply(root), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> lessThan(BiFunction<Root<T>, CriteriaBuilder, Expression<? extends K>> attributeProvider, K value) {
        return predicate((root, builder) -> builder.lessThan(attributeProvider.apply(root, builder), value), () -> nonNull(value));
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> lessThanOrEqualTo(SingularAttribute<T, K> attribute, K value) {
        return lessThanOrEqualTo(root -> root.get(attribute), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> lessThanOrEqualTo(Function<Root<T>, Expression<? extends K>> attributeProvider, K value) {
        return lessThanOrEqualTo((root, builder) -> attributeProvider.apply(root), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> lessThanOrEqualTo(BiFunction<Root<T>, CriteriaBuilder, Expression<? extends K>> attributeProvider, K value) {
        return predicate((root, builder) -> builder.lessThanOrEqualTo(attributeProvider.apply(root, builder), value), () -> nonNull(value));
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> greaterThan(SingularAttribute<T, K> attributeProvider, K value) {
        return greaterThan(root -> root.get(attributeProvider), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> greaterThan(Function<Root<T>, Expression<? extends K>> attributeProvider, K value) {
        return greaterThan((root, builder) -> attributeProvider.apply(root), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> greaterThan(BiFunction<Root<T>, CriteriaBuilder, Expression<? extends K>> attributeProvider, K value) {
        return predicate((root, builder) -> builder.greaterThan(attributeProvider.apply(root, builder), value), () -> nonNull(value));
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> greaterThanOrEqualTo(SingularAttribute<T, K> attributeProvider, K value) {
        return greaterThanOrEqualTo(root -> root.get(attributeProvider), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> greaterThanOrEqualTo(Function<Root<T>, Expression<? extends K>> attributeProvider, K value) {
        return greaterThanOrEqualTo((root, builder) -> attributeProvider.apply(root), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> greaterThanOrEqualTo(BiFunction<Root<T>, CriteriaBuilder, Expression<? extends K>> attributeProvider, K value) {
        return predicate((root, builder) -> builder.greaterThanOrEqualTo(attributeProvider.apply(root, builder), value), () -> nonNull(value));
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> greaterThanOrNull(SingularAttribute<T, K> attributeProvider, K value) {
        return greaterThanOrNull(root -> root.get(attributeProvider), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> greaterThanOrNull(Function<Root<T>, Expression<? extends K>> attributeProvider, K value) {
        return greaterThanOrNull((root, builder) -> attributeProvider.apply(root), value);
    }

    public <K extends Comparable<? super K>> SpecificationsBuilder<T> greaterThanOrNull(BiFunction<Root<T>, CriteriaBuilder, Expression<? extends K>> attributeProvider, K value) {
        return predicate((root, builder) -> {
            Predicate predicate = builder.greaterThan(attributeProvider.apply(root, builder), value);
            Predicate nullPredicate = builder.isNull(attributeProvider.apply(root, builder));
            return builder.or(predicate, nullPredicate);
        }, () -> nonNull(value));
    }

    public SpecificationsBuilder<T> predicate(BiFunction<Root<T>, CriteriaBuilder, Predicate> attributeProvider) {
        return predicate(attributeProvider, () -> true);
    }

    public SpecificationsBuilder<T> predicate(BiFunction<Root<T>, CriteriaBuilder, Predicate> attributeProvider, Supplier<Boolean> canApply) {
        if (canApply.get()) {
            specificationsList.add((root, query, builder) -> {
                query.distinct(true);
                return attributeProvider.apply(root, builder);
            });
        }
        return this;
    }

    public <K> SpecificationsBuilder<T> joinFetch(SingularAttribute<T, K> attribute) {
        specificationsList.add((root, criteriaQuery, builder) -> {
            if (Long.class != criteriaQuery.getResultType()) {
                criteriaQuery.distinct(true);
                root.fetch(attribute, JoinType.LEFT);
            }

            return builder.isNotNull(root.get(attribute));
        });

        return this;
    }

    private String normalizeString(String value) {
        return value.trim().toLowerCase();
    }

}
