package ch.benlu.composeform

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive unit tests for FieldState class.
 * Tests state management, validation, error handling, and option selection functionality.
 */
class FieldStateTest {

    private lateinit var mockValidator: Validator<String>
    private lateinit var mockIntValidator: Validator<Int>

    @Before
    fun setUp() {
        // Create mock validators for testing
        mockValidator = object : Validator<String>(
            validate = { it != null && it.isNotEmpty() },
            errorText = "Field cannot be empty"
        ) {}

        mockIntValidator = object : Validator<Int>(
            validate = { it != null && it > 0 },
            errorText = "Value must be positive"
        ) {}
    }

    // Constructor and initialization tests

    @Test
    fun constructor_withDefaultParameters_initializesCorrectly() {
        val initialValue = "test"
        val state = mutableStateOf(initialValue)
        
        val fieldState = FieldState(state = state)
        
        assertEquals(initialValue, fieldState.state.value)
        assertTrue(fieldState.validators.isEmpty())
        assertTrue(fieldState.errorText.isEmpty())
        assertEquals(false, fieldState.isValid.value)
        assertTrue(fieldState.isVisible())
        assertEquals(false, fieldState.hasChanges.value)
        assertTrue(fieldState.options.isEmpty())
        assertNull(fieldState.optionItemFormatter)
    }

    @Test
    fun constructor_withAllParameters_initializesCorrectly() {
        val initialValue = "test"
        val state = mutableStateOf(initialValue)
        val validators = mutableListOf(mockValidator)
        val errorText = mutableStateListOf("Error message")
        val isValid = mutableStateOf(true)
        val isVisible = { false }
        val hasChanges = mutableStateOf(true)
        val options = mutableListOf("option1", "option2")
        val formatter: (String?) -> String = { it ?: "null" }
        
        val fieldState = FieldState(
            state = state,
            validators = validators,
            errorText = errorText,
            isValid = isValid,
            isVisible = isVisible,
            hasChanges = hasChanges,
            options = options,
            optionItemFormatter = formatter
        )
        
        assertEquals(initialValue, fieldState.state.value)
        assertEquals(validators, fieldState.validators)
        assertEquals(errorText, fieldState.errorText)
        assertEquals(true, fieldState.isValid.value)
        assertFalse(fieldState.isVisible())
        assertEquals(true, fieldState.hasChanges.value)
        assertEquals(options, fieldState.options)
        assertEquals(formatter, fieldState.optionItemFormatter)
    }

    // hasError() method tests

    @Test
    fun hasError_whenVisibleValidAndChanged_returnsFalse() {
        val fieldState = FieldState(
            state = mutableStateOf("test"),
            isValid = mutableStateOf(true),
            isVisible = { true },
            hasChanges = mutableStateOf(true)
        )
        
        assertFalse(fieldState.hasError())
    }

    @Test
    fun hasError_whenVisibleInvalidAndChanged_returnsTrue() {
        val fieldState = FieldState(
            state = mutableStateOf("test"),
            isValid = mutableStateOf(false),
            isVisible = { true },
            hasChanges = mutableStateOf(true)
        )
        
        assertTrue(fieldState.hasError())
    }

    @Test
    fun hasError_whenNotVisible_returnsFalse() {
        val fieldState = FieldState(
            state = mutableStateOf("test"),
            isValid = mutableStateOf(false),
            isVisible = { false },
            hasChanges = mutableStateOf(true)
        )
        
        assertFalse(fieldState.hasError())
    }

    @Test
    fun hasError_whenNoChanges_returnsFalse() {
        val fieldState = FieldState(
            state = mutableStateOf("test"),
            isValid = mutableStateOf(false),
            isVisible = { true },
            hasChanges = mutableStateOf(false)
        )
        
        assertFalse(fieldState.hasError())
    }

    // selectedOption() method tests

    @Test
    fun selectedOption_whenStateValueMatchesOption_returnsMatchingOption() {
        val options = mutableListOf("apple", "banana", "cherry")
        val fieldState = FieldState(
            state = mutableStateOf("banana"),
            options = options
        )
        
        assertEquals("banana", fieldState.selectedOption())
    }

    @Test
    fun selectedOption_whenStateValueDoesNotMatchAnyOption_returnsNull() {
        val options = mutableListOf("apple", "banana", "cherry")
        val fieldState = FieldState(
            state = mutableStateOf("grape"),
            options = options
        )
        
        assertNull(fieldState.selectedOption())
    }

