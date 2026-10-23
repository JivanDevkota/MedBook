package com.nsu.medbook_backend.admin.controller;

import com.nsu.medbook_backend.admin.dto.AdminCreateUserRequest;
import com.nsu.medbook_backend.admin.dto.AdminUpdateUserStatusRequest;
import com.nsu.medbook_backend.admin.service.AdminUserService;
import com.nsu.medbook_backend.auth.dto.UserResponse;
import com.nsu.medbook_backend.common.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only user management. Route-level protection is declared in
 * SecurityConfig; the method annotations double-guard and document intent.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminUserService adminUserService;

    @GetMapping("/users")
    public PageResponse<UserResponse> listUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.ASC, "email"));
        return PageResponse.from(adminUserService.listUsers(search, pageable).map(UserResponse::from));
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        return UserResponse.from(adminUserService.createUser(request));
    }

    @PatchMapping("/users/{id}/status")
    public UserResponse updateStatus(
            @PathVariable Long id, @Valid @RequestBody AdminUpdateUserStatusRequest request
    ) {
        return UserResponse.from(adminUserService.setActive(id, request.active()));
    }
}
