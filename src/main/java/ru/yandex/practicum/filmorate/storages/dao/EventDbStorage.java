package ru.yandex.practicum.filmorate.storages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storages.EventStorage;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository("eventDAO")
public class EventDbStorage implements EventStorage {
    private static final RowMapper<Event> EVENT_ROW_MAPPER = ((rs, rowNum) -> new Event(
            rs.getInt("EVENT_ID"),
            rs.getInt("USER_ID"),
            EventType.valueOf(rs.getString("EVENT_TYPE")),
            Operation.valueOf(rs.getString("OPERATION")),
            rs.getInt("ENTITY_ID"),
            rs.getTimestamp("TIME_STAMP")
    ));

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    public EventDbStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void addEvent(int userId, EventType eventType, Operation operation, int entityId) {
        String sql = "INSERT INTO EVENTS_FEED (USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "VALUES (:USER_ID, :EVENT_TYPE, :OPERATION, :ENTITY_ID);";
        jdbcTemplate.update(sql, Map.of("USER_ID", userId,
                "EVENT_TYPE", eventType.name(),
                "OPERATION", operation.name(),
                "ENTITY_ID", entityId));
    }

    @Override
    public List<Event> findByUserId(int userId) {
        String sql = "SELECT * FROM EVENTS_FEED WHERE USER_ID = :USER_ID;";
        return jdbcTemplate.query(sql, Map.of("USER_ID", userId), EVENT_ROW_MAPPER);
    }
}