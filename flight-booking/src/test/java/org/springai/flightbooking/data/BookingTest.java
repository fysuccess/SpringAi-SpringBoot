package org.springai.flightbooking.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Entity Tests")
class BookingTest {

    private Customer customer;
    private Booking booking;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        customer = new Customer("张三");
        testDate = LocalDate.of(2024, 12, 25);
        booking = new Booking("B001", testDate, customer, BookingStatus.CONFIRMED, 
                             "北京", "上海", BookingClass.ECONOMY);
    }

    @Test
    @DisplayName("Should create booking with all required fields")
    void shouldCreateBookingWithAllRequiredFields() {
        assertNotNull(booking);
        assertEquals("B001", booking.getBookingNumber());
        assertEquals(testDate, booking.getDate());
        assertEquals(customer, booking.getCustomer());
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
        assertEquals("北京", booking.getFrom());
        assertEquals("上海", booking.getTo());
        assertEquals(BookingClass.ECONOMY, booking.getBookingClass());
    }

    @Test
    @DisplayName("Should allow updating booking number")
    void shouldAllowUpdatingBookingNumber() {
        String newBookingNumber = "B002";
        booking.setBookingNumber(newBookingNumber);
        assertEquals(newBookingNumber, booking.getBookingNumber());
    }

    @Test
    @DisplayName("Should allow updating booking date")
    void shouldAllowUpdatingBookingDate() {
        LocalDate newDate = LocalDate.of(2024, 12, 30);
        booking.setDate(newDate);
        assertEquals(newDate, booking.getDate());
    }

    @Test
    @DisplayName("Should allow updating booking to date")
    void shouldAllowUpdatingBookingToDate() {
        LocalDate bookingToDate = LocalDate.of(2024, 12, 28);
        booking.setBookingTo(bookingToDate);
        assertEquals(bookingToDate, booking.getBookingTo());
    }

    @Test
    @DisplayName("Should allow updating customer")
    void shouldAllowUpdatingCustomer() {
        Customer newCustomer = new Customer("李四");
        booking.setCustomer(newCustomer);
        assertEquals(newCustomer, booking.getCustomer());
        assertEquals("李四", booking.getCustomer().getName());
    }

    @Test
    @DisplayName("Should allow updating booking status")
    void shouldAllowUpdatingBookingStatus() {
        booking.setBookingStatus(BookingStatus.CANCELLED);
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
    }

    @Test
    @DisplayName("Should allow updating from location")
    void shouldAllowUpdatingFromLocation() {
        String newFrom = "广州";
        booking.setFrom(newFrom);
        assertEquals(newFrom, booking.getFrom());
    }

    @Test
    @DisplayName("Should allow updating to location")
    void shouldAllowUpdatingToLocation() {
        String newTo = "深圳";
        booking.setTo(newTo);
        assertEquals(newTo, booking.getTo());
    }

    @Test
    @DisplayName("Should allow updating booking class")
    void shouldAllowUpdatingBookingClass() {
        booking.setBookingClass(BookingClass.BUSINESS);
        assertEquals(BookingClass.BUSINESS, booking.getBookingClass());
    }

    @Test
    @DisplayName("Should handle null booking to date")
    void shouldHandleNullBookingToDate() {
        assertNull(booking.getBookingTo());
        
        LocalDate bookingToDate = LocalDate.of(2024, 12, 28);
        booking.setBookingTo(bookingToDate);
        assertEquals(bookingToDate, booking.getBookingTo());
        
        booking.setBookingTo(null);
        assertNull(booking.getBookingTo());
    }

    @Test
    @DisplayName("Should create booking with different booking classes")
    void shouldCreateBookingWithDifferentBookingClasses() {
        Booking economyBooking = new Booking("E001", testDate, customer, BookingStatus.CONFIRMED, 
                                           "北京", "上海", BookingClass.ECONOMY);
        assertEquals(BookingClass.ECONOMY, economyBooking.getBookingClass());

        Booking premiumBooking = new Booking("P001", testDate, customer, BookingStatus.CONFIRMED, 
                                           "北京", "上海", BookingClass.PREMIUM_ECONOMY);
        assertEquals(BookingClass.PREMIUM_ECONOMY, premiumBooking.getBookingClass());

        Booking businessBooking = new Booking("B001", testDate, customer, BookingStatus.CONFIRMED, 
                                            "北京", "上海", BookingClass.BUSINESS);
        assertEquals(BookingClass.BUSINESS, businessBooking.getBookingClass());
    }

    @Test
    @DisplayName("Should create booking with different booking statuses")
    void shouldCreateBookingWithDifferentBookingStatuses() {
        Booking confirmedBooking = new Booking("C001", testDate, customer, BookingStatus.CONFIRMED, 
                                             "北京", "上海", BookingClass.ECONOMY);
        assertEquals(BookingStatus.CONFIRMED, confirmedBooking.getBookingStatus());

        Booking completedBooking = new Booking("C002", testDate, customer, BookingStatus.COMPLETED, 
                                             "北京", "上海", BookingClass.ECONOMY);
        assertEquals(BookingStatus.COMPLETED, completedBooking.getBookingStatus());

        Booking cancelledBooking = new Booking("C003", testDate, customer, BookingStatus.CANCELLED, 
                                             "北京", "上海", BookingClass.ECONOMY);
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.getBookingStatus());
    }
}