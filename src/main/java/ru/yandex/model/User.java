package ru.yandex.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class User {
    String email;
    String name;
    String password;
}
