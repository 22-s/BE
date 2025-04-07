package dgu.sw.domain.notification.controller;

import dgu.sw.domain.notification.dto.NotificationResponse;
import dgu.sw.domain.notification.service.NotificationService;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.ApiResponse;
import dgu.sw.global.exception.UserException;
import dgu.sw.global.status.ErrorStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
@Tag(name = "Notification Controller", description = "알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "유저의 알림 목록을 반환합니다.")
    public ApiResponse<List<NotificationResponse>> getNotifications(Authentication authentication) {
        User user = getUser(authentication);
        return ApiResponse.onSuccess(notificationService.getNotifications(user));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    public ApiResponse<String> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {
        User user = getUser(authentication);
        notificationService.markAsRead(notificationId, user);
        return ApiResponse.onSuccess("알림을 읽음 처리했습니다.");
    }

    private User getUser(Authentication authentication) {
        return userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));
    }

    @PostMapping("/test")
    @Operation(summary = "알림 테스트", description = "지정된 사용자에게 테스트 알림을 전송합니다.")
    public ApiResponse<String> sendTestNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String body
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        notificationService.sendNotification(user, title, body);
        return ApiResponse.onSuccess("테스트 알림이 전송되었습니다.");
    }
}