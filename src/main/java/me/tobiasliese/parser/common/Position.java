package me.tobiasliese.parser.common;

import java.util.Objects;

public record Position(int character, int line) {

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other instanceof Position(int c, int l)) {
            if (c != character)
                return false;
            return l == line;
        }
        return false;
    }
}
