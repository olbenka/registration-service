package ru.croc.project.pollingStationRegistrationService.entities.classes;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import ru.croc.project.pollingStationRegistrationService.entities.User;
import org.springframework.jdbc.core.JdbcTemplate;


@Repository
public class UserDAOImpl implements UserDAO {
    private final JdbcTemplate jdbcTemplate;

    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void registerUser(User user) {
        try {
            String sql = "INSERT INTO users_data_table (chat_id, name) VALUES (?, ?)";
            jdbcTemplate.update(sql, user.getChatId(), user.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findById(long chatId) {
        try {
            String sql = "SELECT * FROM users_data_table WHERE chat_id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{chatId}, new BeanPropertyRowMapper<>(User.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }



}
