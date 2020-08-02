package sample;

import org.apache.commons.lang3.JavaVersion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BomNotUpdatedLang3Test {
    @Test
    void ensureLang3Version3_9NotUpdated() {
        // v3.9 on classpath, but we expect v3.10 because of updated BOM
        assertThat(JavaVersion.class.getProtectionDomain().getCodeSource().toString())
                .contains("commons-lang3-3.9.jar")
                .doesNotContain("commons-lang3-3.10.jar");
    }
}
