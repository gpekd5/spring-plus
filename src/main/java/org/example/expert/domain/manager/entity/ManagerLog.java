package org.example.expert.domain.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.manager.enums.ManagerLogStatus;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "log")
public class ManagerLog extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long todoId;
    private Long managerUserId;
    private Long requestUserId;
    private String requestUserEmail;

    @Enumerated(EnumType.STRING)
    private ManagerLogStatus status;

    private String failureReason;

    public ManagerLog(Long todoId, Long managerUserId, Long requestUserId, String requestUserEmail, ManagerLogStatus status, String failureReason) {
        this.todoId = todoId;
        this.managerUserId = managerUserId;
        this.requestUserId = requestUserId;
        this.requestUserEmail = requestUserEmail;
        this.status = status;
        this.failureReason = failureReason;
    }
}
