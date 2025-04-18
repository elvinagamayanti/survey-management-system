package com.example.sms.util;

import com.example.sms.entity.Tahap;

public class StringUtil {
    
    public static String formatEnumName(Tahap tahap) {
        if (tahap == null) {
            return "";
        }
        
        String name = tahap.toString();
        
        // Replace underscores with spaces
        name = name.replace("_", " ");
        
        // Capitalize first letter of each word
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
}