package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final ResultSetExtractor<List<User>> RSE = new ResultSetExtractor<>() {
        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<User> list = new ArrayList<>();

            User prev = null;
            while (rs.next()) {
                int userId = rs.getInt("id");

                if (prev != null && userId == prev.id()) { //just add another role
                    prev.getRoles().add(getRole(rs.getString("role")));
                    continue;
                }

                User u = new User();

                u.setId(userId);
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setEnabled(rs.getBoolean("enabled"));
                u.setRegistered(rs.getDate("registered"));
                u.setCaloriesPerDay(rs.getInt("calories_per_day"));

                Set<Role> roles = new HashSet<>();

                Role role;
                if ((role = getRole(rs.getString("role"))) != null)
                    roles.add(role);

                u.setRoles(roles);

                prev = u;
                list.add(u);
            }
            return list;
        }

        private Role getRole(String str) {
            return (str == null ? null : Role.valueOf(str));
        }
    };

    private static class BatchPreparedStatementSetterRoles implements BatchPreparedStatementSetter {
        private final List<Role> list;
        private final int userId;

        private final boolean updateMode;

        public BatchPreparedStatementSetterRoles(Set<Role> set, int userId) {
            this(set, userId, false);
        }

        public BatchPreparedStatementSetterRoles(Set<Role> set, int userId, boolean updateMode) {
            this.list = new ArrayList<>(set);
            this.userId = userId;
            this.updateMode = updateMode;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setInt((updateMode ? 2 : 1), userId);
            ps.setString((updateMode ? 1 : 2), list.get(i).toString());
        }

        @Override
        public int getBatchSize() {
            return list.size();
        }
    }

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());

            jdbcTemplate.batchUpdate("INSERT INTO user_role (user_id, role) VALUES (?, ?)",
                    new BatchPreparedStatementSetterRoles(user.getRoles(), newKey.intValue()));
        } else {
            if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password,
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
                return null;
            }

            jdbcTemplate.batchUpdate("UPDATE user_role SET role = ? WHERE user_id = ? ",
                    new BatchPreparedStatementSetterRoles(user.getRoles(), user.id(), true));
        }
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT OUTER JOIN user_role ON (users.id = user_role.user_id) WHERE id=?", RSE, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT OUTER JOIN user_role ON (users.id = user_role.user_id) WHERE email=?", RSE, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users LEFT OUTER JOIN user_role ON (users.id = user_role.user_id) ORDER BY name, email", RSE);
    }

}
