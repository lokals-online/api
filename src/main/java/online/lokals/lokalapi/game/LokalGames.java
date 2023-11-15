package online.lokals.lokalapi.game;

import lombok.Getter;

public enum LokalGames {
    BACKGAMMON("backgammon", "tavla"),
    PISHTI("pishti", "pişti");

    @Getter
    private final String key;
    
    @Getter
    private final String name;

    LokalGames(String key, String name) {
        this.key = key;
        this.name = name;
    }
}
