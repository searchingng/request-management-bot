package uz.everbest.requestmanagement.util;

import java.util.Random;

public class PasswordUtil {

    public static String generatePassword(String key) {
        String[] delimeters = new String[]{"@", "_", "!", ":", "$", "%", "&", "#", "?", "*"};
        Random random = new Random();

        String chr = delimeters[random.nextInt(10)];
        int number = random.nextInt(1000);

        return String.format("%s%s%03d", key, chr, number);
    }

    public static String generateClientNumber(String inn, Integer number) {
        return String.format("%s%03d", inn, number);
    }

}
