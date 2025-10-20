package com.woozooha.adonistrack.aspect;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;

import java.util.function.BiConsumer;

@Getter
@Setter
public abstract class PrintableAspect {

    protected static BiConsumer<String, JoinPoint> printer;

    public static void setPrinter(BiConsumer<String, JoinPoint> printer) {
        PrintableAspect.printer = printer;
    }

    protected void print(String message, JoinPoint joinPoint) {
        if (printer == null) {
            return;
        }

        printer.accept(message, joinPoint);
    }

}
