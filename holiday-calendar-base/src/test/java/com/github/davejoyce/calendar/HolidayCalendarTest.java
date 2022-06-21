/*******************************************************************************
 * Holiday Calendar - A library for definition and calculation of holiday calendars
 * Copyright (C) 2021 David Joyce
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 ******************************************************************************/

package com.github.davejoyce.calendar;

import com.github.davejoyce.calendar.function.DateRoll;
import org.testng.annotations.Test;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static com.github.davejoyce.calendar.TestObjects.*;
import static org.testng.Assert.*;

public class HolidayCalendarTest {

    private static final ZoneId ZONE_ID_NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId ZONE_ID_TOKYO = ZoneId.of("Asia/Tokyo");
    private static final TimeZone TZ_NEW_YORK = TimeZone.getTimeZone(ZONE_ID_NEW_YORK);

    @Test
    public void testBuilder_NoHolidays() {
        HolidayCalendar holidayCalendar = HolidayCalendar.builder()
                                                         .code("FRB")
                                                         .name("Federal Reserve Board")
                                                         .build();
        assertNotNull(holidayCalendar.getHolidays());
        assertTrue(holidayCalendar.getHolidays().isEmpty());
    }

    @Test
    public void testBuilder_NoWeekendDays() {
        HolidayCalendar holidayCalendar = HolidayCalendar.builder()
                                                         .code("FRB")
                                                         .name("Federal Reserve Board")
                                                         .build();
        assertEquals(holidayCalendar.getWeekendDays(), HolidayCalendar.STANDARD_WEEKEND);
    }

    @Test
    public void testBuilder_NoDateRoll() {
        HolidayCalendar holidayCalendar = HolidayCalendar.builder()
                                                         .code("FRB")
                                                         .name("Federal Reserve Board")
                                                         .build();
        assertEquals(holidayCalendar.getDateRoll(), HolidayCalendar.NO_ROLL);
    }

    @Test
    public void testGetCode() {
        HolidayCalendar holidayCalendar = HolidayCalendar.builder()
                                                         .code("FRB")
                                                         .name("Federal Reserve Board")
                                                         .build();
        assertEquals(holidayCalendar.getCode(), "FRB");
    }

    @Test
    public void testGetName() {
        HolidayCalendar holidayCalendar = HolidayCalendar.builder()
                                                         .code("FRB")
                                                         .name("Federal Reserve Board")
                                                         .build();
        assertEquals(holidayCalendar.getName(), "Federal Reserve Board");
    }

