package uz.everbest.requestmanagement.domain.enums;

public enum TicketStatus {

    NEW("\uD83D\uDFE0 NEW"),
    ACCEPTED("\uD83D\uDD35️ ACCEPTED"),
    COMPLETED("\uD83D\uDFE2 COMPLETED");

    TicketStatus(String value) {
        this.value = value;
    }

    private String value;

    public String value() {
        return value;
    }
}
