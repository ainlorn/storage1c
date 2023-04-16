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
    MISSING_BODY(3, 400, "Отсутствует тело запроса"),
    ACCESS_DENIED(4, 403, "Доступ к ресурсу запрещён"),
    UNAUTHORIZED(5, 401, "Для доступа к этому ресурсу необходимо войти"),

    // user account
    EMAIL_IN_USE(100, 400, "Адрес электронной почты уже используется"),
    USERNAME_IN_USE(101, 400, "Имя пользователя уже используется"),
    WRONG_CREDENTIALS(102, 400, "Неправильный логин или пароль"),
    USER_NOT_FOUND(103, 404, "Пользователь не найден");

    int code;
    int httpCode;
    String message;
}
