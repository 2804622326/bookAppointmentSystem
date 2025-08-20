package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.Veterinarian;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DummyUserRepositoryCaller repositoryCaller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExistsByEmail() {
        String email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = repositoryCaller.checkEmailExists(email);
        assertTrue(result);

        verify(userRepository).existsByEmail(email);
    }

    @Test
    void testFindByEmail() {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        Optional<User> result = repositoryCaller.findByEmail(email);
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void testUpdateUser() {
        when(userRepository.updateUser(
                1L, "John", "Doe", "Male", "1234567890"
        )).thenReturn(1); // 模拟成功更新 1 条记录

        int result = repositoryCaller.updateUserData();

        assertEquals(1, result);
        verify(userRepository).updateUser(1L, "John", "Doe", "Male", "1234567890");
    }

    @Test
    void testFindAllByUserType() {
        List<Veterinarian> mockList = List.of(new Veterinarian(), new Veterinarian());

        when(userRepository.findAllByUserType("VET")).thenReturn(mockList);

        List<Veterinarian> result = repositoryCaller.getVetsByType("VET");
        assertEquals(2, result.size());

        verify(userRepository).findAllByUserType("VET");
    }

    @Test
    void testCountByUserType() {
        when(userRepository.countByUserType("ADMIN")).thenReturn(3L);

        long count = repositoryCaller.countUserType("ADMIN");
        assertEquals(3L, count);

        verify(userRepository).countByUserType("ADMIN");
    }

    @Test
    void testUpdateUserEnabledStatus() {
        // 模拟 void 方法，不用 when
        repositoryCaller.disableUser(1L);
        verify(userRepository).updateUserEnabledStatus(1L, false);
    }

    // ===================== Helper Caller Class ======================
    static class DummyUserRepositoryCaller {
        @InjectMocks
        private UserRepository userRepository;

        public boolean checkEmailExists(String email) {
            return userRepository.existsByEmail(email);
        }

        public Optional<User> findByEmail(String email) {
            return userRepository.findByEmail(email);
        }

        public int updateUserData() {
            return userRepository.updateUser(1L, "John", "Doe", "Male", "1234567890");
        }

        public List<Veterinarian> getVetsByType(String type) {
            return userRepository.findAllByUserType(type);
        }

        public long countUserType(String type) {
            return userRepository.countByUserType(type);
        }

        public void disableUser(Long id) {
            userRepository.updateUserEnabledStatus(id, false);
        }
    }
}