package com.rboker.automation.unit;

import com.rboker.automation.core.ScreenshotUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests para ScreenshotUtil (lógica pura).
 */
class ScreenshotUtilUnitTest {

    @Test
    void sanitizeShouldReplaceInvalidCharacters() throws Exception {
        Method sanitize = ScreenshotUtil.class
                .getDeclaredMethod("sanitize", String.class);

        sanitize.setAccessible(true);

        String input = "Meu teste: Selenium / Chrome?";
        String result = (String) sanitize.invoke(null, input);

        assertEquals("Meu_teste__Selenium___Chrome_", result);
    }

    @Test
    void sanitizeShouldReturnDefaultWhenNullOrBlank() throws Exception {
        Method sanitize = ScreenshotUtil.class
                .getDeclaredMethod("sanitize", String.class);

        sanitize.setAccessible(true);

        assertEquals("unknown_test", sanitize.invoke(null, (Object) null));
        assertEquals("unknown_test", sanitize.invoke(null, "   "));
    }
}
