package com.doodle.scheduler.application.adapter.out.persistence.timeslot;

import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaEntity;
import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaEntity_;
import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaMapper;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.out.SearchTimeSlotsPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SearchTimeSlotsRepositoryAdapter implements SearchTimeSlotsPort {

    private final EntityManager entityManager;
    private final TimeSlotJpaMapper timeSlotJpaMapper;

    @Override
    public SearchResult searchTimeSlots(UUID ownerId, String status, Instant startTime, Instant endTime, int page, int size) {
        TimeSlotQueryBuilder builder = TimeSlotQueryBuilder.forEntity(entityManager)
                .filterByOwner(ownerId)
                .filterByStatus(status)
                .filterByTimeRange(startTime, endTime)
                .paginate(page, size);

        List<TimeSlot> timeSlots = builder.findAll()
                .orderByStartTimeAsc()
                .getResults()
                .stream()
                .map(timeSlotJpaMapper::toDomain)
                .toList();

        long totalElements = builder.count();

        return new SearchResult(timeSlots, totalElements);
    }

    @RequiredArgsConstructor(staticName = "forEntity")
    private static class TimeSlotQueryBuilder {
        private final EntityManager entityManager;
        private final List<PredicateBuilder> predicateBuilders = new ArrayList<>();
        private int page = 0;
        private int size = Integer.MAX_VALUE;

        private TimeSlotQueryBuilder filterByOwner(UUID ownerId) {
            if (ownerId == null) {
                throw new IllegalArgumentException("ownerId cannot be null");
            }
            predicateBuilders.add((cb, root) -> cb.equal(root.get(TimeSlotJpaEntity_.ownerId), ownerId));
            return this;
        }

        private TimeSlotQueryBuilder filterByStatus(String status) {
            if (status != null && !status.isEmpty()) {
                predicateBuilders.add((cb, root) -> cb.equal(root.get(TimeSlotJpaEntity_.state), status));
            }
            return this;
        }

        private TimeSlotQueryBuilder filterByTimeRange(Instant startTime, Instant endTime) {
            if (startTime != null) {
                predicateBuilders.add((cb, root) ->
                    cb.greaterThanOrEqualTo(root.get(TimeSlotJpaEntity_.startTime), startTime));
            }
            if (endTime != null) {
                predicateBuilders.add((cb, root) ->
                    cb.lessThanOrEqualTo(root.get(TimeSlotJpaEntity_.endTime), endTime));
            }
            return this;
        }

        private TimeSlotQueryBuilder paginate(int page, int size) {
            this.page = page;
            this.size = size;
            return this;
        }

        private QueryExecutor findAll() {
            return new QueryExecutor(entityManager, predicateBuilders, page, size);
        }

        private long count() {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
            Root<TimeSlotJpaEntity> root = query.from(TimeSlotJpaEntity.class);

            List<Predicate> predicates = buildPredicates(criteriaBuilder, root);
            query.select(criteriaBuilder.count(root))
                 .where(predicates.toArray(Predicate[]::new));

            return entityManager.createQuery(query).getSingleResult();
        }

        private List<Predicate> buildPredicates(CriteriaBuilder criteriaBuilder, Root<TimeSlotJpaEntity> root) {
            return predicateBuilders.stream()
                    .map(builder -> builder.build(criteriaBuilder, root))
                    .toList();
        }

        @FunctionalInterface
        private interface PredicateBuilder {
            Predicate build(CriteriaBuilder criteriaBuilder, Root<TimeSlotJpaEntity> root);
        }
    }

    @RequiredArgsConstructor
    private static class QueryExecutor {
        private final EntityManager entityManager;
        private final List<TimeSlotQueryBuilder.PredicateBuilder> predicateBuilders;
        private final int page;
        private final int size;
        private boolean orderByStartTime = false;

        private QueryExecutor orderByStartTimeAsc() {
            this.orderByStartTime = true;
            return this;
        }

        private List<TimeSlotJpaEntity> getResults() {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<TimeSlotJpaEntity> query = criteriaBuilder.createQuery(TimeSlotJpaEntity.class);
            Root<TimeSlotJpaEntity> root = query.from(TimeSlotJpaEntity.class);

            List<Predicate> predicates = buildPredicates(criteriaBuilder, root);
            query.where(predicates.toArray(Predicate[]::new));

            if (orderByStartTime) {
                query.orderBy(criteriaBuilder.asc(root.get(TimeSlotJpaEntity_.startTime)));
            }

            TypedQuery<TimeSlotJpaEntity> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult(page * size);
            typedQuery.setMaxResults(size);

            return typedQuery.getResultList();
        }

        private List<Predicate> buildPredicates(CriteriaBuilder criteriaBuilder, Root<TimeSlotJpaEntity> root) {
            return predicateBuilders.stream()
                    .map(builder -> builder.build(criteriaBuilder, root))
                    .toList();
        }
    }
}
