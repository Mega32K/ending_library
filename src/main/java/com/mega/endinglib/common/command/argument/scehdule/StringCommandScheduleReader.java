package com.mega.endinglib.common.command.argument.scehdule;

import com.mega.endinglib.common.command.argument.CommandArgumentType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class StringCommandScheduleReader {
    private static final DynamicCommandExceptionType PARSE_EXCEPTION;

    static {
        PARSE_EXCEPTION = CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException();
    }

    private final StringReader reader;

    public StringCommandScheduleReader(StringReader reader) {
        this.reader = reader;
    }

    public static List<String> parse(StringReader reader) throws CommandSyntaxException {
        return (new StringCommandScheduleReader(reader)).parseBlockEntry();
    }

    private static String cleanString(String string) {
        if (string.isEmpty()) {
            return string;
        } else {
            int endIndex = string.length();
            if (string.charAt(endIndex - 1) == '\\') {
                --endIndex;
            }

            return string.substring(0, endIndex);
        }
    }

    private List<String> parseBlockEntry() throws CommandSyntaxException {
        List<String> list = new ObjectArrayList<>();
        this.reader.skipWhitespace();
        this.reader.expect('{');
        StringBuilder stringBuilder = new StringBuilder();
        int bracketDepth = 1;

        while (this.reader.canRead() && bracketDepth > 0) {
            char c = this.reader.read();
            if (c == '{') {
                ++bracketDepth;
            } else if (c == '}') {
                --bracketDepth;
            }

            boolean isOuterSemicolon = c == ';' && bracketDepth == 1;
            boolean isOuterClosingBrace = c == '}' && bracketDepth == 0;
            if (!isOuterSemicolon && !isOuterClosingBrace) {
                stringBuilder.append(c);
            } else {
                String command = stringBuilder.toString().trim();
                if (!command.isEmpty()) {
                    String parsedCommandLine = CommandArgumentType.command().parse(new StringReader(command));
                    list.add(parsedCommandLine);
                } else if (isOuterSemicolon) {
                    throw PARSE_EXCEPTION.create("Empty command at: " + this.reader.getCursor());
                }

                stringBuilder.setLength(0);
            }
        }

        if (bracketDepth != 0) {
            throw PARSE_EXCEPTION.create("Unclosed brackets");
        } else {
            return list;
        }
    }
}
