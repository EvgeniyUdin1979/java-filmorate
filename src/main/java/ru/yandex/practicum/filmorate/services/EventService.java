package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storages.EventStorage;

import java.util.List;

@Slf4j
@Service
public class EventService {
    private final EventStorage eventStorage;


    public EventService(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public void addEvent(int userId, EventType eventType, Operation operation, int entityId) {
        eventStorage.addEvent(userId, eventType, operation, entityId);
        log.info("Добавлен ивент в ленту событий со следующими значениями: " +
                "userId: {}, eventType: {}, operation: {}, entityId: {}", userId, eventType, operation, entityId);
    }

    public List<Event> findByUserId(int userId) {
        log.info("Запрос из ленты событий по пользователю с id: {}", userId);
        return eventStorage.findByUserId(userId);
    }
}