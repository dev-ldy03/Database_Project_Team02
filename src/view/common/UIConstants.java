package DB2026Team02.view.common;

import java.awt.*;
import java.util.*;

public class UIConstants {

    public static final Color PRIMARY          = new Color(22, 101, 52);
    public static final Color PRIMARY_DARK     = new Color(5, 46, 22);
    public static final Color PRIMARY_HOVER    = new Color(16, 83, 42);
    public static final Color PRIMARY_LIGHT_BG = new Color(209, 250, 229);

    public static final Color WHITE            = Color.WHITE;
    public static final Color BG               = new Color(248, 249, 250);
    public static final Color SURFACE          = new Color(243, 244, 246);
    public static final Color BORDER           = new Color(229, 231, 235);
    public static final Color TEXT_PRIMARY     = new Color(15,  23,  42);
    public static final Color TEXT_SECONDARY   = new Color(71,  85, 105);
    public static final Color TEXT_MUTED       = new Color(148, 163, 184);

    public static final Color CONFIRMED_FG  = new Color(37, 99, 235);
    public static final Color CONFIRMED_BG  = new Color(219, 234, 254);
    public static final Color PENDING_FG    = new Color(161, 88,  4);
    public static final Color PENDING_BG    = new Color(254, 243, 199);
    public static final Color COMPLETED_FG  = new Color(22, 101, 52);
    public static final Color COMPLETED_BG  = new Color(209, 250, 229);
    public static final Color CANCELLED_FG  = new Color(107, 114, 128);
    public static final Color CANCELLED_BG  = new Color(229, 231, 235);

    public static final Color DANGER        = new Color(185, 28,  28);
    public static final Color DANGER_BG     = new Color(254, 226, 226);
    public static final Color WARNING_FG    = new Color(120, 53,  15);
    public static final Color WARNING_BG    = new Color(254, 243, 199);

    public static final String FONT_FAMILY;
    static {
        Set<String> avail = new HashSet<>(
            Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        String[] candidates = {"맑은 고딕", "Apple SD Gothic Neo", "Nanum Gothic", "SansSerif"};
        String chosen = "SansSerif";
        for (String c : candidates) { if (avail.contains(c)) { chosen = c; break; } }
        FONT_FAMILY = chosen;
    }

    public static Font f(int style, int size) { return new Font(FONT_FAMILY, style, size); }

    public static final int FRAME_W  = 1280;
    public static final int FRAME_H  = 800;
    public static final int NAVBAR_H = 60;
    public static final int SIDEBAR_W = 200;

    public static final Color SIDEBAR_BG     = new Color(30, 41, 59);
    public static final Color SIDEBAR_ACTIVE = new Color(51, 65, 85);
    public static final Color SIDEBAR_TEXT   = new Color(203, 213, 225);
}
