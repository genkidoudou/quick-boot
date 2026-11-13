package io.github.genkidoudou.satoken;

import io.github.genkidoudou.common.satoken.SaTokenIgnoreProperties;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SaTokenIgnoreProperties#resolvedIgnoreUrls()} 清洗与去重。
 */
class SaTokenIgnorePropertiesTest {

    @Test
    void resolvedIgnoreUrls_trimsAndDedups() {
        SaTokenIgnoreProperties props = new SaTokenIgnoreProperties();
        props.setIgnoreUrl(new ArrayList<>(List.of(
                " /api/** ",
                "/jimubi/**",
                "/api/**",
                "",
                "   ",
                "/**"
        )));

        List<String> urls = props.resolvedIgnoreUrls();
        assertEquals(List.of("/api/**", "/jimubi/**", "/**"), urls);
    }

    @Test
    void resolvedIgnoreUrls_emptyWhenUnset() {
        assertTrue(new SaTokenIgnoreProperties().resolvedIgnoreUrls().isEmpty());
    }
}
