package com.msp31.storage1c.common.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Status {
    UNKNOWN(-1, 500, "Неизвестная ошибка"),
    OK(0, 200, null),
    VALIDATION_ERROR(1, 400, "Ошибка валидации"),
    METHOD_NOT_ALLOWED(2, 405, "Метод не разрешён"),

    // registration
    EMAIL_IN_USE(100, 400, "Адрес электронной почты уже используется"),
    USERNAME_IN_USE(101, 400, "Имя пользователя уже используется");

    int code;
    int httpCode;
    String message;
}
