package me.tobiasliese.parser.common;

import java.util.Objects;

public record Range(Position from, Position to) {

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other instanceof Range(Position f, Position t)) {
            if (!f.equals(from))
                return false;
            return t.equals(to);
        }
        return false;
    }
}
