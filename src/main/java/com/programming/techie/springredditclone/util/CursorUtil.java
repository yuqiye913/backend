package com.programming.techie.springredditclone.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CursorUtil {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Encode cursor data to Base64 string
     */
    public String encodeCursor(Instant createdDate, Long id) {
        try {
            Map<String, Object> cursorData = new HashMap<>();
            cursorData.put("createdDate", createdDate.toString());
            cursorData.put("id", id);
            
            String json = objectMapper.writeValueAsString(cursorData);
            return Base64.getEncoder().encodeToString(json.getBytes());
        } catch (JsonProcessingException e) {
            log.error("Error encoding cursor", e);
            throw new RuntimeException("Error encoding cursor", e);
        }
    }
    
    /**
     * Decode cursor from Base64 string
     */
    public CursorData decodeCursor(String cursor) {
        try {
            String json = new String(Base64.getDecoder().decode(cursor));
            Map<String, Object> cursorData = objectMapper.readValue(json, Map.class);
            
            Instant createdDate = Instant.parse((String) cursorData.get("createdDate"));
            Long id = Long.valueOf(cursorData.get("id").toString());
            
            return new CursorData(createdDate, id);
        } catch (Exception e) {
            log.error("Error decoding cursor: {}", cursor, e);
            throw new RuntimeException("Invalid cursor format", e);
        }
    }
    
    /**
     * Cursor data holder
     */
    public static class CursorData {
        private final Instant createdDate;
        private final Long id;
        
        public CursorData(Instant createdDate, Long id) {
            this.createdDate = createdDate;
            this.id = id;
        }
        
        public Instant getCreatedDate() {
            return createdDate;
        }
        
        public Long getId() {
            return id;
        }
    }
} 