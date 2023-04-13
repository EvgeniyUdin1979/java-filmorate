package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.List;

public interface EventStorage {
    void addEvent(int userId, EventType eventType, Operation operation, int entityId);

    List<Event> findByUserId(int userId);
}