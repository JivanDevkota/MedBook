package com.nsu.medbook_backend.notification.entity;

import com.nsu.medbook_backend.common.entity.BaseEntity;
import com.nsu.medbook_backend.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Message is required")
    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false)
    @Builder.Default
    private boolean readStatus = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum NotificationType {
        APPOINTMENT,
        PRESCRIPTION,
        PAYMENT,
        SYSTEM
    }
}
