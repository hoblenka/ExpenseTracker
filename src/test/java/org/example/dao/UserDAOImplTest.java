package org.example.dao;

import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDAOImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private UserDAOImpl userDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDAO = new UserDAOImpl(jdbcTemplate);
    }

    @Test
    void testFindByUsernameExists() {
        User expectedUser = new User("testuser", "password123");
        expectedUser.setId(1L);

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(expectedUser);

        User result = userDAO.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertEquals(1L, result.getId());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq("testuser"));
    }

    @Test
    void testFindByUsernameNotExists() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
                .thenThrow(new EmptyResultDataAccessException(1));

        User result = userDAO.findByUsername("nonexistent");

        assertNull(result);
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq("nonexistent"));
    }

    @Test
    void testSave() {
        User user = new User("newuser", "newpassword");
        
        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenAnswer(invocation -> {
                    KeyHolder keyHolder = invocation.getArgument(1);
                    ((GeneratedKeyHolder) keyHolder).getKeyList().add(java.util.Map.of("GENERATED_KEY", 5L));
                    return 1;
                });

        User result = userDAO.save(user);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("newpassword", result.getPassword());
        assertEquals(5L, result.getId());
        verify(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
    }
}