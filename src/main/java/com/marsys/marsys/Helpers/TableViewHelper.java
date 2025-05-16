package com.marsys.marsys.Helpers;

import javafx.scene.control.TableView;

public class TableViewHelper {

    public static void adjustTableHeight(TableView<?> tableView) {
        int rows = tableView.getItems().size();
        double rowHeight = tableView.getFixedCellSize();
        if (rowHeight <= 0) {
            rowHeight = 31;
        }
        double headerHeight = 28;

        double newHeight = headerHeight + (rows * rowHeight);

        double maxHeight = 1200;
        if (newHeight > maxHeight) {
            newHeight = maxHeight;
        }

        tableView.setPrefHeight(newHeight);
    }
}
