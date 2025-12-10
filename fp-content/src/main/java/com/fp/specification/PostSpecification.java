package com.fp.specification;

import com.fp.dto.content.ListPostsRequestDTO;
import com.fp.entity.Post;
import com.fp.exception.business.IllegalPageableCursorException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {
    public static Specification<Post> fromListRequest(ListPostsRequestDTO listPostsRequestDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // if (accountId != null)
            if(listPostsRequestDTO.getAccountId() != null) {
                predicates.add(cb.equal(root.get("accountId"), listPostsRequestDTO.getAccountId()));
            }
            // status filter
            if(listPostsRequestDTO.getStatus() != null){
                predicates.add(cb.equal(root.get("status"), Post.Status.valueOf(listPostsRequestDTO.getStatus().toUpperCase())));
            }

            // if (keyword != null)
            if(listPostsRequestDTO.getKeyword() != null){
                predicates.add(cb.like(root.get("content"), "%" + listPostsRequestDTO.getKeyword() + "%"));
            }

            // Create coalesce expression for effectiveAt: coalesce(modified_at, created_at), to be used in pagination and sorting
            Expression<Instant> coalesce = cb.coalesce(root.get("modifiedAt"), root.get("createdAt"));

            // cursor-based pagination
            // 1. if cursor is not blank, decode it to get the last effectiveAt and postId.
            String cursor = listPostsRequestDTO.getCursor();
            Instant cursorEffectiveAt = null;
            Long cursorPostId = null;
            if (cursor != null && !cursor.isBlank()) {
                try {
                    String[] parts = cursor.split("#");
                    cursorEffectiveAt = Instant.parse(parts[0]);
                    cursorPostId = Long.parseLong(parts[1]);
                } catch (DateTimeParseException ex) {
                    throw new IllegalPageableCursorException("Post Specification cursor timestamp parse error", ex);
                } catch (NumberFormatException ex) {
                    throw new IllegalPageableCursorException("Post Specification cursor postId parse error", ex);
                } catch (Exception ex) {
                    throw new IllegalPageableCursorException("Post Specification cursor parse error", ex);
                }
            }
            // 2. Create coalesce expression and add predicates
            if(cursorEffectiveAt != null){
                // WHERE (coalesce(modified_at, created_at) < :cursorEffectiveAt
                // OR (coalesce(modified_at, created_at) = :cursorEffectiveAt AND id < :cursorPostId))
                predicates.add(
                        cb.or(
                                cb.lessThan(coalesce, cursorEffectiveAt),
                                cb.and(
                                        cb.equal(coalesce, cursorEffectiveAt),
                                        cb.lessThan(root.get("id"), cursorPostId)
                                )
                        )
                );
            }

            // Add sorting rules by effectiveAt desc, id desc
            if (query != null) {
                query.orderBy(
                        cb.desc(coalesce),
                        cb.desc(root.get("id"))
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

}
