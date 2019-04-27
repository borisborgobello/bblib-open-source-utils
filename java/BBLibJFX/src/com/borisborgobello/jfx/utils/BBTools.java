/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 *
 * @author borisborgobello
 */
public class BBTools {

    //SystemUtils.IS_OS_WINDOWS; requires/based on commons.lang3.SystemUtils
    public static final boolean IS_WINDOBE() {
        try {
            return Class.forName("org.apache.commons.lang3.SystemUtils").getField("IS_OS_WINDOWS").getBoolean(null);
        } catch (Exception e) {
            throw new RuntimeException("org.apache.commons.lang3.SystemUtils is required");
        }
    }

    public static final String cleanString(String s) {
        return s.replaceAll("[^\\w\\s]", "");
    } // remove everything but number, alphabet, and .

    public static boolean isFloat(Object s) {
        if (s == null) {
            return false;
        }
        try {
            Double.parseDouble(s.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInt(Object s) {
        if (s == null) {
            return false;
        }
        try {
            Long.parseLong(s.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static final boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static final Comparator<String> INTEGER_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            Integer i1, i2;
            try {
                i1 = Integer.parseInt(s1);
            } catch (Exception e) {
                i1 = null;
            }
            try {
                i2 = Integer.parseInt(s2);
            } catch (Exception e) {
                i2 = null;
            }

            if (i1 == null && i2 == null) {
                if (s1 == null && s2 == null) {
                    return 0;
                }
                if (s1 == null) {
                    return 1;
                }
                return s1.compareToIgnoreCase(s2);
            }
            if (i1 == null) {
                return 1;
            }
            if (i2 == null) {
                return -1;
            }

            return i1 - i2;
        }
    };

    public static final Comparator<String> LONG_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            Long i1, i2;
            try {
                i1 = Long.parseLong(s1);
            } catch (Exception e) {
                i1 = null;
            }
            try {
                i2 = Long.parseLong(s2);
            } catch (Exception e) {
                i2 = null;
            }

            if (i1 == null && i2 == null) {
                if (s1 == null && s2 == null) {
                    return 0;
                }
                if (s1 == null) {
                    return 1;
                }
                return s1.compareToIgnoreCase(s2);
            }
            if (i1 == null) {
                return 1;
            }
            if (i2 == null) {
                return -1;
            }
            if (i1 > i2) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    public static final Comparator<String> STRING_IGN_CASE_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    };

    public static final Comparator<String> STRING_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    public static final Comparator<String> DOUBLE_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            Double i1, i2;
            try {
                i1 = Double.parseDouble(s1);
            } catch (Exception e) {
                i1 = null;
            }
            try {
                i2 = Double.parseDouble(s2);
            } catch (Exception e) {
                i2 = null;
            }

            if (i1 == null && i2 == null) {
                if (s1 == null && s2 == null) {
                    return 0;
                }
                if (s1 == null) {
                    return 1;
                }
                return s1.compareToIgnoreCase(s2);
            }
            if (i1 == null) {
                return 1;
            }
            if (i2 == null) {
                return -1;
            }

            if (i1.equals(i2)) {
                return 0;
            } else if (i1 > i2) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    public static void copyToClipboard(String s) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(s);
        clipboard.setContent(content);
    }

    private static final int BUFFER_SIZE = 8192;

    public static long copy(InputStream source, OutputStream sink)
            throws IOException {
        long nread = 0L;
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

}
