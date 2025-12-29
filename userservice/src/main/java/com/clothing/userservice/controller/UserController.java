package com.clothing.userservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clothing.userservice.model.User;
import com.clothing.userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        try {
            System.out.println("\n📝 Registration attempt:");
            System.out.println("   Username: " + user.getUsername());
            System.out.println("   Email: " + user.getEmail());
            
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("ROLE_USER"); // ✅ Regular user role
            System.out.println("✅ Password hashed successfully");
            
            User createdUser = userService.createUser(user);
            
            System.out.println("✅ User created with ID: " + createdUser.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", createdUser.getId());
            response.put("username", createdUser.getUsername());
            response.put("email", createdUser.getEmail());
            response.put("role", createdUser.getRole());
            response.put("message", "User registered successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ Registration failed: " + e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("message", "Registration failed");
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ NEW: Admin Registration Endpoint
    @PostMapping("/register-admin")
    public ResponseEntity<Map<String, Object>> registerAdmin(
            @RequestBody User user,
            @RequestHeader(value = "X-Admin-Secret", required = false) String adminSecret) {
        try {
            // Validate admin secret key
            if (!"SUPER_SECRET_ADMIN_KEY_2025".equals(adminSecret)) {
                System.err.println("❌ Unauthorized admin registration attempt");
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Unauthorized admin registration attempt");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            System.out.println("\n👑 Admin registration attempt:");
            System.out.println("   Username: " + user.getUsername());
            System.out.println("   Email: " + user.getEmail());
            
            // Hash password and set admin role
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("ROLE_ADMIN"); // ✅ Admin role
            user.setEnabled(true);
            
            System.out.println("✅ Password hashed successfully");
            
            User createdUser = userService.createUser(user);
            
            System.out.println("✅ Admin created with ID: " + createdUser.getId());
            System.out.println("   Role: " + createdUser.getRole());
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", createdUser.getId());
            response.put("username", createdUser.getUsername());
            response.put("email", createdUser.getEmail());
            response.put("role", createdUser.getRole());
            response.put("message", "Admin registered successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ Admin registration failed: " + e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("message", "Admin registration failed");
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            System.out.println("🔍 Fetching user with ID: " + id);
            User user = userService.getUserById(id);
            
            System.out.println("✅ User found: " + user.getUsername());
            return ResponseEntity.ok(user);
            
        } catch (RuntimeException e) {
            System.err.println("❌ User not found: " + e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("🔍 Fetching all users");
        List<User> users = userService.getAllUsers();
        System.out.println("✅ Found " + users.size() + " users");
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            System.out.println("📝 Update attempt for user ID: " + id);
            System.out.println("   New firstName: " + user.getFirstName());
            System.out.println("   New lastName: " + user.getLastName());
            System.out.println("   New phoneNumber: " + user.getPhoneNumber());
            
            user.setPassword(null);
            
            User updatedUser = userService.updateUser(id, user);
            
            System.out.println("✅ User updated successfully");
            return ResponseEntity.ok(updatedUser);
            
        } catch (RuntimeException e) {
            System.err.println("❌ Update failed: " + e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            System.out.println("🗑️ Delete attempt for user ID: " + id);
            
            userService.deleteUser(id);
            
            System.out.println("✅ User deleted successfully");
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ Delete failed: " + e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}