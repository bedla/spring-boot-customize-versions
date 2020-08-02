package sample;

import org.apache.commons.lang3.JavaVersion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BomInvalidLang3Test {
    @Test
    void ensureLang3Version3_11NotPresent() {
        assertThat(JavaVersion.class.getProtectionDomain().getCodeSource().toString())
                .contains("commons-lang3-3.9.jar")
                .doesNotContain("commons-lang3-3.11.jar");
    }
}
