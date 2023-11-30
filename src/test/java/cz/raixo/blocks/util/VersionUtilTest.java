package cz.raixo.blocks.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionUtilTest {


    @Test
    void shouldBeUpdated() {
        assertTrue(VersionUtil.isHigherVersion("2.0", "2.1"));
        assertTrue(VersionUtil.isHigherVersion("2.0", "2.0.1"));
        assertTrue(VersionUtil.isHigherVersion("2.0", "3.0"));
        assertFalse(VersionUtil.isHigherVersion("2.0", "2.0"));
        assertFalse(VersionUtil.isHigherVersion("2.0", "1.0"));
        assertFalse(VersionUtil.isHigherVersion("2.1", "2.0.5"));
    }

}