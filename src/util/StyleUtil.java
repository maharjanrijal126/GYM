package util;

// Utility class: shared CSS style strings used across all views
public class StyleUtil {

    // Orange filled button
    public static final String BTN_ORANGE =
        "-fx-background-color: #F57C00; -fx-text-fill: white; " +
        "-fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;";

    // White outlined button
    public static final String BTN_WHITE =
        "-fx-background-color: white; -fx-text-fill: #333; " +
        "-fx-border-color: #ccc; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";

    // Card panel style (white rounded box)
    public static final String CARD =
        "-fx-background-color: white; -fx-background-radius: 10; " +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2); -fx-padding: 16;";

    // Input field style
    public static final String FIELD =
        "-fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; " +
        "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 6 10;";

    // Active status badge
    public static final String BADGE_ACTIVE =
        "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32; " +
        "-fx-background-radius: 4; -fx-padding: 2 8; -fx-font-size: 11;";

    // Expired status badge
    public static final String BADGE_EXPIRED =
        "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828; " +
        "-fx-background-radius: 4; -fx-padding: 2 8; -fx-font-size: 11;";

    // Sidebar nav button - normal
    public static final String NAV_BTN =
        "-fx-background-color: transparent; -fx-text-fill: #444; " +
        "-fx-alignment: CENTER_LEFT; -fx-font-size: 13; -fx-cursor: hand; -fx-padding: 10 20;";

    // Sidebar nav button - selected (orange highlight)
    public static final String NAV_BTN_ACTIVE =
        "-fx-background-color: #FFF3E0; -fx-text-fill: #F57C00; " +
        "-fx-border-color: #F57C00; -fx-border-width: 0 0 0 4; " +
        "-fx-alignment: CENTER_LEFT; -fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 10 20;";
}
