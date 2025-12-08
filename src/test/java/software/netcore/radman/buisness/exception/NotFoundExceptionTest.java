package software.netcore.radman.buisness.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for NotFoundException
 */
class NotFoundExceptionTest {

    @Test
    void constructor_ShouldSetMessage() {
        // Arrange & Act
        NotFoundException exception = new NotFoundException("Resource not found");

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Resource not found");
    }

    @Test
    void exception_ShouldBeThrowable() {
        // Arrange
        NotFoundException exception = new NotFoundException("Test error");

        // Act & Assert
        assertThat(exception).isInstanceOf(Exception.class);
    }
}
