package uz.everbest.requestmanagement.domain.dto;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Data
@Builder
public class SendMedia {

    private String method;
    private String chatId;
    private InputFile document;
    private InputFile video;
    private InputFile photo;
    private InputFile audio;
    private InputFile voice;

}
