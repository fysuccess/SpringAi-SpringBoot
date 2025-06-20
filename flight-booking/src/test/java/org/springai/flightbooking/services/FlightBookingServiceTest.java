package org.springai.flightbooking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springai.flightbooking.data.*;
import org.springai.flightbooking.services.BookingTools.BookingDetails;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Flight Booking Service Tests")
class FlightBookingServiceTest {

    private FlightBookingService flightBookingService;

    @BeforeEach
    void setUp() {
        flightBookingService = new FlightBookingService();
    }

    @Test
    @DisplayName("Should initialize with demo data")
    void shouldInitializeWithDemoData() {
        List<BookingDetails> bookings = flightBookingService.getBookings();
        
        assertNotNull(bookings);
        assertEquals(5, bookings.size());
        
        // Verify all bookings have required fields
        for (BookingDetails booking : bookings) {
            assertNotNull(booking.bookingNumber());
            assertNotNull(booking.name());
            assertNotNull(booking.date());
            assertNotNull(booking.bookingStatus());
            assertNotNull(booking.from());
            assertNotNull(booking.to());
            assertNotNull(booking.bookingClass());
        }
    }

    @Test
    @DisplayName("Should retrieve booking details by booking number and name")
    void shouldRetrieveBookingDetailsByBookingNumberAndName() {
        List<BookingDetails> allBookings = flightBookingService.getBookings();
        BookingDetails firstBooking = allBookings.get(0);
        
        BookingDetails retrievedBooking = flightBookingService.getBookingDetails(
            firstBooking.bookingNumber(), 
            firstBooking.name()
        );
        
        assertNotNull(retrievedBooking);
        assertEquals(firstBooking.bookingNumber(), retrievedBooking.bookingNumber());
        assertEquals(firstBooking.name(), retrievedBooking.name());
        assertEquals(firstBooking.date(), retrievedBooking.date());
        assertEquals(firstBooking.from(), retrievedBooking.from());
        assertEquals(firstBooking.to(), retrievedBooking.to());
    }

