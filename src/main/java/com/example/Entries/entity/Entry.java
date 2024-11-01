package com.example.Entries.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "entries")
@Data
@NoArgsConstructor
public class Entry {
    @Id
    private ObjectId id;
    private String title;
    private String content;
    private LocalDateTime date;
}
