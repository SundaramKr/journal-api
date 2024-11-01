package com.example.Entries.service;

import com.example.Entries.entity.Entry;
import com.example.Entries.entity.User;
import com.example.Entries.repository.EntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//controller ---> service ---> repository
@Component
public class EntryService {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private UserService userService;

    //GET
    public List<Entry> getAllEntries() {
        return entryRepository.findAll();
    }

    //POST & PUT(update)
    public void saveEntry(Entry entry) {
        entry.setDate(LocalDateTime.now()); //To set date and time while both posting and putting (updating)
        entryRepository.save(entry);
    }

    @Transactional
    public void saveEntryUsername(Entry entry, String userName) {
        User user = userService.findByUserName(userName);
        entry.setDate(LocalDateTime.now());
        Entry saved = entryRepository.save(entry);
        user.getEntries().add(saved);
        userService.saveEntry(user);
    }

    //PathParam or GET by id
    public Optional<Entry> findEntryById(ObjectId myID) {
        return entryRepository.findById(myID);
    }

    //DELETE
    public void removeEntryById(ObjectId myID) {
        entryRepository.deleteById(myID);
    }

    @Transactional
    public void removeEntryByUsernameId(ObjectId myID, String userName) {
        User user = userService.findByUserName(userName);
        //user.getEntries().remove(findEntryById(myID).orElse(null));       NOT TESTED
        user.getEntries().removeIf(x -> x.getId().equals(myID));
        userService.saveEntry(user);
        entryRepository.deleteById(myID);
    }
}
