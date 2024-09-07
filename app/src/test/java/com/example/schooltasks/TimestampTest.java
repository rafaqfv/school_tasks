package com.example.schooltasks;

import static org.junit.Assert.*;

import com.google.firebase.Timestamp;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class TimestampTest {

    // Método para converter LocalDate para Timestamp com 00:00 em UTC
    public Timestamp convertToTimestampAtMidnightUTC(LocalDate date) {
        LocalDateTime midnightUTC = date.atStartOfDay(ZoneOffset.UTC).toLocalDateTime();
        Date dateMidnightUTC = Date.from(midnightUTC.toInstant(ZoneOffset.UTC));
        return new Timestamp(dateMidnightUTC);
    }

    @Test
    public void testConvertToTimestampAtMidnightUTC_Today() {
        // Obter a data atual
        LocalDate today = LocalDate.now();

        // Converter para Timestamp (00:00 UTC)
        Timestamp result = convertToTimestampAtMidnightUTC(today);

        // Obter o Timestamp esperado
        LocalDateTime expectedLocalDateTime = today.atStartOfDay(ZoneOffset.UTC).toLocalDateTime();
        Date expectedDate = Date.from(expectedLocalDateTime.toInstant(ZoneOffset.UTC));
        Timestamp expectedTimestamp = new Timestamp(expectedDate);

        // Verificar se o resultado corresponde ao esperado
        assertEquals(expectedTimestamp, result);
    }

    @Test
    public void testConvertToTimestampAtMidnightUTC_ThreeDaysLater() {
        // Obter a data de daqui 3 dias
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);

        // Converter para Timestamp (00:00 UTC)
        Timestamp result = convertToTimestampAtMidnightUTC(threeDaysLater);

        // Obter o Timestamp esperado
        LocalDateTime expectedLocalDateTime = threeDaysLater.atStartOfDay(ZoneOffset.UTC).toLocalDateTime();
        Date expectedDate = Date.from(expectedLocalDateTime.toInstant(ZoneOffset.UTC));
        Timestamp expectedTimestamp = new Timestamp(expectedDate);

        // Verificar se o resultado corresponde ao esperado
        assertEquals(expectedTimestamp, result);
    }
}