    @Test
    fun selectedOption_whenOptionsIsEmpty_returnsNull() {
        val fieldState = FieldState(
            state = mutableStateOf("value")
        )
        
        assertNull(fieldState.selectedOption())
    }

    @Test
    fun selectedOption_whenStateValueIsNull_returnsNullIfNoNullOption() {
        val fieldState = FieldState(
            state = mutableStateOf<String?>(null),
            options = mutableListOf<String?>("apple", "banana")
        )
        
        assertNull(fieldState.selectedOption())
    }

    @Test
    fun selectedOption_whenStateValueIsNullAndNullOptionExists_returnsNull() {
        val fieldState = FieldState(
            state = mutableStateOf<String?>(null),
            options = mutableListOf<String?>("apple", null, "banana")
        )
        
        assertNull(fieldState.selectedOption())
    }

    @Test
    fun selectedOption_withIntegerValues_worksCorrectly() {
        val options = mutableListOf(1, 2, 3, 4, 5)
        val fieldState = FieldState(
            state = mutableStateOf(3),
            options = options
        )
        
        assertEquals(3, fieldState.selectedOption())
    }

    @Test
    fun selectedOption_withCustomObjects_usesEquals() {
        data class CustomOption(val id: Int, val name: String)
        
        val option1 = CustomOption(1, "First")
        val option2 = CustomOption(2, "Second")
        val option3 = CustomOption(3, "Third")
        val options = mutableListOf(option1, option2, option3)
        
        val fieldState = FieldState(
            state = mutableStateOf(CustomOption(2, "Second")),
            options = options
        )
        
        assertEquals(option2, fieldState.selectedOption())
    }

    // selectedOptionText() method tests

    @Test
    fun selectedOptionText_withFormatterAndSelectedOption_returnsFormattedText() {
        val options = mutableListOf("apple", "banana", "cherry")
        val formatter: (String?) -> String = { "Fruit: ${it?.uppercase()}" }
        val fieldState = FieldState(
            state = mutableStateOf("banana"),
            options = options,
            optionItemFormatter = formatter
        )
        
        assertEquals("Fruit: BANANA", fieldState.selectedOptionText())
    }

    @Test
    fun selectedOptionText_withoutFormatterAndSelectedOption_returnsToString() {
        val options = mutableListOf("apple", "banana", "cherry")
        val fieldState = FieldState(
            state = mutableStateOf("banana"),
            options = options
        )
        
        assertEquals("banana", fieldState.selectedOptionText())
    }

    @Test
    fun selectedOptionText_whenNoSelectedOption_returnsNull() {
        val options = mutableListOf("apple", "banana", "cherry")
        val fieldState = FieldState(
            state = mutableStateOf("grape"),
            options = options
        )
        
        assertNull(fieldState.selectedOptionText())
    }

    @Test
    fun selectedOptionText_withFormatterAndNoSelectedOption_returnsNull() {
        val options = mutableListOf("apple", "banana", "cherry")
        val formatter: (String?) -> String = { "Fruit: ${it?.uppercase()}" }
        val fieldState = FieldState(
            state = mutableStateOf("grape"),
            options = options,
            optionItemFormatter = formatter
        )
        
        assertNull(fieldState.selectedOptionText())
    }

    @Test
    fun selectedOptionText_withFormatterAndNullSelection_returnsNull() {
        val formatter: (String?) -> String = { it ?: "No selection" }
        val fieldState = FieldState(
            state = mutableStateOf<String?>(null),
            options = mutableListOf<String?>("apple", null, "cherry"),
            optionItemFormatter = formatter
        )
        
        // First verify selectedOption() finds the null option
        assertEquals(null, fieldState.selectedOption())
        
        // When null option is selected, selectedOptionText returns null regardless of formatter
        // This is due to the Elvis operator in the implementation: selectedOption() ?: return null
        assertNull(fieldState.selectedOptionText())
    }

    @Test
    fun selectedOptionText_withFormatterAndNoMatchingOption_returnsNull() {
        val formatter: (String?) -> String = { it ?: "No selection" }
        val fieldState = FieldState(
            state = mutableStateOf("nonexistent"),
            options = mutableListOf("apple", "banana", "cherry"),
            optionItemFormatter = formatter
        )
        
        // When state value doesn't match any option, selectedOptionText returns null
        assertNull(fieldState.selectedOptionText())
    }

    @Test
    fun selectedOptionText_withIntegerOptions_returnsToString() {
        val options = mutableListOf(10, 20, 30)
        val fieldState = FieldState(
            state = mutableStateOf(20),
            options = options
        )
        
        assertEquals("20", fieldState.selectedOptionText())
    }