    @Test
    public void testGetDateRoll() {
        final DateRoll usDateRoll = createDateRollUS();
        HolidayCalendar holidayCalendar = new HolidayCalendar("FRB", "Federal Reserve Board", usDateRoll, null, null);
        assertEquals(holidayCalendar.getDateRoll(), usDateRoll);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testGetHolidays_Unmodifiable() {
        HolidayCalendar holidayCalendar = HolidayCalendar.builder()
                                                         .code("FRB")
                                                         .name("Federal Reserve Board")
                                                         .build();
        Set<Holiday> view = holidayCalendar.getHolidays();
        assertNotNull(view, "Expected non-null holidays");

        view.add(null);
        fail("Holidays set view should be unmodifiable!");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testGetWeekendDays_Unmodifiable() {
        HolidayCalendar holidayCalendar = HolidayCalendar.builder()
                                                         .code("FRB")
                                                         .name("Federal Reserve Board")
                                                         .build();
        Set<DayOfWeek> view = holidayCalendar.getWeekendDays();
        assertNotNull(view, "Expected non-null weekendDays");

        view.add(null);
        fail("WeekendDays set view should be unmodifiable!");
    }

    @Test
    public void testToString() {
        HolidayCalendar holidayCalendar = createHolidayCalendarSifmaUS();
        String actual = holidayCalendar.toString();
        assertEquals(actual, "HolidayCalendar[code='SIFMA', name='SIFMA Holiday Recommendations (US)']");
    }

    @Test
    public void testCalculate() {
        HolidayCalendar calendar = createHolidayCalendarSifmaUS();
        List<HolidayDate> dates = calendar.calculate(2021);

        assertEquals(dates.get(0).getHoliday().getName(), "New Year's Day");
        assertEquals(dates.get(0).getDate(), LocalDate.of(2021, Month.JANUARY, 1));
        assertEquals(dates.get(dates.size() - 1).getHoliday().getName(), "Christmas Day");
        assertEquals(dates.get(dates.size() - 1).getDate(), LocalDate.of(2021, Month.DECEMBER, 24)); // rolled back 1 day
    }

    @Test
    public void testMerge() {
        HolidayCalendar sifmaCalendar = createHolidayCalendarSifmaUS();
        HolidayCalendar frbCalendar = HolidayCalendar.builder()
                                                     .code("FRB")
                                                     .name("Federal Reserve Board")
                                                     .build();
        assertEquals(sifmaCalendar.getHolidays().size(), 10);

        HolidayCalendar merged = sifmaCalendar.merge(frbCalendar);
        assertEquals(merged.getCode(), "SIFMA/FRB");
        assertEquals(merged.getWeekendDays(), HolidayCalendar.STANDARD_WEEKEND);
        assertEquals(merged.getHolidays().size(), 10);
    }

    @Test
    public void testMergeNull() {
        HolidayCalendar sifmaCalendar = createHolidayCalendarSifmaUS();
        HolidayCalendar merged = sifmaCalendar.merge(null);
        assertSame(merged, sifmaCalendar);
    }

    @Test
    public void testMergeThis() {
        HolidayCalendar sifmaCalendar = createHolidayCalendarSifmaUS();
        HolidayCalendar merged = sifmaCalendar.merge(sifmaCalendar);
        assertSame(merged, sifmaCalendar);
    }

    @Test
    public void testIsWeekendUTC_Date() {
        HolidayCalendar calendar = createHolidayCalendarSifmaUS();

        LocalDate date = LocalDate.of(2021, Month.DECEMBER, 19);
        Date d = new Date(date.atStartOfDay(ZONE_ID_NEW_YORK).toInstant().toEpochMilli());
        boolean actual = calendar.isWeekendUTC(d);
        assertTrue(actual);
    }

    @Test
    public void testIsWeekendUTC_Instant() {
        HolidayCalendar calendar = createHolidayCalendarSifmaUS();

        LocalDate date = LocalDate.of(2021, Month.DECEMBER, 19);
        Instant i = date.atStartOfDay(ZONE_ID_NEW_YORK).toInstant();
        boolean actual = calendar.isWeekendUTC(i);
        assertTrue(actual);
    }

    private HolidayCalendar createHolidayCalendarSifmaUS() {
        return HolidayCalendar.builder()
                              .code("SIFMA")
                              .name("SIFMA Holiday Recommendations (US)")
                              .dateRoll(createDateRollUS())
                              .weekendDays(HolidayCalendar.STANDARD_WEEKEND)
                              .holiday(new FixedHoliday("New Year's Day", "", Month.JANUARY, 1))
                              .holiday(new FloatingHoliday("Martin Luther King Jr. Day", "Honor of Martin Luther King Jr's birthday", createObservanceMlkDay()))
                              .holiday(new FloatingHoliday("Presidents' Day", "Honor of George Washington's birthday", createObservancePresidentsDay()))
                              .holiday(new FloatingHoliday("Memorial Day", "Mourning of fallen US military personnel", createObservanceMemorialDay()))
                              .holiday(new FixedHoliday("Independence Day", "Fourth of July", Month.JULY, 4))
                              .holiday(new FloatingHoliday("Labor Day", "Recognition of American labor", createObservanceLaborDay()))
                              .holiday(new FloatingHoliday("Columbus Day", "Anniversary of arrival of Columbus in Americas", createObservanceColumbusDay()))
                              .holiday(new FixedHoliday("Veterans Day", "Honor of all US veterans", Month.NOVEMBER, 11))
                              .holiday(new FloatingHoliday("Thanksgiving Day", "Day of giving thanks", createObservanceThanksgiving()))
                              .holiday(new FixedHoliday("Christmas Day", "", Month.DECEMBER, 25))
                              .build();
    }

}