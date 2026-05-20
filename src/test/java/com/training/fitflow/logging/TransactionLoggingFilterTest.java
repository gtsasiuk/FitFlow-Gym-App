package com.training.fitflow.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionLoggingFilter Tests")
class TransactionLoggingFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TransactionLoggingFilter filter;

    // ─── successful request ───────────────────────────────────────────

    @Test
    @DisplayName("doFilterInternal → successful request → sets transaction id and clears MDC")
    void doFilterInternal_successfulRequest_setsTransactionIdAndClearsMDC()
            throws ServletException, IOException {

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(response.getStatus()).thenReturn(HttpServletResponse.SC_OK);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(eq("X-Transaction-Id"), anyString());

        verify(filterChain).doFilter(request, response);

        assertTrue(MDC.getCopyOfContextMap() == null
                || MDC.getCopyOfContextMap().isEmpty());
    }

    // ─── request with servlet exception ───────────────────────────────

    @Test
    @DisplayName("doFilterInternal → filter chain throws ServletException → exception rethrown and MDC cleared")
    void doFilterInternal_filterChainThrowsServletException_rethrowsAndClearsMDC()
            throws ServletException, IOException {

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/error");
        when(response.getStatus()).thenReturn(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        doThrow(new ServletException("Servlet error"))
                .when(filterChain)
                .doFilter(request, response);

        ServletException exception = assertThrows(
                ServletException.class,
                () -> filter.doFilterInternal(request, response, filterChain)
        );

        assertEquals("Servlet error", exception.getMessage());

        verify(response).setHeader(eq("X-Transaction-Id"), anyString());

        verify(filterChain).doFilter(request, response);

        assertTrue(MDC.getCopyOfContextMap() == null
                || MDC.getCopyOfContextMap().isEmpty());
    }

    // ─── request with io exception ────────────────────────────────────

    @Test
    @DisplayName("doFilterInternal → filter chain throws IOException → exception rethrown and MDC cleared")
    void doFilterInternal_filterChainThrowsIOException_rethrowsAndClearsMDC()
            throws ServletException, IOException {

        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/v1/io-error");
        when(response.getStatus()).thenReturn(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        doThrow(new IOException("IO error"))
                .when(filterChain)
                .doFilter(request, response);

        IOException exception = assertThrows(
                IOException.class,
                () -> filter.doFilterInternal(request, response, filterChain)
        );

        assertEquals("IO error", exception.getMessage());

        verify(response).setHeader(eq("X-Transaction-Id"), anyString());

        verify(filterChain).doFilter(request, response);

        assertTrue(MDC.getCopyOfContextMap() == null
                || MDC.getCopyOfContextMap().isEmpty());
    }

    // ─── transaction id format ────────────────────────────────────────

    @Test
    @DisplayName("doFilterInternal → transaction id header → has valid format")
    void doFilterInternal_transactionIdHeader_hasValidFormat()
            throws ServletException, IOException {

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(response.getStatus()).thenReturn(HttpServletResponse.SC_OK);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(
                eq("X-Transaction-Id"),
                argThat(transactionId ->
                        transactionId != null
                                && transactionId.length() == 8
                )
        );
    }
}