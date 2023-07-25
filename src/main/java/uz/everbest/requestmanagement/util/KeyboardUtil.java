package uz.everbest.requestmanagement.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.List;

public class KeyboardUtil {

    public static InlineKeyboardButton inline(String text, String callBack) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callBack)
                .build();
    }

    public static KeyboardButton button(String text) {
        return KeyboardButton.builder()
                .text(text)
                .build();
    }

    public static KeyboardButton requestContact(String text) {
        return KeyboardButton.builder()
                .text(text)
                .requestContact(true)
                .build();
    }

    public static InlineKeyboardMarkup inlineMarkup(List<List<InlineKeyboardButton>> rows) {
        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public static ReplyKeyboardMarkup markup(String... buttons) {
        List<KeyboardRow> rows = Arrays.stream(buttons)
                .map(buttonText -> new KeyboardRow(List.of(button(buttonText))))
                .toList();

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .oneTimeKeyboard(false)
                .resizeKeyboard(true)
                .selective(true)
                .build();
    }


}
