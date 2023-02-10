package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Integer, User> db = new ConcurrentHashMap<>();
    private final AtomicInteger count = new AtomicInteger(0);

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return db.remove(id) != null;
    }

    @Override
    public User save(User user) {
        log.info("save {}", user);
        if (user.isNew()) {
            int newId = count.getAndIncrement();
            user.setId(newId);
            db.put(newId, user);
            return user;
        }

        return db.computeIfPresent(user.getId(), (id,oldUser) -> user);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return db.get(id);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        return new ArrayList<>(db.values());
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        for (User u : db.values()) {
            if (u.getEmail().equals(email))
                return u;
        }

        return null;
    }
}
