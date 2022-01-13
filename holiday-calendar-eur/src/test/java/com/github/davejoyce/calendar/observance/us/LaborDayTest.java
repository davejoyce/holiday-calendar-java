/*******************************************************************************
 * Holiday Calendar - A library for definition and calculation of holiday calendars
 * Copyright (C) 2022 David Joyce
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

package com.github.davejoyce.calendar.observance.us;

import com.github.davejoyce.calendar.observance.AbstractObservanceTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class LaborDayTest extends AbstractObservanceTest<LaborDay> {

    public LaborDayTest() {
        super(new LaborDay());
    }

    @Override
    protected List<Object[]> createData() {
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{ 1893, null });
        data.add(new Object[]{ 1976, LocalDate.of(1976, Month.SEPTEMBER, 6) });
        data.add(new Object[]{ 1977, LocalDate.of(1977, Month.SEPTEMBER, 5) });
        data.add(new Object[]{ 1990, LocalDate.of(1990, Month.SEPTEMBER, 3) });
        data.add(new Object[]{ 2021, LocalDate.of(2021, Month.SEPTEMBER, 6) });
        data.add(new Object[]{ 2022, LocalDate.of(2022, Month.SEPTEMBER, 5) });

        return data;
    }

}