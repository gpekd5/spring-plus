package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.dto.request.ManagerLogSaveRequest;
import org.example.expert.domain.manager.entity.ManagerLog;
import org.example.expert.domain.manager.enums.ManagerLogStatus;
import org.example.expert.domain.manager.repository.ManagerLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 담당자 등록 시도 결과를 본 트랜잭션 성공 여부와 분리해 기록한다.
 */
@Service
@RequiredArgsConstructor
public class ManagerLogService {

    private final ManagerLogRepository managerLogRepository;

    /**
     * 담당자 등록 처리의 성공 또는 실패 상태를 별도 트랜잭션으로 저장한다.
     *
     * @param request 할일, 담당자, 요청자, 처리 상태, 실패 사유를 담은 로그 저장 요청
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(ManagerLogSaveRequest request) {
       ManagerLog log = new ManagerLog(
               request.getTodoId(),
               request.getManagerUserId(),
               request.getRequestUserId(),
               request.getRequestUserEmail(),
               request.getStatus(),
               request.getFailureReason()
       );
       managerLogRepository.save(log);
    }
}
