package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.dto.request.ManagerLogSaveRequest;
import org.example.expert.domain.manager.entity.ManagerLog;
import org.example.expert.domain.manager.enums.ManagerLogStatus;
import org.example.expert.domain.manager.repository.ManagerLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerLogService {

    private final ManagerLogRepository managerLogRepository;

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
