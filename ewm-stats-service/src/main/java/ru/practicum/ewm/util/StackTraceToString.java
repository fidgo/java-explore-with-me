package ru.practicum.ewm.util;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

public class StackTraceToString {
    public static List<String> exec(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return Collections.singletonList(stringWriter.toString());
    }
}
