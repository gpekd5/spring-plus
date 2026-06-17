package org.example.expert.domain.manager.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.enums.ManagerLogStatus;

@Getter
@RequiredArgsConstructor
public class ManagerLogSaveRequest {

    private final Long todoId;
    private final Long managerUserId;
    private final Long requestUserId;
    private final String requestUserEmail;
    private final ManagerLogStatus status;
    private final String failureReason;

    public static ManagerLogSaveRequest success(
            Long todoId,
            Long managerUserId,
            Long requestUserId,
            String requestUserEmail
    ) {
        return new ManagerLogSaveRequest(
                todoId,
                managerUserId,
                requestUserId,
                requestUserEmail,
                ManagerLogStatus.SUCCESS,
                null
        );
    }

    public static ManagerLogSaveRequest fail(
            Long todoId,
            Long managerUserId,
            Long requestUserId,
            String requestUserEmail,
            String failureReason
    ) {
        return new ManagerLogSaveRequest(
                todoId,
                managerUserId,
                requestUserId,
                requestUserEmail,
                ManagerLogStatus.FAIL,
                failureReason
        );
    }
}
