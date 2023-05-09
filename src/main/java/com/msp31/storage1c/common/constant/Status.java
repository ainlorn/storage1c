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
    USER_NOT_FOUND(103, 404, "Пользователь не найден"),

    // repository
    REPOSITORY_NAME_IN_USE(200, 400, "Имя репозитория уже используется"),
    REPOSITORY_NOT_FOUND(201, 404, "Репозиторий не найден"),
    ACCESS_LEVEL_NOT_FOUND(202, 404, "Уровень доступа не существует"),
    USER_ALREADY_ADDED(203, 400, "Пользователь уже имеет доступ к репозиторию"),
    ILLEGAL_FILE_PATH(204, 400, "Путь к файлу некорректен"),
    TARGET_FILE_IS_A_DIRECTORY(205, 400, "Целевой файл является директорией"),
    NEW_VERSION_IS_IDENTICAL_TO_PREVIOUS(206, 400, "Новая версия файла идентична предыдущей"),
    FILE_NOT_FOUND(207, 404, "Файл не найден"),
    COMMIT_NOT_FOUND(208, 404, "Коммит не найден");

    int code;
    int httpCode;
    String message;
}
