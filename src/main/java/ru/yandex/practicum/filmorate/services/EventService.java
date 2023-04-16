package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storages.EventStorage;

import java.util.List;

@Slf4j
@Service
public class EventService {
    private final EventStorage eventStorage;


    public EventService(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public void addEvent(Event event) {
        eventStorage.addEvent(event);
        log.info("Добавлен ивент в ленту событий: " + event);
    }

    public List<Event> findByUserId(int userId) {
        log.info("Запрос из ленты событий по пользователю с id: {}", userId);
        return eventStorage.findByUserId(userId);
    }
}