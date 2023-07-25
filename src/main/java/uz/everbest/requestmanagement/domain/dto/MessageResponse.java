package uz.everbest.requestmanagement.domain.dto;

import org.telegram.telegrambots.meta.api.objects.Message;

public record MessageResponse (

    Boolean ok,
    Message result

){}
