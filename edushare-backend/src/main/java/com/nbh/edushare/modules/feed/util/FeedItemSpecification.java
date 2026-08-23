package com.nbh.edushare.modules.feed.util;

import com.nbh.edushare.modules.feed.dto.request.FeedSearchInput;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class FeedItemSpecification {

    public static Specification<FeedItem> build(FeedSearchInput input) {
            return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("isPublic")));
//            predicates.add(cb.isNull(root.get("deletedAt")));

            if (input.type() != null) {
                predicates.add(cb.equal(root.get("type"), input.type()));
            }

            if (input.categoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), input.categoryId()));
            }
//                WHERE jsonb_extract_path_text(type_meta, 'level') = 'BEGINNER'
            if (input.level() != null) {
                predicates.add(cb.equal(
                        cb.function("jsonb_extract_path_text", String.class,
                                root.get("typeMeta"), cb.literal("level")),
                        input.level().name()
                ));
            }

            if (input.keyword() != null && !input.keyword().isBlank()) {
                String pattern = "%" + input.keyword().trim().toLowerCase() + "%";

                Predicate matchTitle = cb.like(cb.lower(root.get("title")), pattern);
                Predicate matchAbstract = cb.like(cb.lower(root.get("abstractText")), pattern);

                predicates.add(cb.or(matchTitle, matchAbstract));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
