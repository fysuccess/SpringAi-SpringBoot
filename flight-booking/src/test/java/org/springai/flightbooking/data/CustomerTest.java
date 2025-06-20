package org.springai.flightbooking.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Entity Tests")
class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("张三");
    }

    @Test
    @DisplayName("Should create customer with name")
    void shouldCreateCustomerWithName() {
        assertEquals("张三", customer.getName());
        assertNotNull(customer.getBookings());
        assertTrue(customer.getBookings().isEmpty());
    }

    @Test
    @DisplayName("Should create customer with default constructor")
    void shouldCreateCustomerWithDefaultConstructor() {
        Customer defaultCustomer = new Customer();
        assertNull(defaultCustomer.getName());
        assertNotNull(defaultCustomer.getBookings());
        assertTrue(defaultCustomer.getBookings().isEmpty());
    }

    @Test
    @DisplayName("Should allow updating customer name")
    void shouldAllowUpdatingCustomerName() {
        customer.setName("李四");
        assertEquals("李四", customer.getName());
    }

    @Test
    @DisplayName("Should allow setting name to null")
    void shouldAllowSettingNameToNull() {
        customer.setName(null);
        assertNull(customer.getName());
    }

    @Test
    @DisplayName("Should allow adding bookings to customer")
    void shouldAllowAddingBookingsToCustomer() {
        LocalDate testDate = LocalDate.of(2024, 12, 25);
        Booking booking1 = new Booking("B001", testDate, customer, BookingStatus.CONFIRMED, 
                                     "北京", "上海", BookingClass.ECONOMY);
        Booking booking2 = new Booking("B002", testDate.plusDays(1), customer, BookingStatus.CONFIRMED, 
                                     "上海", "广州", BookingClass.BUSINESS);

        customer.getBookings().add(booking1);
        customer.getBookings().add(booking2);

        assertEquals(2, customer.getBookings().size());
        assertTrue(customer.getBookings().contains(booking1));
        assertTrue(customer.getBookings().contains(booking2));
    }

    @Test
    @DisplayName("Should allow setting entire bookings list")
    void shouldAllowSettingEntireBookingsList() {
        LocalDate testDate = LocalDate.of(2024, 12, 25);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking("B001", testDate, customer, BookingStatus.CONFIRMED, 
                               "北京", "上海", BookingClass.ECONOMY));
        bookings.add(new Booking("B002", testDate.plusDays(1), customer, BookingStatus.CONFIRMED, 
                               "上海", "广州", BookingClass.BUSINESS));

        customer.setBookings(bookings);

        assertEquals(2, customer.getBookings().size());
        assertEquals(bookings, customer.getBookings());
    }

    @Test
    @DisplayName("Should allow removing bookings from customer")
    void shouldAllowRemovingBookingsFromCustomer() {
        LocalDate testDate = LocalDate.of(2024, 12, 25);
        Booking booking1 = new Booking("B001", testDate, customer, BookingStatus.CONFIRMED, 
                                     "北京", "上海", BookingClass.ECONOMY);
        Booking booking2 = new Booking("B002", testDate.plusDays(1), customer, BookingStatus.CONFIRMED, 
                                     "上海", "广州", BookingClass.BUSINESS);

        customer.getBookings().add(booking1);
        customer.getBookings().add(booking2);
        assertEquals(2, customer.getBookings().size());

        customer.getBookings().remove(booking1);
        assertEquals(1, customer.getBookings().size());
        assertFalse(customer.getBookings().contains(booking1));
        assertTrue(customer.getBookings().contains(booking2));
    }

    @Test
    @DisplayName("Should allow clearing all bookings")
    void shouldAllowClearingAllBookings() {
        LocalDate testDate = LocalDate.of(2024, 12, 25);
        Booking booking = new Booking("B001", testDate, customer, BookingStatus.CONFIRMED, 
                                    "北京", "上海", BookingClass.ECONOMY);

        customer.getBookings().add(booking);
        assertEquals(1, customer.getBookings().size());

        customer.getBookings().clear();
        assertTrue(customer.getBookings().isEmpty());
    }

    @Test
    @DisplayName("Should handle empty name")
    void shouldHandleEmptyName() {
        customer.setName("");
        assertEquals("", customer.getName());
    }

    @Test
    @DisplayName("Should handle whitespace-only name")
    void shouldHandleWhitespaceOnlyName() {
        customer.setName("   ");
        assertEquals("   ", customer.getName());
    }

    @Test
    @DisplayName("Should maintain bookings list reference")
    void shouldMaintainBookingsListReference() {
        List<Booking> originalBookings = customer.getBookings();
        
        LocalDate testDate = LocalDate.of(2024, 12, 25);
        Booking booking = new Booking("B001", testDate, customer, BookingStatus.CONFIRMED, 
                                    "北京", "上海", BookingClass.ECONOMY);
        
        originalBookings.add(booking);
        
        // The same list reference should be maintained
        assertSame(originalBookings, customer.getBookings());
        assertEquals(1, customer.getBookings().size());
    }
}