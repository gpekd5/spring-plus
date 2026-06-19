package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 할일 도메인의 생성, 조회, 검색 규칙과 외부 날씨 정보 연동을 담당한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    //lv1_1.코드 개선 퀴즈 - @Transactional의 이해
    //readOnly = true 전역 적용으로 오류 발생
    //@Transactional 별도 지정
    /**
     * 할일 생성 시 현재 날씨를 함께 기록해 이후 날씨 기준 조회가 가능하도록 저장한다.
     *
     * @param authUser 할일 작성자로 사용할 현재 사용자 식별 정보
     * @param todoSaveRequest 저장할 할일 제목과 내용
     * @return 저장된 할일, 날씨, 작성자 정보를 담은 응답
     */
    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    /**
     * 목록 조회에서 날짜 필터는 하루 전체가 포함되도록 시간 범위로 변환해 적용한다.
     *
     * @param page 클라이언트 기준 1부터 시작하는 페이지 번호
     * @param size 한 페이지에 포함할 할일 개수
     * @param weather 날씨 기준으로 제한할 선택 필터
     * @param startDate 조회할 생성일 시작일
     * @param endDate 조회할 생성일 종료일
     * @return 수정일 내림차순으로 정렬된 할일 페이지
     */
    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDateTime startDateTime = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate == null ? null : endDate.atTime(LocalTime.MAX);

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable, weather, startDateTime, endDateTime);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    /**
     * 상세 조회에서 작성자 정보가 필요하므로 사용자까지 함께 조회한 결과를 응답으로 변환한다.
     *
     * @param todoId 조회할 할일 식별자
     * @return 할일 상세 정보와 작성자 정보를 담은 응답
     */
    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    /**
     * 화면의 복합 검색 조건을 그대로 저장소 검색 조건으로 전달하고 페이지 단위로 제한한다.
     *
     * @param request 검색에 사용할 제목, 작성자 닉네임, 기간 등의 조건
     * @param page 클라이언트 기준 1부터 시작하는 페이지 번호
     * @param size 한 페이지에 포함할 검색 결과 개수
     * @return 검색 조건에 맞는 할일 요약 페이지
     */
    public Page<TodoSearchResponse> searchTodosList(TodoSearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return todoRepository.searchTodoByMultiCondition(request, pageable);
    }
}
