package util;

import javafx.scene.layout.BorderPane;

// Base class: all dashboard views inherit from this (inheritance)
public class BaseView extends BorderPane {

    // Subclasses override this to build their UI
    public void buildUI() {}

    // Shared orange accent color used across all views
    public static final String ORANGE      = "#F57C00";
    public static final String LIGHT_BG    = "#F5F5F5";
    public static final String WHITE       = "#FFFFFF";
    public static final String DARK_TEXT   = "#1a1a1a";
    public static final String GRAY_TEXT   = "#666666";
    public static final String BORDER_CLR  = "#E0E0E0";
}
