package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFormattedTextField;

public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormat.parse(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            if (value instanceof Calendar) {
                return dateFormat.format(((Calendar) value).getTime());
            } else if (value instanceof Date) {
                return dateFormat.format((Date) value);
            }
        }
        return "";
    }
}
