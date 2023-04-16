package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Event {
    private Integer eventId;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int entityId;
    private Long timestamp;
}