package ru.practicum.shareit.booking;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UnsupportedStatus;

@Component
public class StringToStateEnumConverter implements Converter<String, State> {
        @Override
        public State convert(String source) {
            try {
                return State.valueOf(source);
            } catch(Exception e) {
                throw new UnsupportedStatus("Unknown state: " + source);
            }
        }
}
