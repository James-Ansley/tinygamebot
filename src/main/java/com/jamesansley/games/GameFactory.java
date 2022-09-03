package com.jamesansley.games;

import com.jamesansley.games.exceptions.UnparseableGameString;
import com.jamesansley.games.join4.Join4;
import com.jamesansley.games.join4.Join4Parser;

public class GameFactory {
    public static Game fromString(String data) throws UnparseableGameString {
        return switch (Parser.getGameName(data)) {
            case Join4.DISPLAY_NAME -> new Join4Parser(data).parse();
            default -> throw new UnparseableGameString(data);
        };
    }
}