    @Test
    @DisplayName("Should throw exception when booking not found")
    void shouldThrowExceptionWhenBookingNotFound() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightBookingService.getBookingDetails("INVALID", "Invalid Name")
        );
        
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when booking number exists but name doesn't match")
    void shouldThrowExceptionWhenBookingNumberExistsButNameDoesntMatch() {
        List<BookingDetails> allBookings = flightBookingService.getBookings();
        BookingDetails firstBooking = allBookings.get(0);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightBookingService.getBookingDetails(firstBooking.bookingNumber(), "Wrong Name")
        );
        
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully change booking when more than 24 hours before departure")
    void shouldSuccessfullyChangeBookingWhenMoreThan24HoursBeforeDeparture() {
        List<BookingDetails> allBookings = flightBookingService.getBookings();
        BookingDetails bookingToChange = allBookings.get(0);
        
        // Ensure the booking is more than 24 hours in the future
        LocalDate newDate = LocalDate.now().plusDays(5);
        String newFrom = "新出发地";
        String newTo = "新目的地";
        
        assertDoesNotThrow(() -> flightBookingService.changeBooking(
            bookingToChange.bookingNumber(),
            bookingToChange.name(),
            newDate.toString(),
            newFrom,
            newTo
        ));
        
        // Verify the booking was changed
        BookingDetails updatedBooking = flightBookingService.getBookingDetails(
            bookingToChange.bookingNumber(),
            bookingToChange.name()
        );
        
        assertEquals(newDate, updatedBooking.date());
        assertEquals(newFrom, updatedBooking.from());
        assertEquals(newTo, updatedBooking.to());
    }

    @Test
    @DisplayName("Should throw exception when trying to change booking within 24 hours")
    void shouldThrowExceptionWhenTryingToChangeBookingWithin24Hours() {
        // Create a booking with a date within 24 hours
        List<BookingDetails> allBookings = flightBookingService.getBookings();
        BookingDetails bookingToChange = allBookings.get(0);
        
        // First, change the booking to have a date within 24 hours
        LocalDate nearDate = LocalDate.now().plusHours(12).toLocalDate();
        
        // We need to manually set up a booking that's within 24 hours
        // Since we can't directly modify the internal state, we'll test the business logic
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                // Create a new service instance and try to change a booking that would be within 24 hours
                // This simulates the scenario where a booking date is close to departure
                LocalDate tomorrowDate = LocalDate.now().plusDays(1);
                flightBookingService.changeBooking(
                    bookingToChange.bookingNumber(),
                    bookingToChange.name(),
                    tomorrowDate.toString(),
                    "新出发地",
                    "新目的地"
                );
                
                // Now try to change it again to a date that would trigger the 24-hour rule
                LocalDate todayDate = LocalDate.now();
                flightBookingService.changeBooking(
                    bookingToChange.bookingNumber(),
                    bookingToChange.name(),
                    todayDate.toString(),
                    "另一个出发地",
                    "另一个目的地"
                );
            }
        );
        
        assertEquals("Booking cannot be changed within 24 hours of the start date.", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully cancel booking when more than 48 hours before departure")
    void shouldSuccessfullyCancelBookingWhenMoreThan48HoursBeforeDeparture() {
        List<BookingDetails> allBookings = flightBookingService.getBookings();
        BookingDetails bookingToCancel = allBookings.get(0);
        
        // Ensure the booking is more than 48 hours in the future
        assertDoesNotThrow(() -> flightBookingService.cancelBooking(
            bookingToCancel.bookingNumber(),
            bookingToCancel.name()
        ));
        
        // Verify the booking was cancelled
        BookingDetails cancelledBooking = flightBookingService.getBookingDetails(
            bookingToCancel.bookingNumber(),
            bookingToCancel.name()
        );
        
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.bookingStatus());
    }

    @Test
    @DisplayName("Should throw exception when trying to cancel booking within 48 hours")
    void shouldThrowExceptionWhenTryingToCancelBookingWithin48Hours() {
        List<BookingDetails> allBookings = flightBookingService.getBookings();
        BookingDetails bookingToCancel = allBookings.get(0);
        
        // First, change the booking to have a date within 48 hours
        LocalDate nearDate = LocalDate.now().plusDays(1);
        
        flightBookingService.changeBooking(
            bookingToCancel.bookingNumber(),
            bookingToCancel.name(),
            nearDate.toString(),
            bookingToCancel.from(),
            bookingToCancel.to()
        );
        
        // Now try to cancel it
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightBookingService.cancelBooking(
                bookingToCancel.bookingNumber(),
                bookingToCancel.name()
            )
        );
        
        assertEquals("Booking cannot be cancelled within 48 hours of the start date.", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle case-insensitive booking number search")
    void shouldHandleCaseInsensitiveBookingNumberSearch() {
        List<BookingDetails> allBookings = flightBookingService.getBookings();
        BookingDetails firstBooking = allBookings.get(0);
        
        // Test with lowercase booking number
        BookingDetails retrievedBooking = flightBookingService.getBookingDetails(
            firstBooking.bookingNumber().toLowerCase(),
            firstBooking.name()
        );
        
        assertNotNull(retrievedBooking);
        assertEquals(firstBooking.bookingNumber(), retrievedBooking.bookingNumber());
    }

    @Test
    @DisplayName("Should handle case-insensitive customer name search")
    void shouldHandleCaseInsensitiveCustomerNameSearch() {
        List<BookingDetails> allBookings = flightBookingService.getBookings();
        BookingDetails firstBooking = allBookings.get(0);
        
        // Test with different case customer name
        BookingDetails retrievedBooking = flightBookingService.getBookingDetails(
            firstBooking.bookingNumber(),
            firstBooking.name().toLowerCase()
        );
        
        assertNotNull(retrievedBooking);
        assertEquals(firstBooking.name(), retrievedBooking.name());
    }

    @Test
    @DisplayName("Should verify all demo bookings have confirmed status initially")
    void shouldVerifyAllDemoBookingsHaveConfirmedStatusInitially() {
        List<BookingDetails> bookings = flightBookingService.getBookings();
        
        for (BookingDetails booking : bookings) {
            assertEquals(BookingStatus.CONFIRMED, booking.bookingStatus());
        }
    }

    @Test
    @DisplayName("Should verify all demo bookings have future dates")
    void shouldVerifyAllDemoBookingsHaveFutureDates() {
        List<BookingDetails> bookings = flightBookingService.getBookings();
        LocalDate today = LocalDate.now();
        
        for (BookingDetails booking : bookings) {
            assertTrue(booking.date().isAfter(today), 
                "Booking " + booking.bookingNumber() + " should have a future date");
        }
    }

    @Test
    @DisplayName("Should verify booking numbers follow expected pattern")
    void shouldVerifyBookingNumbersFollowExpectedPattern() {
        List<BookingDetails> bookings = flightBookingService.getBookings();
        
        for (BookingDetails booking : bookings) {
            String bookingNumber = booking.bookingNumber();
            assertTrue(bookingNumber.startsWith("10"), 
                "Booking number should start with '10'");
            assertTrue(bookingNumber.length() == 3, 
                "Booking number should be 3 characters long");
        }
    }

    @Test
    @DisplayName("Should verify all bookings have valid booking classes")
    void shouldVerifyAllBookingsHaveValidBookingClasses() {
        List<BookingDetails> bookings = flightBookingService.getBookings();
        
        for (BookingDetails booking : bookings) {
            String bookingClass = booking.bookingClass();
            assertTrue(
                bookingClass.equals("ECONOMY") || 
                bookingClass.equals("PREMIUM_ECONOMY") || 
                bookingClass.equals("BUSINESS"),
                "Booking class should be one of the valid enum values"
            );
        }
    }
}