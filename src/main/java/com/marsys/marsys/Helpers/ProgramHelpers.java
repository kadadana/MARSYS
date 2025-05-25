package com.marsys.marsys.Helpers;

import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class ProgramHelpers {

    public static void adjustTableHeight(TableView<?> tableView) {
        int rows = tableView.getItems().size();
        double rowHeight = tableView.getFixedCellSize();
        tableView.setFixedCellSize(43);

        if (rowHeight <= 0) {
            rowHeight = 43;
        }

        double headerHeight = 43;

        double newHeight = headerHeight + ((rows) * rowHeight);

        double maxHeight = 1200;
        if (newHeight > maxHeight) {
            newHeight = maxHeight;
        }

        tableView.setPrefHeight(newHeight);
    }


    public static String getStringDateByLocalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return date.format(formatter);

    }

    public static String getStringDateTimeByLocalDateTime(LocalDateTime date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        return date.format(dateTimeFormatter);

    }

    public static LocalDate getLocalDateByStringDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    }

    public static LocalDateTime getLocalDateTimeByStringDateTime(String date) {
        return LocalDateTime.parse(date.trim(), DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"));
    }


    public static Double get2DecimalDouble(double number) {
        String strRatedPrice = String.format(Locale.US, "%.2f", number);
        number = Double.parseDouble(strRatedPrice);
        return number;
    }

    public static Double get2DecimalDoubleFromString(String number) {
        double doubleNumber = Double.parseDouble(number);
        String strNumber = String.format(Locale.US, "%.2f", doubleNumber);
        return Double.parseDouble(strNumber);
    }

    public static String get2DecimalString(double number) {
        return String.format(Locale.US, "%.2f", number);
    }

    public static String get2DecimalStringFromString(String number) {
        double doubleNumber = Double.parseDouble(number);
        return String.format(Locale.US, "%.2f", doubleNumber);
    }

    public static TextFormatter<String> unaryOperator(int i) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            String regex = String.format("\\d{0,%d}", i);
            if (newText.matches(regex)) {
                return change;
            }
            return null;
        };

        return new TextFormatter<>(filter);
    }

    public static String getPercentageDisplay(double rate) {
        return String.format("%.2f%%", rate * 100);
    }

}
