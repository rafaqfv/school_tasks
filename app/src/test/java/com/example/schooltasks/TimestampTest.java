package com.example.schooltasks;

import static org.junit.Assert.*;

import com.google.firebase.Timestamp;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

public class TimestampTest {

    // Método para converter LocalDate para Timestamp com 00:00 em UTC
    @Test
    public void testeHoras() {
        Date hoje = new Date();

        System.out.println(hoje);
    }

    @Test
    public void testConvertToTimestampAtMidnightUTC_Today() {
    }

    @Test
    public void testConvertToTimestampAtMidnightUTC_ThreeDaysLater() {

    }
}
