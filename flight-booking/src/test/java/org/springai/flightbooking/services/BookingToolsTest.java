package org.springai.flightbooking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springai.flightbooking.data.BookingStatus;
import org.springai.flightbooking.services.BookingTools.BookingDetails;
import org.springai.flightbooking.services.BookingTools.BookingDetailsRequest;
import org.springai.flightbooking.services.BookingTools.ChangeBookingDatesRequest;
import org.springai.flightbooking.services.BookingTools.CancelBookingRequest;

import java.time.LocalDate;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Booking Tools Tests")
class BookingToolsTest {

    @Mock
    private FlightBookingService flightBookingService;

    private BookingTools bookingTools;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingTools = new BookingTools();
        // Use reflection to set the private field for testing
        try {
            var field = BookingTools.class.getDeclaredField("flightBookingService");
            field.setAccessible(true);
            field.set(bookingTools, flightBookingService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should create BookingDetailsRequest record correctly")
    void shouldCreateBookingDetailsRequestRecordCorrectly() {
        BookingDetailsRequest request = new BookingDetailsRequest("B001", "张三");
        
        assertEquals("B001", request.bookingNumber());
        assertEquals("张三", request.name());
    }

    @Test
    @DisplayName("Should create ChangeBookingDatesRequest record correctly")
    void shouldCreateChangeBookingDatesRequestRecordCorrectly() {
        ChangeBookingDatesRequest request = new ChangeBookingDatesRequest(
            "B001", "张三", "2024-12-25", "北京", "上海"
        );
        
        assertEquals("B001", request.bookingNumber());
        assertEquals("张三", request.name());
        assertEquals("2024-12-25", request.date());
        assertEquals("北京", request.from());
        assertEquals("上海", request.to());
    }

    @Test
    @DisplayName("Should create CancelBookingRequest record correctly")
    void shouldCreateCancelBookingRequestRecordCorrectly() {
        CancelBookingRequest request = new CancelBookingRequest("B001", "张三");
        
        assertEquals("B001", request.bookingNumber());
        assertEquals("张三", request.name());
    }

    @Test
    @DisplayName("Should create BookingDetails record correctly")
    void shouldCreateBookingDetailsRecordCorrectly() {
        LocalDate testDate = LocalDate.of(2024, 12, 25);
        BookingDetails details = new BookingDetails(
            "B001", "张三", testDate, BookingStatus.CONFIRMED, 
            "北京", "上海", "ECONOMY"
        );
        
        assertEquals("B001", details.bookingNumber());
        assertEquals("张三", details.name());
        assertEquals(testDate, details.date());
        assertEquals(BookingStatus.CONFIRMED, details.bookingStatus());
        assertEquals("北京", details.from());
        assertEquals("上海", details.to());
        assertEquals("ECONOMY", details.bookingClass());
    }

    @Test
    @DisplayName("Should handle null values in BookingDetails record")
    void shouldHandleNullValuesInBookingDetailsRecord() {
        BookingDetails details = new BookingDetails(
            "B001", "张三", null, null, null, null, null
        );
        
        assertEquals("B001", details.bookingNumber());
        assertEquals("张三", details.name());
        assertNull(details.date());
        assertNull(details.bookingStatus());
        assertNull(details.from());
        assertNull(details.to());
        assertNull(details.bookingClass());
    }

    @Test
    @DisplayName("Should get booking details function successfully")
    void shouldGetBookingDetailsFunctionSuccessfully() {
        LocalDate testDate = LocalDate.of(2024, 12, 25);
        BookingDetails expectedDetails = new BookingDetails(
            "B001", "张三", testDate, BookingStatus.CONFIRMED, 
            "北京", "上海", "ECONOMY"
        );
        
        when(flightBookingService.getBookingDetails("B001", "张三"))
            .thenReturn(expectedDetails);
        
        Function<BookingDetailsRequest, BookingDetails> function = bookingTools.getBookingDetails();
        BookingDetailsRequest request = new BookingDetailsRequest("B001", "张三");
        
        BookingDetails result = function.apply(request);
        
        assertEquals(expectedDetails, result);
        verify(flightBookingService).getBookingDetails("B001", "张三");
    }

    @Test
    @DisplayName("Should handle exception in get booking details function")
    void shouldHandleExceptionInGetBookingDetailsFunction() {
        when(flightBookingService.getBookingDetails("B001", "张三"))
            .thenThrow(new IllegalArgumentException("Booking not found"));
        
        Function<BookingDetailsRequest, BookingDetails> function = bookingTools.getBookingDetails();
        BookingDetailsRequest request = new BookingDetailsRequest("B001", "张三");
        
        BookingDetails result = function.apply(request);
        
        // Should return a BookingDetails with null values except for booking number and name
        assertEquals("B001", result.bookingNumber());
        assertEquals("张三", result.name());
        assertNull(result.date());
        assertNull(result.bookingStatus());
        assertNull(result.from());
        assertNull(result.to());
        assertNull(result.bookingClass());
        
        verify(flightBookingService).getBookingDetails("B001", "张三");
    }

    @Test
    @DisplayName("Should change booking function successfully")
    void shouldChangeBookingFunctionSuccessfully() {
        Function<ChangeBookingDatesRequest, String> function = bookingTools.changeBooking();
        ChangeBookingDatesRequest request = new ChangeBookingDatesRequest(
            "B001", "张三", "2024-12-25", "北京", "上海"
        );
        
        String result = function.apply(request);
        
        assertEquals("", result);
        verify(flightBookingService).changeBooking("B001", "张三", "2024-12-25", "北京", "上海");
    }

    @Test
    @DisplayName("Should cancel booking function successfully")
    void shouldCancelBookingFunctionSuccessfully() {
        Function<CancelBookingRequest, String> function = bookingTools.cancelBooking();
        CancelBookingRequest request = new CancelBookingRequest("B001", "张三");
        
        String result = function.apply(request);
        
        assertEquals("", result);
        verify(flightBookingService).cancelBooking("B001", "张三");
    }

    @Test
    @DisplayName("Should verify records are immutable")
    void shouldVerifyRecordsAreImmutable() {
        BookingDetailsRequest request1 = new BookingDetailsRequest("B001", "张三");
        BookingDetailsRequest request2 = new BookingDetailsRequest("B001", "张三");
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        
        ChangeBookingDatesRequest changeRequest1 = new ChangeBookingDatesRequest(
            "B001", "张三", "2024-12-25", "北京", "上海"
        );
        ChangeBookingDatesRequest changeRequest2 = new ChangeBookingDatesRequest(
            "B001", "张三", "2024-12-25", "北京", "上海"
        );
        
        assertEquals(changeRequest1, changeRequest2);
        assertEquals(changeRequest1.hashCode(), changeRequest2.hashCode());
        
        CancelBookingRequest cancelRequest1 = new CancelBookingRequest("B001", "张三");
        CancelBookingRequest cancelRequest2 = new CancelBookingRequest("B001", "张三");
        
        assertEquals(cancelRequest1, cancelRequest2);
        assertEquals(cancelRequest1.hashCode(), cancelRequest2.hashCode());
    }
}