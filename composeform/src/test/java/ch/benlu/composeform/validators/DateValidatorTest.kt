package ch.benlu.composeform.validators

import org.junit.Test
import org.junit.Assert.*
import java.util.*
import java.util.Calendar.*

class DateValidatorTest {

    @Test
    fun validate_withDateAfterMinDateTime_returnsTrue() {
        // Arrange
        val minDateTime = Calendar.getInstance().apply {
            set(2023, JANUARY, 1, 0, 0, 0)
            set(MILLISECOND, 0)
        }.timeInMillis
        val validator = DateValidator({ minDateTime })
        val testDate = Calendar.getInstance().apply {
            set(2023, JUNE, 15, 12, 30, 0)
            set(MILLISECOND, 0)
        }.time
        
        // Act
        val result = validator.validate(testDate)
        
        // Assert
        assertTrue("Date after minimum should pass validation", result)
    }

    @Test
    fun validate_withDateBeforeMinDateTime_returnsFalse() {
        // Arrange
        val minDateTime = Calendar.getInstance().apply {
            set(2023, JUNE, 1, 0, 0, 0)
            set(MILLISECOND, 0)
        }.timeInMillis
        val validator = DateValidator({ minDateTime })
        val testDate = Calendar.getInstance().apply {
            set(2023, JANUARY, 15, 12, 30, 0)
            set(MILLISECOND, 0)
        }.time
        
        // Act
        val result = validator.validate(testDate)
        
        // Assert
        assertFalse("Date before minimum should fail validation", result)
    }

    @Test
    fun validate_withDateExactlyAtMinDateTime_returnsTrue() {
        // Arrange
        val minDateTime = Calendar.getInstance().apply {
            set(2023, JUNE, 1, 0, 0, 0)
            set(MILLISECOND, 0)
        }.timeInMillis
        val validator = DateValidator({ minDateTime })
        val testDate = Date(minDateTime)
        
        // Act
        val result = validator.validate(testDate)
        
        // Assert
        assertTrue("Date exactly at minimum should pass validation", result)
    }

    @Test
    fun validate_withNullDate_returnsFalse() {
        // Arrange
        val minDateTime = System.currentTimeMillis()
        val validator = DateValidator({ minDateTime })
        
        // Act
        val result = validator.validate(null)
        
        // Assert
        assertFalse("Null date should fail validation", result)
    }

    @Test
    fun validate_withCurrentDateAndPastMinimum_returnsTrue() {
        // Arrange
        val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        val validator = DateValidator({ oneDayAgo })
        val currentDate = Date()
        
        // Act
        val result = validator.validate(currentDate)
        
        // Assert
        assertTrue("Current date should pass validation with past minimum", result)
    }

    @Test
    fun validate_withCurrentDateAndFutureMinimum_returnsFalse() {
        // Arrange
        val oneDayFromNow = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
        val validator = DateValidator({ oneDayFromNow })
        val currentDate = Date()
        
        // Act
        val result = validator.validate(currentDate)
        
        // Assert
        assertFalse("Current date should fail validation with future minimum", result)
    }

    @Test
    fun validate_withVeryOldDate_behavesCorrectly() {
        // Arrange
        val minDateTime = Calendar.getInstance().apply {
            set(2000, JANUARY, 1, 0, 0, 0)
            set(MILLISECOND, 0)
        }.timeInMillis
        val validator = DateValidator({ minDateTime })
        val veryOldDate = Calendar.getInstance().apply {
            set(1990, DECEMBER, 25, 0, 0, 0)
            set(MILLISECOND, 0)
        }.time
        
        // Act
        val result = validator.validate(veryOldDate)
        
        // Assert
        assertFalse("Very old date should fail validation", result)
    }

    @Test
    fun validate_withVeryFutureDate_returnsTrue() {
        // Arrange
        val minDateTime = System.currentTimeMillis()
        val validator = DateValidator({ minDateTime })
        val futureDate = Calendar.getInstance().apply {
            set(2050, DECEMBER, 31, 23, 59, 59)
            set(MILLISECOND, 999)
        }.time
        
        // Act
        val result = validator.validate(futureDate)
        
        // Assert
        assertTrue("Future date should pass validation with current minimum", result)
    }

    @Test
    fun validate_withDynamicMinDateTime_worksCorrectly() {
        // Arrange
        var minDateTime = Calendar.getInstance().apply {
            set(2023, JANUARY, 1, 0, 0, 0)
            set(MILLISECOND, 0)
        }.timeInMillis
        val validator = DateValidator({ minDateTime })
        val testDate = Calendar.getInstance().apply {
            set(2023, JUNE, 1, 0, 0, 0)
            set(MILLISECOND, 0)
        }.time
        
        // Act & Assert - First validation
        assertTrue("Date should pass with initial minimum", 
                  validator.validate(testDate))
        
        // Change minimum to future date
        minDateTime = Calendar.getInstance().apply {
            set(2023, DECEMBER, 1, 0, 0, 0)
            set(MILLISECOND, 0)
        }.timeInMillis
        
        // Act & Assert - Second validation
        assertFalse("Date should fail with updated minimum", 
                   validator.validate(testDate))
    }

