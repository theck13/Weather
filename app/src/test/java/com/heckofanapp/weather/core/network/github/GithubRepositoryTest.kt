package com.heckofanapp.weather.core.network.github

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GithubRepositoryTest {
    @Test
    fun versionIgnoreBuildMetadataPrefixWhitespace() {
        assertFalse(
            isNewerVersion(
                tagCurrent = "v3.10.6",
                tagLatest = " v3.10.6 ",
            )
        )
        assertFalse(
            isNewerVersion(
                tagCurrent = "v3.10.6",
                tagLatest = "V3.10.6",
            )
        )
        assertFalse(
            isNewerVersion(
                tagCurrent = "v3.11.0+git.abc123",
                tagLatest = "v3.10.6",
            )
        )
        assertFalse(
            isNewerVersion(
                tagCurrent = "v1.2.3+5-x",
                tagLatest = "v1.2.3",
            )
        )
    }

    @Test
    fun versionInstalledNewerThanRelease() {
            assertFalse(
                isNewerVersion(
                    tagCurrent = "v3.11.0",
                    tagLatest = "v3.10.6",
                )
            )
            assertFalse(
                isNewerVersion(
                    tagCurrent = "v3.10.0",
                    tagLatest = "v3.9.1",
                )
            )
            assertFalse(
                isNewerVersion(
                    tagCurrent = "v3.0.0-beta1",
                    tagLatest = "v2.7.1",
                )
            )
    }

    @Test
    fun versionInstalledSameAsRelease() {
        assertFalse(
            isNewerVersion(
                tagCurrent = "v3.10.6",
                tagLatest = "v3.10.6",
            )
        )
        assertFalse(
            isNewerVersion(
                tagCurrent = "v1.2.3.0",
                tagLatest = "v1.2.3",
            )
        )
    }

    @Test
    fun versionReleasedNewerThanInstalled() {
        assertTrue(
            isNewerVersion(
                tagCurrent = "v3.10.6",
                tagLatest = "v3.11.0",
            )
        )
        assertTrue(
            isNewerVersion(
                tagCurrent = "v3.9.9",
                tagLatest = "v3.10.0",
            )
        )
        assertTrue(
            isNewerVersion(
                tagCurrent = "v3.99.99",
                tagLatest = "v4.0.0",
            )
        )
        assertTrue(
            isNewerVersion(
                tagCurrent = "v1.2.3",
                tagLatest = "v1.2.3.4",
            )
        )
    }

    @Test
    fun versionReleasedNewerThanPreRelease() {
        assertTrue(
            isNewerVersion(
                tagCurrent = "v3.0.0-beta3",
                tagLatest = "v3.0.0",
            )
        )
        assertTrue(
            isNewerVersion(
                tagCurrent = "3.0.0-alpha1",
                tagLatest = "v3.0.0",
            )
        )
        assertTrue(
            isNewerVersion(
                tagCurrent = "v2.0.0-beta.4",
                tagLatest = "v2.0.0",
            )
        )
        assertTrue(
            isNewerVersion(
                tagCurrent = "v3.0.0-beta3",
                tagLatest = "v3.0.1",
            )
        )
        assertFalse(
            isNewerVersion(
                tagCurrent = "v3.0.0",
                tagLatest = "v3.0.0-beta1",
            )
        )
    }

    @Test
    fun versionUnparseableTagsFallBackToTextComparison() {
        assertTrue(
            isNewerVersion(
                tagCurrent = "v3.10.6",
                tagLatest = "latest",
            )
        )
        assertFalse(
            isNewerVersion(
                tagCurrent = "nightly",
                tagLatest = "nightly",
            )
        )
        assertTrue(
            isNewerVersion(
                tagCurrent = "",
                tagLatest = "v3.10.6",
            )
        )
        assertTrue(
            isNewerVersion(
                tagCurrent = "v3.10.6",
                tagLatest = "v99999999999", // Too large for Int.
            )
        )
    }
}
