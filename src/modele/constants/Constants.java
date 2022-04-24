package modele.constants;

import java.awt.Color;

public final class Constants {

    private Constants() {
        // restrict instantiation
    }

    public static final Color WINDOW_BACKGROUND = new Color(143, 112, 82);
    public static final Color BACKGROUND_COLOR = new Color(117, 99, 82);
    public static final Color COLOR_EMPTY = new Color(204, 192, 179);
    public static final Color COLOR_2 = new Color(74, 53, 30);
    public static final Color COLOR_4 = new Color(94, 72, 26);
    public static final Color COLOR_8 = new Color(174, 86, 11);
    public static final Color COLOR_16 = new Color(195, 70, 6);
    public static final Color COLOR_32 = new Color(198, 42, 5);
    public static final Color COLOR_64 = new Color(229, 46, 5);
    public static final Color COLOR_128 = new Color(237, 207, 114);
    public static final Color COLOR_256 = new Color(237, 204, 97);
    public static final Color COLOR_512 = new Color(237, 200, 80);
    public static final Color COLOR_1024 = new Color(237, 197, 63);
    public static final Color COLOR_2048 = new Color(237, 194, 46);
    public static final Color COLOR_OTHER = Color.BLACK;
    //public static final Color COLOR_GAME_OVER = new Color(238, 228, 218, 0.73);

    public static final Color COLOR_VALUE_LIGHT = new Color(249, 246, 242);
    // For tiles >= 8

    public static final Color COLOR_VALUE_DARK = new Color(119, 110, 101);
    // For tiles < 8

}