    @Test
    fun validate_withMillisecondPrecision_worksCorrectly() {
        // Arrange
        val baseTime = System.currentTimeMillis()
        val minDateTime = baseTime
        val validator = DateValidator({ minDateTime })
        
        // Test with exact same time
        assertTrue("Exact same millisecond should pass", 
                  validator.validate(Date(baseTime)))
        
        // Test with one millisecond later
        assertTrue("One millisecond later should pass", 
                  validator.validate(Date(baseTime + 1)))
        
        // Test with one millisecond earlier
        assertFalse("One millisecond earlier should fail", 
                   validator.validate(Date(baseTime - 1)))
    }

    @Test
    fun validate_withZeroMinDateTime_allowsEpochAndLater() {
        // Arrange
        val validator = DateValidator({ 0L })
        val epochDate = Date(0) // January 1, 1970
        val preEpochDate = Date(-86400000) // One day before epoch
        
        // Act & Assert
        assertTrue("Epoch date should pass validation", 
                  validator.validate(epochDate))
        assertFalse("Pre-epoch date should fail validation", 
                   validator.validate(preEpochDate))
    }

    @Test
    fun validate_withNegativeMinDateTime_allowsEarlierDates() {
        // Arrange
        val validator = DateValidator({ -86400000L }) // One day before epoch
        val epochDate = Date(0)
        val veryOldDate = Date(-172800000) // Two days before epoch
        
        // Act & Assert
        assertTrue("Epoch date should pass with negative minimum", 
                  validator.validate(epochDate))
        assertFalse("Date before negative minimum should fail", 
                   validator.validate(veryOldDate))
    }

    @Test
    fun validate_withSpecificDateScenarios_worksCorrectly() {
        // Test leap year date
        val leapYearDate = Calendar.getInstance().apply {
            set(2024, FEBRUARY, 29, 0, 0, 0) // Feb 29, 2024 (leap year)
            set(MILLISECOND, 0)
        }.time
        val beforeLeapYear = Calendar.getInstance().apply {
            set(2024, JANUARY, 1, 0, 0, 0)
            set(MILLISECOND, 0)
        }.timeInMillis
        val validator = DateValidator({ beforeLeapYear })
        
        assertTrue("Leap year date should pass validation", 
                  validator.validate(leapYearDate))
    }

    @Test
    fun validate_withEndOfYearDates_worksCorrectly() {
        // Arrange
        val minDateTime = Calendar.getInstance().apply {
            set(2023, DECEMBER, 30, 23, 59, 59)
            set(MILLISECOND, 999)
        }.timeInMillis
        val validator = DateValidator({ minDateTime })
        val newYearDate = Calendar.getInstance().apply {
            set(2024, JANUARY, 1, 0, 0, 0)
            set(MILLISECOND, 0)
        }.time
        
        // Act
        val result = validator.validate(newYearDate)
        
        // Assert
        assertTrue("New Year date should pass validation", result)
    }

    @Test
    fun errorText_withDefaultMessage_returnsExpectedText() {
        // Arrange
        val validator = DateValidator({ System.currentTimeMillis() })
        
        // Act & Assert
        assertEquals("Default error message should match expected text", 
                    "This field is not valid.", 
                    validator.errorText)
    }

    @Test
    fun errorText_withCustomMessage_returnsCustomText() {
        // Arrange
        val customMessage = "Date must be today or later"
        val validator = DateValidator({ System.currentTimeMillis() }, customMessage)
        
        // Act & Assert
        assertEquals("Custom error message should be returned", 
                    customMessage, 
                    validator.errorText)
    }

    @Test
    fun errorText_withNullCustomMessage_returnsDefaultText() {
        // Arrange
        val validator = DateValidator({ System.currentTimeMillis() }, null)
        
        // Act & Assert
        assertEquals("Null custom message should fallback to default", 
                    "This field is not valid.", 
                    validator.errorText)
    }

    @Test
    fun validate_boundaryDateTesting_worksCorrectly() {
        // Arrange
        val boundaryTime = Calendar.getInstance().apply {
            set(2023, JUNE, 15, 12, 0, 0)
            set(MILLISECOND, 0)
        }.timeInMillis
        val validator = DateValidator({ boundaryTime })
        
        // Test one second before
        val oneSecondBefore = Date(boundaryTime - 1000)
        assertFalse("One second before boundary should fail", 
                   validator.validate(oneSecondBefore))
        
        // Test exact boundary
        val exactBoundary = Date(boundaryTime)
        assertTrue("Exact boundary time should pass", 
                  validator.validate(exactBoundary))
        
        // Test one second after
        val oneSecondAfter = Date(boundaryTime + 1000)
        assertTrue("One second after boundary should pass", 
                  validator.validate(oneSecondAfter))
    }

    @Test
    fun validate_withTimezoneConsiderations_usesUtcTime() {
        // Note: Date.time returns UTC milliseconds, so timezone doesn't affect comparison
        val minDateTime = System.currentTimeMillis()
        val validator = DateValidator({ minDateTime })
        val testDate = Date(minDateTime + 1000) // 1 second later
        
        assertTrue("UTC time comparison should work consistently", 
                  validator.validate(testDate))
    }
}