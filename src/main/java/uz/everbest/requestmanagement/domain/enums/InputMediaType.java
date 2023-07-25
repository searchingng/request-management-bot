package uz.everbest.requestmanagement.domain.enums;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import uz.everbest.requestmanagement.domain.dto.SendMedia;

public enum InputMediaType {

    PHOTO {
        @Override
        public SendMedia sendMedia(String fileId) {
            return SendMedia.builder()
                    .method("sendPhoto")
                    .photo(new InputFile(fileId))
                    .build();
        }
    },

    VIDEO {
        @Override
        public SendMedia sendMedia(String fileId) {
            return SendMedia.builder()
                    .method("sendVideo")
                    .video(new InputFile(fileId))
                    .build();
        }
    },

    AUDIO {
        @Override
        public SendMedia sendMedia(String fileId) {
            return SendMedia.builder()
                    .method("sendAudio")
                    .audio(new InputFile(fileId))
                    .build();
        }
    },

    VOICE {
        @Override
        public SendMedia sendMedia(String fileId) {
            return SendMedia.builder()
                    .method("sendVoice")
                    .voice(new InputFile(fileId))
                    .build();
        }
    },

    DOCUMENT {
        @Override
        public SendMedia sendMedia(String fileId) {
            return SendMedia.builder()
                    .method("sendDocument")
                    .document(new InputFile(fileId))
                    .build();
        }
    };

    public SendMedia sendMedia(String fileId) {
        return null;
    }

}