    @Test
    fun selectedOptionText_withCustomObjectAndFormatter_usesFormatter() {
        data class Person(val name: String, val age: Int)
        
        val options = mutableListOf(
            Person("Alice", 25),
            Person("Bob", 30),
            Person("Charlie", 35)
        )
        val formatter: (Person?) -> String = { "${it?.name} (${it?.age})" }
        val fieldState = FieldState(
            state = mutableStateOf(Person("Bob", 30)),
            options = options,
            optionItemFormatter = formatter
        )
        
        assertEquals("Bob (30)", fieldState.selectedOptionText())
    }

    @Test
    fun selectedOptionText_withCustomObjectNoFormatter_usesToString() {
        data class Person(val name: String, val age: Int)
        
        val options = mutableListOf(
            Person("Alice", 25),
            Person("Bob", 30)
        )
        val selectedPerson = Person("Bob", 30)
        val fieldState = FieldState(
            state = mutableStateOf(selectedPerson),
            options = options
        )
        
        assertEquals(selectedPerson.toString(), fieldState.selectedOptionText())
    }

    // State mutation tests

    @Test
    fun stateValue_canBeModified() {
        val fieldState = FieldState(state = mutableStateOf("initial"))
        
        fieldState.state.value = "modified"
        
        assertEquals("modified", fieldState.state.value)
    }

    @Test
    fun validators_canBeAdded() {
        val fieldState = FieldState(state = mutableStateOf("test"))
        
        fieldState.validators.add(mockValidator)
        
        assertEquals(1, fieldState.validators.size)
        assertEquals(mockValidator, fieldState.validators[0])
    }

    @Test
    fun errorText_canBeModified() {
        val fieldState = FieldState(state = mutableStateOf("test"))
        
        fieldState.errorText.add("New error")
        
        assertEquals(1, fieldState.errorText.size)
        assertEquals("New error", fieldState.errorText[0])
    }

    @Test
    fun isValid_canBeChanged() {
        val fieldState = FieldState(state = mutableStateOf("test"))
        
        fieldState.isValid.value = true
        
        assertEquals(true, fieldState.isValid.value)
    }

    @Test
    fun hasChanges_canBeChanged() {
        val fieldState = FieldState(state = mutableStateOf("test"))
        
        fieldState.hasChanges.value = true
        
        assertEquals(true, fieldState.hasChanges.value)
    }

    @Test
    fun options_canBeModified() {
        val fieldState = FieldState(state = mutableStateOf("test"))
        
        fieldState.options.add("new option")
        
        assertEquals(1, fieldState.options.size)
        assertEquals("new option", fieldState.options[0])
    }

    // Edge cases and complex scenarios

    @Test
    fun hasError_multipleStateChanges_behavesConsistently() {
        val fieldState = FieldState(
            state = mutableStateOf("test"),
            isValid = mutableStateOf(false),
            isVisible = { true },
            hasChanges = mutableStateOf(true)
        )
        
        // Initial state should have error
        assertTrue(fieldState.hasError())
        
        // Change validity
        fieldState.isValid.value = true
        assertFalse(fieldState.hasError())
        
        // Change back to invalid
        fieldState.isValid.value = false
        assertTrue(fieldState.hasError())
        
        // Change has changes
        fieldState.hasChanges.value = false
        assertFalse(fieldState.hasError())
    }

    @Test
    fun selectedOption_afterOptionsChange_updatesCorrectly() {
        val fieldState = FieldState(
            state = mutableStateOf("banana"),
            options = mutableListOf("apple", "cherry")
        )
        
        // Initially no match
        assertNull(fieldState.selectedOption())
        
        // Add matching option
        fieldState.options.add("banana")
        assertEquals("banana", fieldState.selectedOption())
        
        // Remove matching option
        fieldState.options.remove("banana")
        assertNull(fieldState.selectedOption())
    }

    @Test
    fun selectedOptionText_afterStateValueChange_updatesCorrectly() {
        val options = mutableListOf("apple", "banana", "cherry")
        val fieldState = FieldState(
            state = mutableStateOf("apple"),
            options = options
        )
        
        assertEquals("apple", fieldState.selectedOptionText())
        
        fieldState.state.value = "banana"
        assertEquals("banana", fieldState.selectedOptionText())
        
        fieldState.state.value = "nonexistent"
        assertNull(fieldState.selectedOptionText())
    }
}