package com.fashionstore.user.controller;

import com.fashionstore.user.dto.UpdateProfileRequest;
import com.fashionstore.user.dto.UserProfileResponse;
import com.fashionstore.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The Gateway injects X-User-Id / X-User-Role headers after validating the JWT,
 * so this service reads identity from those headers instead of re-parsing the token.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestHeader("X-User-Id") Long userId,
                                                               @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ---- Admin endpoints ----

    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers(@RequestHeader("X-User-Role") String role) {
        checkAdmin(role);
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        checkAdmin(role);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private void checkAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new com.fashionstore.user.exception.UnauthorizedException("Chỉ ADMIN mới có quyền thực hiện thao tác này");
        }
    }
}
