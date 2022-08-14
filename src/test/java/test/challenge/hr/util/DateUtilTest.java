package test.challenge.hr.util;

import io.challenge.hr.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Paolo Cariaso
 * @date 13/8/2022 7:53 PM
 */
public class DateUtilTest {

    @Test
    void testDateParse() {

        LocalDate format1 = DateUtil.parseStartDate("2022-07-15");
        LocalDate format2 = DateUtil.parseStartDate("15-Jul-22");

        assertEquals("2022-07-15", format1.toString());
        assertEquals("2022-07-15", format2.toString());

        System.out.println(format1);
        System.out.println(format2);
    }
}
