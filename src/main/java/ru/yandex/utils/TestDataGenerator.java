package ru.yandex.utils;

import lombok.experimental.UtilityClass;
import net.datafaker.Faker;
import ru.yandex.model.User;

@UtilityClass
public class TestDataGenerator {
    private static final Faker faker = new Faker();

    public static User generateUniqueUser() {
        return new User(generateEmail(), generateName(), generatePassword());
    }

    public static String generatePassword() {
        return faker.internet().password(6, 6, false, false, true);
    }

    public static String generateName() {
        return faker.name().firstName();
    }

    public static String generateEmail() {
        return faker.internet().safeEmailAddress();
    }

    public static String generateHash() {
        return faker.internet().uuid();
    }
}
