package com.fijimf.deepfijomega.utils;

import com.fijimf.deepfijomega.entity.schedule.Team;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeamUtils {
    public static class Color {
        public static final Pattern p3 = Pattern.compile("#([0-9a-f])([0-9a-f])([0-9a-f])");
        public static final Pattern p6 = Pattern.compile("#([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})");
        public final short r;
        public final short g;
        public final short b;

        public Color(short r, short g, short b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public double luminance() {
            return (0.299 * r  + 0.587 * g + 0.114 * b )/255.0 ;
        }

        public String toHexString() {
            return String.format("#%02x%02x%02x", r, g, b);
        }

        public static Color fromHexString(String c) {
            Matcher m3 = p3.matcher(c.toLowerCase());
            Matcher m6 = p6.matcher(c.toLowerCase());
            if (m3.matches()) {
                return new Color(
                        Short.parseShort(m3.group(1), 16),
                        Short.parseShort(m3.group(2), 16),
                        Short.parseShort(m3.group(3), 16)
                );
            } else if (m6.matches()) {
                return new Color(
                        Short.parseShort(m6.group(1), 16),
                        Short.parseShort(m6.group(2), 16),
                        Short.parseShort(m6.group(3), 16)
                );
            } else {
                throw new IllegalArgumentException("Bad format passed to Color.fromHexString " + c);
            }
        }
    }

    public static boolean isColorLight(Team t) {
        return Color.fromHexString(t.getColor1()).luminance()>0.5;
    }

    public static boolean isColorDark(Team t) {
        return !isColorLight(t);
    }

    public static String correctLogoUrl(String originalUrl, boolean isColorLight) {
        if (isColorLight) {
            return darkBackgroundUrl(originalUrl);
        } else {
            return lightBackgroundUrl(originalUrl);
        }
    }

    @NotNull
    public static String lightBackgroundUrl(String originalUrl) {
        return backgroundSpecificUrl(originalUrl, "/bgl/", "/bgd/");
    }

    @NotNull
    public static String darkBackgroundUrl(String originalUrl) {
        return backgroundSpecificUrl(originalUrl, "/bgd/", "/bgl/");
    }

    @NotNull
    public static String backgroundSpecificUrl(String originalUrl, String s1, String s2) {
        if (originalUrl.contains(s1)) {
            return originalUrl;
        } else if (originalUrl.contains(s2)) {
            return originalUrl.replace(s2, s1);
        } else {
            return originalUrl;
        }
    }


    public static void main(String[] args) {
        Color white = new Color((short) 255, (short) 255, (short) 255);
        Color black = new Color((short) 0, (short) 0, (short) 0);

        System.out.println(white.toHexString());
        System.out.println(white.luminance());
        System.out.println(black.toHexString());
        System.out.println(black.luminance());
    }


}
