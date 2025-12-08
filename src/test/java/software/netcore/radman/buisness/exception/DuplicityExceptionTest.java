package software.netcore.radman.buisness.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DuplicityException
 */
class DuplicityExceptionTest {

    @Test
    void constructor_ShouldSetMessage() {
        // Arrange & Act
        DuplicityException exception = new DuplicityException("Duplicate entry found");

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Duplicate entry found");
    }

    @Test
    void exception_ShouldBeThrowable() {
        // Arrange
        DuplicityException exception = new DuplicityException("Test error");

        // Act & Assert
        assertThat(exception).isInstanceOf(Exception.class);
    }
}
