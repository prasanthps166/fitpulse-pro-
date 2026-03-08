package com.fitpulse.pro.data.backup

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalBackupManagerTest {

    @Test
    fun isSupportedBackupSchemaVersion_acceptsCurrentAndPreviousSchema() {
        assertTrue(isSupportedBackupSchemaVersion(1))
        assertTrue(isSupportedBackupSchemaVersion(2))
    }

    @Test
    fun isSupportedBackupSchemaVersion_rejectsOutOfRangeSchema() {
        assertFalse(isSupportedBackupSchemaVersion(0))
        assertFalse(isSupportedBackupSchemaVersion(3))
    }
}
