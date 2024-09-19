package com.blueteam.historyEdu.entities.convert;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        // Convert List<String> to a single String (e.g., "item1,item2,item3")
        return stringList != null ? String.join(SPLIT_CHAR, stringList) : "";
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        // Convert a single String back to List<String>
        return s != null && !s.isEmpty() ? Arrays.asList(s.split(SPLIT_CHAR)) : null;
    }
}
