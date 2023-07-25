package uz.everbest.requestmanagement.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.everbest.requestmanagement.domain.dto.ChatResponse;
import uz.everbest.requestmanagement.domain.dto.GroupResponse;
import uz.everbest.requestmanagement.domain.dto.MessageResponse;
import uz.everbest.requestmanagement.domain.dto.SendMedia;
import uz.everbest.requestmanagement.domain.enums.InputMediaType;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
@Slf4j
public class RestService {

    private final String TELEGRAM_URL;
    private final String TOKEN;
    private final RestTemplate restTemplate;

    public Message sendMessage(Long chatId, SendMessage sendMessage){
        ResponseEntity<MessageResponse> response = restTemplate
                .postForEntity(TELEGRAM_URL + TOKEN + "/sendMessage?chat_id=" + chatId, sendMessage, MessageResponse.class);
        return Objects.requireNonNull(response.getBody()).result();
    }

    public GroupResponse sendMediaGroup(String chatId, List<InputMedia> medias){
        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setMedias(medias);
        ResponseEntity<GroupResponse> response = restTemplate
                .postForEntity(TELEGRAM_URL + TOKEN + "/sendMediaGroup?chat_id=" + chatId, mediaGroup, GroupResponse.class);
        return response.getBody();
    }

    public Message execute(String chatId, SendMedia sendMedia) {
        sendMedia.setChatId(chatId);
        ResponseEntity<MessageResponse> response = restTemplate.postForEntity(MessageFormat
                .format(TELEGRAM_URL + TOKEN + "/{0}?chat_id={1}", sendMedia.getMethod(), chatId), sendMedia, MessageResponse.class);
        return Objects.requireNonNull(response.getBody()).result();
    }

    public void forwardMessage(Long fromChatId, Long chatId, Long messageId) {
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setProtectContent(true);
        forwardMessage.setMessageId(messageId.intValue());
        forwardMessage.setFromChatId(fromChatId);
        
        restTemplate
                .postForEntity(TELEGRAM_URL + TOKEN + "/forwardMessage?chat_id=" + chatId,
                        forwardMessage, MessageResponse.class);
    }


    public void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        try {
            restTemplate.postForEntity(
                    TELEGRAM_URL + TOKEN + "/deleteMessage?chat_id=" + chatId,
                    deleteMessage, Objects.class
            );
        } catch (RestClientException ignored) {

        }
    }

    public void editReplyMarkup(Long chatId, Integer messageId, InlineKeyboardMarkup markup) {
        var body = EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(markup)
                .build();

        restTemplate
                .postForEntity(TELEGRAM_URL + TOKEN + "/editMessageReplyMarkup?chat_id=" + chatId,
                        body, MessageResponse.class);
    }

    public void editMessageText(Integer messageId, SendMessage sendMessage) {
        var body = EditMessageText.builder()
                .chatId(sendMessage.getChatId())
                .text(sendMessage.getText())
                .replyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup())
                .messageId(messageId)
                .parseMode("HTML")
                .build();

        restTemplate.postForEntity(TELEGRAM_URL + TOKEN + "/editMessageText?chat_id=" + sendMessage.getChatId(),
                body, MessageResponse.class);
    }

    public void sendToast(String requestCallBackId, String text) {
        try {
            var body = AnswerCallbackQuery.builder()
                    .text(text)
                    .callbackQueryId(requestCallBackId)
                    .showAlert(true)
                    .build();

            restTemplate.postForEntity(TELEGRAM_URL + TOKEN + "/answerCallbackQuery", body, Object.class);
        } catch (HttpClientErrorException.BadRequest e) {
            System.out.println(e.getMessage());
        }
    }

    public Chat getChat(Long chatId) {
        var response = restTemplate.getForEntity(TELEGRAM_URL + TOKEN + "/getChat?chat_id=" + chatId, ChatResponse.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null)
            return response.getBody().result();

        return null;
    }


}
