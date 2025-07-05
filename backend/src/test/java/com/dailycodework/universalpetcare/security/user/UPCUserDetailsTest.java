package com.dailycodework.universalpetcare.security.user;

import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UPCUserDetailsTest {

    @Test
    void testBuildUserDetails() {
        // Arrange
        Role role1 = new Role();
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setEnabled(true);
        user.setRoles(Set.of(role1, role2));

        // Act
        UPCUserDetails userDetails = UPCUserDetails.buildUserDetails(user);

        // Assert
        assertEquals(1L, userDetails.getId());
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());

        List<String> authorities = userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();
        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertEquals(2, authorities.size());
    }

    @Test
    void testUserDetailsMethods() {
        UPCUserDetails userDetails = new UPCUserDetails(
                10L,
                "john@example.com",
                "securepwd",
                true,
                List.of(() -> "ROLE_PATIENT")
        );

        assertEquals("john@example.com", userDetails.getUsername());
        assertEquals("securepwd", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertEquals(List.of("ROLE_PATIENT"), roles);
    }
}
