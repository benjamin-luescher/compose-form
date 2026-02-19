package ch.benlu.composeform.validators

import org.junit.Test
import org.junit.Assert.*

class MinLengthValidatorTest {

    @Test
    fun validate_withStringExactlyMinLength_returnsTrue() {
        // Arrange
        val minLength = 5
        val validator = MinLengthValidator(minLength)
        val testString = "12345" // exactly 5 characters
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertTrue("String with exactly minimum length should pass validation", result)
    }

    @Test
    fun validate_withStringLongerThanMinLength_returnsTrue() {
        // Arrange
        val minLength = 3
        val validator = MinLengthValidator(minLength)
        val testString = "abcdef" // 6 characters > 3
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertTrue("String longer than minimum length should pass validation", result)
    }

    @Test
    fun validate_withStringShorterThanMinLength_returnsFalse() {
        // Arrange
        val minLength = 5
        val validator = MinLengthValidator(minLength)
        val testString = "abc" // 3 characters < 5
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertFalse("String shorter than minimum length should fail validation", result)
    }

    @Test
    fun validate_withEmptyStringAndZeroMinLength_returnsTrue() {
        // Arrange
        val minLength = 0
        val validator = MinLengthValidator(minLength)
        val testString = "" // 0 characters
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertTrue("Empty string should pass validation when min length is 0", result)
    }

    @Test
    fun validate_withEmptyStringAndPositiveMinLength_returnsFalse() {
        // Arrange
        val minLength = 1
        val validator = MinLengthValidator(minLength)
        val testString = "" // 0 characters < 1
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertFalse("Empty string should fail validation when min length is positive", result)
    }

    @Test
    fun validate_withNullValue_returnsFalse() {
        // Arrange
        val minLength = 1
        val validator = MinLengthValidator(minLength)
        
        // Act
        val result = validator.validate(null)
        
        // Assert
        assertFalse("Null value should fail validation", result)
    }

    @Test
    fun validate_withNullValueAndZeroMinLength_returnsFalse() {
        // Arrange
        val minLength = 0
        val validator = MinLengthValidator(minLength)
        
        // Act
        val result = validator.validate(null)
        
        // Assert
        assertFalse("Null value should fail validation even with zero min length", result)
    }

    @Test
    fun validate_withWhitespaceString_countsWhitespaceCharacters() {
        // Arrange
        val minLength = 3
        val validator = MinLengthValidator(minLength)
        val testString = "   " // 3 whitespace characters
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertTrue("Whitespace characters should be counted in length", result)
    }

    @Test
    fun validate_withSpecialCharacters_countsSpecialCharacters() {
        // Arrange
        val minLength = 5
        val validator = MinLengthValidator(minLength)
        val testString = "!@#$%" // 5 special characters
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertTrue("Special characters should be counted in length", result)
    }

    @Test
    fun validate_withUnicodeCharacters_countsUnicodeCharacters() {
        // Arrange
        val minLength = 3
        val validator = MinLengthValidator(minLength)
        val testString = "🌟🎉🎊" // 3 Unicode emoji characters
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertTrue("Unicode characters should be counted in length", result)
    }

    @Test
    fun validate_withSingleCharacterMinLength1_returnsTrue() {
        // Arrange
        val minLength = 1
        val validator = MinLengthValidator(minLength)
        val testString = "a" // 1 character
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertTrue("Single character should pass validation with min length 1", result)
    }

    @Test
    fun validate_withVeryLongString_returnsTrue() {
        // Arrange
        val minLength = 10
        val validator = MinLengthValidator(minLength)
        val testString = "a".repeat(1000) // 1000 characters
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertTrue("Very long string should pass validation", result)
    }

    @Test
    fun validate_withNegativeMinLength_alwaysReturnsTrue() {
        // Arrange
        val minLength = -1
        val validator = MinLengthValidator(minLength)
        val testString = "" // 0 characters > -1
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertTrue("Negative min length should allow any non-null string", result)
    }

    @Test
    fun validate_withNegativeMinLengthAndNull_returnsTrue() {
        // Arrange
        val minLength = -1
        val validator = MinLengthValidator(minLength)
        
        // Act
        val result = validator.validate(null)
        
        // Assert
        assertTrue("Null should pass validation with negative min length (null treated as length -1)", result)
    }

    @Test
    fun validate_withLargeMinLength_returnsFalseForShorterStrings() {
        // Arrange
        val minLength = 100
        val validator = MinLengthValidator(minLength)
        val testString = "short" // 5 characters < 100
        
        // Act
        val result = validator.validate(testString)
        
        // Assert
        assertFalse("Short string should fail validation with large min length", result)
    }

    @Test
    fun errorText_withDefaultMessage_returnsExpectedText() {
        // Arrange
        val minLength = 5
        val validator = MinLengthValidator(minLength)
        
        // Act & Assert
        assertEquals("Default error message should match expected text", 
                    "This field is too short", 
                    validator.errorText)
    }

    @Test
    fun errorText_withCustomMessage_returnsCustomText() {
        // Arrange
        val minLength = 3
        val customMessage = "Password must be at least 3 characters"
        val validator = MinLengthValidator(minLength, customMessage)
        
        // Act & Assert
        assertEquals("Custom error message should be returned", 
                    customMessage, 
                    validator.errorText)
    }

    @Test
    fun errorText_withNullCustomMessage_returnsDefaultText() {
        // Arrange
        val minLength = 5
        val validator = MinLengthValidator(minLength, null)
        
        // Act & Assert
        assertEquals("Null custom message should fallback to default", 
                    "This field is too short", 
                    validator.errorText)
    }

    @Test
    fun validate_boundaryValueTesting_worksCorrectly() {
        // Test boundary values around min length
        val minLength = 5
        val validator = MinLengthValidator(minLength)
        
        // Test length 4 (one less than min)
        assertFalse("Length 4 should fail with min length 5", 
                   validator.validate("1234"))
        
        // Test length 5 (exactly min)
        assertTrue("Length 5 should pass with min length 5", 
                  validator.validate("12345"))
        
        // Test length 6 (one more than min)
        assertTrue("Length 6 should pass with min length 5", 
                  validator.validate("123456"))
    }
}