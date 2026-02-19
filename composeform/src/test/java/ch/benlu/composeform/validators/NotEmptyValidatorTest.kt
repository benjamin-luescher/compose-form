package ch.benlu.composeform.validators

import org.junit.Test
import org.junit.Assert.*

class NotEmptyValidatorTest {

    @Test
    fun validate_withNullValue_returnsFalse() {
        // Arrange
        val validator = NotEmptyValidator<String>()
        
        // Act
        val result = validator.validate(null)
        
        // Assert
        assertFalse("Null value should be considered empty", result)
    }

    @Test
    fun validate_withNonNullStringValue_returnsTrue() {
        // Arrange
        val validator = NotEmptyValidator<String>()
        
        // Act
        val result = validator.validate("test")
        
        // Assert
        assertTrue("Non-null string should be considered non-empty", result)
    }

    @Test
    fun validate_withEmptyString_returnsTrue() {
        // Arrange
        val validator = NotEmptyValidator<String>()
        
        // Act
        val result = validator.validate("")
        
        // Assert
        assertTrue("Empty string should still be considered non-empty (not null)", result)
    }

    @Test
    fun validate_withWhitespaceString_returnsTrue() {
        // Arrange
        val validator = NotEmptyValidator<String>()
        
        // Act
        val result = validator.validate("   ")
        
        // Assert
        assertTrue("Whitespace string should be considered non-empty", result)
    }

    @Test
    fun validate_withNonNullIntegerValue_returnsTrue() {
        // Arrange
        val validator = NotEmptyValidator<Int>()
        
        // Act
        val result = validator.validate(42)
        
        // Assert
        assertTrue("Non-null integer should be considered non-empty", result)
    }

    @Test
    fun validate_withZeroValue_returnsTrue() {
        // Arrange
        val validator = NotEmptyValidator<Int>()
        
        // Act
        val result = validator.validate(0)
        
        // Assert
        assertTrue("Zero value should be considered non-empty", result)
    }

    @Test
    fun validate_withNullIntegerValue_returnsFalse() {
        // Arrange
        val validator = NotEmptyValidator<Int>()
        
        // Act
        val result = validator.validate(null)
        
        // Assert
        assertFalse("Null integer should be considered empty", result)
    }

    @Test
    fun validate_withCustomObjectValue_returnsTrue() {
        // Arrange
        val validator = NotEmptyValidator<Any>()
        val testObject = Any()
        
        // Act
        val result = validator.validate(testObject)
        
        // Assert
        assertTrue("Non-null object should be considered non-empty", result)
    }

    @Test
    fun errorText_withDefaultMessage_returnsExpectedText() {
        // Arrange
        val validator = NotEmptyValidator<String>()
        
        // Act & Assert
        assertEquals("Default error message should match expected text", 
                    "This field should not be empty", 
                    validator.errorText)
    }

    @Test
    fun errorText_withCustomMessage_returnsCustomText() {
        // Arrange
        val customMessage = "Custom error message"
        val validator = NotEmptyValidator<String>(customMessage)
        
        // Act & Assert
        assertEquals("Custom error message should be returned", 
                    customMessage, 
                    validator.errorText)
    }

    @Test
    fun errorText_withNullCustomMessage_returnsDefaultText() {
        // Arrange
        val validator = NotEmptyValidator<String>(null)
        
        // Act & Assert
        assertEquals("Null custom message should fallback to default", 
                    "This field should not be empty", 
                    validator.errorText)
    }

    @Test
    fun validate_withEmptyList_returnsTrue() {
        // Arrange
        val validator = NotEmptyValidator<List<String>>()
        val emptyList = emptyList<String>()
        
        // Act
        val result = validator.validate(emptyList)
        
        // Assert
        assertTrue("Empty list should be considered non-empty (not null)", result)
    }

    @Test
    fun validate_withNullList_returnsFalse() {
        // Arrange
        val validator = NotEmptyValidator<List<String>>()
        
        // Act
        val result = validator.validate(null)
        
        // Assert
        assertFalse("Null list should be considered empty", result)
    }
}