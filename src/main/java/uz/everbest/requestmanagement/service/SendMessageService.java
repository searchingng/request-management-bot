package uz.everbest.requestmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.everbest.requestmanagement.util.KeyboardUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SendMessageService {

    public SendMessage getMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("HTML")
                .build();
    }

    public SendMessage getButtonMessage(Long chatId, String text, String... buttons) {
        var sendMessage = getMessage(chatId, text);
        sendMessage.setReplyMarkup(KeyboardUtil.markup(buttons));
        return sendMessage;
    }

    public SendMessage getHorizontalInlineMessage(Long chatId, String text, InlineKeyboardButton... buttons) {
        var sendMessage = getMessage(chatId, text);
        sendMessage.setReplyMarkup(KeyboardUtil.inlineMarkup(Collections.singletonList(List.of(buttons))));
        return sendMessage;
    }

    public SendMessage getVerticalInlineMessage(Long chatId, String text, InlineKeyboardButton... buttons) {
        var rows = Arrays.stream(buttons)
                .map(List::of)
                .toList();

        var sendMessage = getMessage(chatId, text);
        sendMessage.setReplyMarkup(KeyboardUtil.inlineMarkup(rows));
        return sendMessage;
    }

}
