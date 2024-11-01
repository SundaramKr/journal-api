package com.example.Entries.repository;

import com.example.Entries.entity.Entry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntryRepository extends MongoRepository<Entry, ObjectId> {
}
