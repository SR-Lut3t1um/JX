package me.tobiasliese.parser.common;

import java.util.ArrayList;
import java.util.List;

public class GlobalVersionTree {
    private final List<String> lines = new ArrayList<>();

    public GlobalVersionTree() {
        if (lines.isEmpty())
            lines.add("");
    }

    public String getContent() {
        return String.join("\n", lines);
    }

    void applyEdit(Edit edit) throws IllegalStateException {
        boolean isInPlaceEdit = edit.range().to().equals(edit.range().from());
        if (isInPlaceEdit && !edit.change().isEmpty()) {
            insert(edit);
        } else if (!isInPlaceEdit && !edit.change().isEmpty()) {
            replace(edit);
        } else if (!isInPlaceEdit) {
            delete(edit);
        } else {
            throw new IllegalStateException("Unsupported edit request");
        }
    }

    private void insert(Edit edit) throws IndexOutOfBoundsException {
        String change = edit.change();
        List<String> changes = new ArrayList<>(List.of(change.split("\n", -1)));

        int lineIndex = edit.range().to().line();
        var charIndex = edit.range().to().character();

        var changePrefix = lines.get(lineIndex).substring(0, charIndex);
        var changeSuffix = lines.get(lineIndex).substring(charIndex);

        var builderFirstLine = new StringBuilder(changePrefix);
        if (changes.size() == 1) {
            builderFirstLine.insert(charIndex, changes.getFirst());
            builderFirstLine.append(changeSuffix);
            lines.set(lineIndex, builderFirstLine.toString());
            return;
        }
        builderFirstLine.append(changes.getFirst());

        changes.set(changes.size() - 1, changes.getLast() + changeSuffix);

        changes.removeFirst();
        lines.set(lineIndex, builderFirstLine.toString());

        lines.addAll(lineIndex + 1, changes);
    }

    private void replace(Edit edit) {
        var from = edit.range().from();
        var to = edit.range().to();
        List<String> changes = new ArrayList<>(List.of(edit.change().split("\n", -1)));

        if (from.line() == to.line()) {
            String line = lines.get(from.line());
            String newLine = line.substring(0, from.character()) +
                    changes.getFirst() +
                    line.substring(to.character());
            lines.set(from.line(), newLine);

            if (changes.size() > 1) {
                changes.removeFirst();
                lines.addAll(from.line() + 1, changes);
            }
        } else {
            String firstLinePrefix = lines.get(from.line()).substring(0, from.character());
            String lastLineSuffix = lines.get(to.line()).substring(to.character());

            if (changes.size() == 1) {
                lines.set(from.line(), firstLinePrefix + changes.getFirst() + lastLineSuffix);
            } else {
                List<String> newLines = new ArrayList<>(changes);
                newLines.set(0, firstLinePrefix + newLines.getFirst());
                newLines.set(newLines.size() - 1, newLines.getLast() + lastLineSuffix);

                int count = to.line() - from.line() + 1;
                for (int i = 0; i < count; i++) {
                    lines.remove(from.line());
                }
                lines.addAll(from.line(), newLines);
            }
        }
    }

    private void delete(Edit edit) {
        var from = edit.range().from();
        var to = edit.range().to();

        if (from.line() == to.line()) {
            String line = lines.get(from.line());
            String newLine = line.substring(0, from.character()) + line.substring(to.character());
            lines.set(from.line(), newLine);
        } else {
            String firstPart = lines.get(from.line()).substring(0, from.character());
            String lastPart = lines.get(to.line()).substring(to.character());

            lines.set(from.line(), firstPart + lastPart);

            if (to.line() >= from.line() + 1) {
                lines.subList(from.line() + 1, to.line() + 1).clear();
            }
        }
    }
}
