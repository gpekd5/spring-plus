package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerLogSaveRequest;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.enums.ManagerLogStatus;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 할일 작성자만 담당자를 관리할 수 있도록 검증하고 담당자 변경 이력을 남긴다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final ManagerLogService managerLogService;

    /**
     * 할일 작성자 검증, 담당자 대상 검증, 자기 자신 등록 방지를 통과한 경우 담당자를 등록한다.
     * 성공과 실패 모두 운영 추적을 위해 별도 로그 트랜잭션으로 기록한다.
     *
     * @param authUser 담당자 등록을 요청한 현재 사용자 식별 정보
     * @param todoId 담당자를 등록할 할일 식별자
     * @param managerSaveRequest 담당자로 등록할 사용자 식별자
     * @return 등록된 담당자와 사용자 정보를 담은 응답
     */
    @Transactional
    public ManagerSaveResponse saveManager(AuthUser authUser, long todoId, ManagerSaveRequest managerSaveRequest) {
        // 일정을 만든 유저
        User user = User.fromAuthUser(authUser);
        Long manageUserId = managerSaveRequest.getManagerUserId();

        try{
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new InvalidRequestException("Todo not found"));

            if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
                throw new InvalidRequestException("담당자를 등록하려고 하는 유저가 유효하지 않거나, 일정을 만든 유저가 아닙니다.");
            }

            User managerUser = userRepository.findById(managerSaveRequest.getManagerUserId())
                    .orElseThrow(() -> new InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다."));

            if (ObjectUtils.nullSafeEquals(user.getId(), managerUser.getId())) {
                throw new InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.");
            }

            Manager newManagerUser = new Manager(managerUser, todo);
            Manager savedManagerUser = managerRepository.save(newManagerUser);

            managerLogService.saveLog(new ManagerLogSaveRequest(
                    todoId,
                    manageUserId,
                    user.getId(),
                    user.getEmail(),
                    ManagerLogStatus.SUCCESS,
                    null
            ));

            return new ManagerSaveResponse(
                    savedManagerUser.getId(),
                    new UserResponse(managerUser.getId(), managerUser.getEmail())
            );
        } catch (RuntimeException e) {
            managerLogService.saveLog(ManagerLogSaveRequest.fail(
                    todoId,
                    manageUserId,
                    user.getId(),
                    user.getEmail(),
                    e.getMessage()
            ));

            throw e;
        }
    }

    /**
     * 할일 존재 여부를 확인한 뒤 담당자 목록을 사용자 정보와 함께 응답 형태로 변환한다.
     *
     * @param todoId 담당자 목록을 조회할 할일 식별자
     * @return 해당 할일에 등록된 담당자와 사용자 정보 목록
     */
    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        List<Manager> managerList = managerRepository.findByTodoIdWithUser(todo.getId());

        List<ManagerResponse> dtoList = new ArrayList<>();
        for (Manager manager : managerList) {
            User user = manager.getUser();
            dtoList.add(new ManagerResponse(
                    manager.getId(),
                    new UserResponse(user.getId(), user.getEmail())
            ));
        }
        return dtoList;
    }

    /**
     * 할일 작성자만 담당자 등록을 삭제할 수 있도록 소유자와 할일-담당자 연결을 함께 검증한다.
     *
     * @param authUser 담당자 삭제를 요청한 현재 사용자 식별 정보
     * @param todoId 담당자 등록이 속한 할일 식별자
     * @param managerId 삭제할 담당자 등록 식별자
     */
    @Transactional
    public void deleteManager(AuthUser authUser, long todoId, long managerId) {
        User user = User.fromAuthUser(authUser);

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
            throw new InvalidRequestException("해당 일정을 만든 유저가 유효하지 않습니다.");
        }

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new InvalidRequestException("Manager not found"));

        if (!ObjectUtils.nullSafeEquals(todo.getId(), manager.getTodo().getId())) {
            throw new InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.");
        }

        managerRepository.delete(manager);
    }
}
