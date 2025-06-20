package org.springai.flightbooking.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingClass Enum Tests")
class BookingClassTest {

    @Test
    @DisplayName("Should have all expected booking class values")
    void shouldHaveAllExpectedBookingClassValues() {
        BookingClass[] classes = BookingClass.values();
        
        assertEquals(3, classes.length);
        
        // Verify all expected values exist
        assertTrue(containsClass(classes, BookingClass.ECONOMY));
        assertTrue(containsClass(classes, BookingClass.PREMIUM_ECONOMY));
        assertTrue(containsClass(classes, BookingClass.BUSINESS));
    }

    @Test
    @DisplayName("Should convert string to BookingClass enum")
    void shouldConvertStringToBookingClassEnum() {
        assertEquals(BookingClass.ECONOMY, BookingClass.valueOf("ECONOMY"));
        assertEquals(BookingClass.PREMIUM_ECONOMY, BookingClass.valueOf("PREMIUM_ECONOMY"));
        assertEquals(BookingClass.BUSINESS, BookingClass.valueOf("BUSINESS"));
    }

    @Test
    @DisplayName("Should throw exception for invalid BookingClass string")
    void shouldThrowExceptionForInvalidBookingClassString() {
        assertThrows(IllegalArgumentException.class, () -> BookingClass.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> BookingClass.valueOf("economy"));
        assertThrows(IllegalArgumentException.class, () -> BookingClass.valueOf("FIRST_CLASS"));
        assertThrows(IllegalArgumentException.class, () -> BookingClass.valueOf(""));
        assertThrows(NullPointerException.class, () -> BookingClass.valueOf(null));
    }

    @Test
    @DisplayName("Should have correct string representation")
    void shouldHaveCorrectStringRepresentation() {
        assertEquals("ECONOMY", BookingClass.ECONOMY.toString());
        assertEquals("PREMIUM_ECONOMY", BookingClass.PREMIUM_ECONOMY.toString());
        assertEquals("BUSINESS", BookingClass.BUSINESS.toString());
    }

    @Test
    @DisplayName("Should have correct name")
    void shouldHaveCorrectName() {
        assertEquals("ECONOMY", BookingClass.ECONOMY.name());
        assertEquals("PREMIUM_ECONOMY", BookingClass.PREMIUM_ECONOMY.name());
        assertEquals("BUSINESS", BookingClass.BUSINESS.name());
    }

    @Test
    @DisplayName("Should have correct ordinal values")
    void shouldHaveCorrectOrdinalValues() {
        assertEquals(0, BookingClass.ECONOMY.ordinal());
        assertEquals(1, BookingClass.PREMIUM_ECONOMY.ordinal());
        assertEquals(2, BookingClass.BUSINESS.ordinal());
    }

    @Test
    @DisplayName("Should support equality comparison")
    void shouldSupportEqualityComparison() {
        BookingClass class1 = BookingClass.ECONOMY;
        BookingClass class2 = BookingClass.ECONOMY;
        BookingClass class3 = BookingClass.BUSINESS;

        assertEquals(class1, class2);
        assertNotEquals(class1, class3);
        assertSame(class1, class2); // Enum instances are singletons
    }

    @Test
    @DisplayName("Should support switch statements")
    void shouldSupportSwitchStatements() {
        String result = switch (BookingClass.PREMIUM_ECONOMY) {
            case ECONOMY -> "经济舱";
            case PREMIUM_ECONOMY -> "超级经济舱";
            case BUSINESS -> "商务舱";
        };
        
        assertEquals("超级经济舱", result);
    }

    @Test
    @DisplayName("Should support comparison operations")
    void shouldSupportComparisonOperations() {
        // Test ordinal-based comparison
        assertTrue(BookingClass.ECONOMY.ordinal() < BookingClass.PREMIUM_ECONOMY.ordinal());
        assertTrue(BookingClass.PREMIUM_ECONOMY.ordinal() < BookingClass.BUSINESS.ordinal());
        assertTrue(BookingClass.ECONOMY.ordinal() < BookingClass.BUSINESS.ordinal());
    }

    @Test
    @DisplayName("Should be usable in collections")
    void shouldBeUsableInCollections() {
        java.util.Set<BookingClass> classSet = java.util.EnumSet.allOf(BookingClass.class);
        
        assertEquals(3, classSet.size());
        assertTrue(classSet.contains(BookingClass.ECONOMY));
        assertTrue(classSet.contains(BookingClass.PREMIUM_ECONOMY));
        assertTrue(classSet.contains(BookingClass.BUSINESS));
    }

    private boolean containsClass(BookingClass[] classes, BookingClass target) {
        for (BookingClass clazz : classes) {
            if (clazz == target) {
                return true;
            }
        }
        return false;
    }
}