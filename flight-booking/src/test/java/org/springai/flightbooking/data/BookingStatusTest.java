package org.springai.flightbooking.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingStatus Enum Tests")
class BookingStatusTest {

    @Test
    @DisplayName("Should have all expected booking status values")
    void shouldHaveAllExpectedBookingStatusValues() {
        BookingStatus[] statuses = BookingStatus.values();
        
        assertEquals(3, statuses.length);
        
        // Verify all expected values exist
        assertTrue(containsStatus(statuses, BookingStatus.CONFIRMED));
        assertTrue(containsStatus(statuses, BookingStatus.COMPLETED));
        assertTrue(containsStatus(statuses, BookingStatus.CANCELLED));
    }

    @Test
    @DisplayName("Should convert string to BookingStatus enum")
    void shouldConvertStringToBookingStatusEnum() {
        assertEquals(BookingStatus.CONFIRMED, BookingStatus.valueOf("CONFIRMED"));
        assertEquals(BookingStatus.COMPLETED, BookingStatus.valueOf("COMPLETED"));
        assertEquals(BookingStatus.CANCELLED, BookingStatus.valueOf("CANCELLED"));
    }

    @Test
    @DisplayName("Should throw exception for invalid BookingStatus string")
    void shouldThrowExceptionForInvalidBookingStatusString() {
        assertThrows(IllegalArgumentException.class, () -> BookingStatus.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> BookingStatus.valueOf("confirmed"));
        assertThrows(IllegalArgumentException.class, () -> BookingStatus.valueOf(""));
        assertThrows(NullPointerException.class, () -> BookingStatus.valueOf(null));
    }

    @Test
    @DisplayName("Should have correct string representation")
    void shouldHaveCorrectStringRepresentation() {
        assertEquals("CONFIRMED", BookingStatus.CONFIRMED.toString());
        assertEquals("COMPLETED", BookingStatus.COMPLETED.toString());
        assertEquals("CANCELLED", BookingStatus.CANCELLED.toString());
    }

    @Test
    @DisplayName("Should have correct name")
    void shouldHaveCorrectName() {
        assertEquals("CONFIRMED", BookingStatus.CONFIRMED.name());
        assertEquals("COMPLETED", BookingStatus.COMPLETED.name());
        assertEquals("CANCELLED", BookingStatus.CANCELLED.name());
    }

    @Test
    @DisplayName("Should have correct ordinal values")
    void shouldHaveCorrectOrdinalValues() {
        assertEquals(0, BookingStatus.CONFIRMED.ordinal());
        assertEquals(1, BookingStatus.COMPLETED.ordinal());
        assertEquals(2, BookingStatus.CANCELLED.ordinal());
    }

    @Test
    @DisplayName("Should support equality comparison")
    void shouldSupportEqualityComparison() {
        BookingStatus status1 = BookingStatus.CONFIRMED;
        BookingStatus status2 = BookingStatus.CONFIRMED;
        BookingStatus status3 = BookingStatus.COMPLETED;

        assertEquals(status1, status2);
        assertNotEquals(status1, status3);
        assertSame(status1, status2); // Enum instances are singletons
    }

    @Test
    @DisplayName("Should support switch statements")
    void shouldSupportSwitchStatements() {
        String result = switch (BookingStatus.CONFIRMED) {
            case CONFIRMED -> "已确认";
            case COMPLETED -> "已完成";
            case CANCELLED -> "已取消";
        };
        
        assertEquals("已确认", result);
    }

    private boolean containsStatus(BookingStatus[] statuses, BookingStatus target) {
        for (BookingStatus status : statuses) {
            if (status == target) {
                return true;
            }
        }
        return false;
    }
}