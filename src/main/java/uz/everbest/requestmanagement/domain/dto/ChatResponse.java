package uz.everbest.requestmanagement.domain.dto;

import org.telegram.telegrambots.meta.api.objects.Chat;

public record ChatResponse(
        Boolean ok,
        Chat result
) {}
