package com.example.Entries.controller;

import com.example.Entries.entity.Entry;
import com.example.Entries.entity.User;
import com.example.Entries.service.EntryService;
import com.example.Entries.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//controller ---> service ---> repository
@RestController
@RequestMapping("/entry")
public class EntryControllerV4 {

    @Autowired
    private EntryService entryService;

    @Autowired
    private UserService userService;

    //GET
    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        if (user == null)
            return new ResponseEntity<>("Username Not Found", HttpStatus.NOT_FOUND);
        List<Entry> allEntries = user.getEntries();
        if (allEntries != null && !allEntries.isEmpty()) {
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No entries found in the collection", HttpStatus.NOT_FOUND);
        }
    }

    //POST
    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody Entry myEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUserName(userName);
            if (user == null)
                return new ResponseEntity<>("Username Not Found", HttpStatus.NOT_FOUND);
            entryService.saveEntryUsername(myEntry, userName);
            return new ResponseEntity<>("ID of Created Entry " + myEntry.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //PathParam or PathVariable
    @GetMapping("/id/{myID}")
    public ResponseEntity<?> getEntryById(@PathVariable ObjectId myID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Entry entryById = entryService.findEntryById(myID).orElse(null);
        if (user.getEntries().contains(entryById) && entryById != null) {
            return new ResponseEntity<>(entryById, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
        }
    }

    //DELETE
    @DeleteMapping("/id/{myID}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId myID) {
        //userName validation
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Entry entryById = entryService.findEntryById(myID).orElse(null);
        if (user.getEntries().contains(entryById) && entryById != null) {
            entryService.removeEntryByUsernameId(myID, userName);
            return new ResponseEntity<>("Deleted Entry: " + (entryById), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
        }
    }

    //PUT (update)
    @PutMapping("/id/{myID}")
    public ResponseEntity<?> updateEntryById(@PathVariable ObjectId myID, @RequestBody Entry newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Entry oldEntry = entryService.findEntryById(myID).orElse(null);
        if (user.getEntries().contains(oldEntry) && oldEntry != null) {
            oldEntry.setTitle(!newEntry.getTitle().isBlank() ? newEntry.getTitle() : oldEntry.getTitle());
            oldEntry.setContent(!newEntry.getContent().isBlank() ? newEntry.getContent() : oldEntry.getContent());
            entryService.saveEntry(oldEntry);
            return new ResponseEntity<>("ID of Updated Entry: " + myID, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
        }
    }
}
