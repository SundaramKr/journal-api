package com.example.Entries.controller;

import com.example.Entries.entity.Entry;
import com.example.Entries.entity.User;
import com.example.Entries.service.EntryService;
import com.example.Entries.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//controller ---> service ---> repository
@RestController
@RequestMapping("/_entry")
public class EntryControllerV3 {

    @Autowired
    private EntryService entryService;

    @Autowired
    private UserService userService;

    //GET
    @GetMapping("{userName}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if(user==null)
            return new ResponseEntity<>("Username Not Found", HttpStatus.NOT_FOUND);
        List<Entry> allEntries = user.getEntries();
        if (allEntries != null && !allEntries.isEmpty()) {
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No entries found in the collection", HttpStatus.NOT_FOUND);
        }
    }

    //POST
    @PostMapping("{userName}")
    public ResponseEntity<?> createEntry(@RequestBody Entry myEntry, @PathVariable String userName) {
        try {
            User user = userService.findByUserName(userName);
            if(user==null)
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
        Optional<Entry> entryById = entryService.findEntryById(myID);
        if (entryById.isPresent()) {
            return new ResponseEntity<>(entryById.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
        }
    }

    //DELETE
    @DeleteMapping("/id/{userName}/{myID}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId myID, @PathVariable String userName) {
        //userName validation
        User user = userService.findByUserName(userName);
        if(user==null)
            return new ResponseEntity<>("Username Not Found", HttpStatus.NOT_FOUND);
        //myID validation
        Entry entryById = entryService.findEntryById(myID).orElse(null);
        if (entryById != null) {
            entryService.removeEntryByUsernameId(myID, userName);
            return new ResponseEntity<>("Deleted ID " + myID, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
        }
    }

    //PUT (update)
    @PutMapping("/id/{userName}/{myID}")
    public ResponseEntity<?> updateEntryById(@PathVariable ObjectId myID,
                                             @PathVariable String userName,
                                             @RequestBody Entry newEntry) {
        //userName validation
        User user = userService.findByUserName(userName);
        if(user==null)
            return new ResponseEntity<>("Username Not Found", HttpStatus.NOT_FOUND);
        //myID validation
        Entry oldEntry = entryService.findEntryById(myID).orElse(null);
        if (oldEntry != null) {
            oldEntry.setTitle(!newEntry.getTitle().isBlank() ? newEntry.getTitle() : oldEntry.getTitle());
            oldEntry.setContent(!newEntry.getContent().isBlank() ? newEntry.getContent() : oldEntry.getContent());

            entryService.saveEntry(oldEntry);
            return new ResponseEntity<>("ID of Updated Entry: " + myID, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
        }
    }
}
