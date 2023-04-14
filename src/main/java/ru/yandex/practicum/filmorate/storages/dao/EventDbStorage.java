package ru.yandex.practicum.filmorate.storages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storages.EventStorage;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    private final DataSource dataSource;

    @Autowired
    public EventDbStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void addEvent(Event event) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("USER_ID", event.getUserId())
                .addValue("EVENT_TYPE", event.getEventType().name())
                .addValue("OPERATION", event.getOperation())
                .addValue("ENTITY_ID", event.getEntityId())
                .addValue("TIME_STAMP", Timestamp.valueOf(LocalDateTime.now()));
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("EVENTS_FEED")
                .usingGeneratedKeyColumns("EVENT_ID");
        insert.execute(param);
    }

    @Override
    public List<Event> findByUserId(int userId) {
        String sql = "SELECT * FROM EVENTS_FEED WHERE USER_ID = :USER_ID;";
        return jdbcTemplate.query(sql, Map.of("USER_ID", userId), EVENT_ROW_MAPPER);
    }
}