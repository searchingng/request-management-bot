package uz.everbest.requestmanagement.domain.dto;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public record GroupResponse(
        Boolean ok,
        List<Message> result
) {}
