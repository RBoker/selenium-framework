package com.rboker.automation.tests.unit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SanityUnitTest {

    @Test
    void shouldRunUnitTests() {
        assertThat(2 + 2).isEqualTo(4);
    }
}
