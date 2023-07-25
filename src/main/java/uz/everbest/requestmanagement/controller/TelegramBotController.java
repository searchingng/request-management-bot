package uz.everbest.requestmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.everbest.requestmanagement.service.BotService;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class TelegramBotController {

    private final BotService botService;

    @PostMapping
    public void getUpdatesForAdminBot(@RequestBody Update update){
        botService.update(update);
    }

}
