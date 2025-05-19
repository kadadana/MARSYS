package com.marsys.marsys.Helpers;

import javafx.scene.control.TableView;

public class TableViewHelper {

    public static void adjustTableHeight(TableView<?> tableView) {
        int rows = tableView.getItems().size();
        double rowHeight = tableView.getFixedCellSize();
        tableView.setFixedCellSize(30);

        if (rowHeight <= 0) {
            rowHeight = 30;
        }

        double headerHeight = 30;

        double newHeight = headerHeight + ((rows) * rowHeight);

        double maxHeight = 1200;
        if (newHeight > maxHeight) {
            newHeight = maxHeight;
        }

        tableView.setPrefHeight(newHeight);
    }
}
