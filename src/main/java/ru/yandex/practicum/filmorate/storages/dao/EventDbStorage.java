package ru.yandex.practicum.filmorate.storages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storages.EventStorage;

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
            rs.getTimestamp("TIME_STAMP").getTime()
    ));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addEvent(Event event) {
        String sql = "INSERT INTO EVENTS_FEED (USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "VALUES (:USER_ID, :EVENT_TYPE, :OPERATION, :ENTITY_ID);";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("USER_ID", event.getUserId())
                .addValue("EVENT_TYPE", event.getEventType().name())
                .addValue("OPERATION", event.getOperation().name())
                .addValue("ENTITY_ID", event.getEntityId());
        jdbcTemplate.update(sql, param);
    }

    @Override
    public List<Event> findByUserId(int userId) {
        String sql = "SELECT * FROM EVENTS_FEED WHERE USER_ID = :USER_ID;";
        return jdbcTemplate.query(sql, Map.of("USER_ID", userId), EVENT_ROW_MAPPER);
    }

    @Override
    public void removeAll() {
        jdbcTemplate.update("DELETE FROM EVENTS_FEED; ALTER TABLE EVENTS_FEED ALTER COLUMN EVENT_ID RESTART WITH 1",
                Map.of());
    }
}