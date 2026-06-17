package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
                );
    }

    @Override
    public Page<TodoSearchResponse> searchTodoByMultiCondition(TodoSearchRequest request, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            builder.and(todo.title.contains(request.getTitle()));
        }

        if (request.getStartDate() != null){
            LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
            builder.and(todo.createdAt.goe(startDateTime));
        }

        if (request.getEndDate() != null){
            LocalDateTime endDateTime = request.getEndDate().plusDays(1).atStartOfDay();
            builder.and(todo.createdAt.lt(endDateTime));
        }

        if (request.getNickname() != null) {
            builder.and(user.nickname.contains(request.getNickname()));
        }

        List<TodoSearchResponse> responses = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        manager.countDistinct(),
                        comment.countDistinct()))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(builder)
                .groupBy(todo.id, todo.title)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(builder)
                .fetchOne();

        if (total == null) {
            total = 0L;
        }

        return new PageImpl<>(responses, pageable, total);
    }


}
