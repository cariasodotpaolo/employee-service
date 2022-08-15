package io.challenge.hr.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author Paolo Cariaso
 * @date 13/8/2022 7:42 PM
 */
public class DateUtil {

    public static LocalDate parseStartDate(String strDate) throws DateTimeParseException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");;
        String dateParts[] = strDate.split("-");

        if (dateParts[0].length() == 2) {

            formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
        }

        LocalDate localDate = LocalDate.parse(strDate, formatter);

        return localDate;
    }
}
