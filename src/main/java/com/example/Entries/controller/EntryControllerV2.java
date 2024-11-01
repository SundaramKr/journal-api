package com.example.Entries.controller;

import com.example.Entries.entity.Entry;
import com.example.Entries.service.EntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//controller ---> service ---> repository
@RestController
@RequestMapping("/__entry")
public class EntryControllerV2 {

    @Autowired
    private EntryService entryService;

    //GET
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Entry> allEntries = entryService.getAllEntries();
        if (allEntries != null && !allEntries.isEmpty()) {
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No entries found in the collection", HttpStatus.NOT_FOUND);
        }
    }

    //POST
    @PostMapping
    //ResponseEntity<?> is used to denote Wild Card Pattern.
    //Using that we can return the object of a different entity class.
    //We can also use ResponseEntity<Entry> but we can only return the object in the Entry entity class
    public ResponseEntity<?> createEntry(@RequestBody Entry myEntry) {
        try {
            entryService.saveEntry(myEntry);
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
            //entryById.get() takes entryById out of the Optional box
            return new ResponseEntity<>(entryById.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
        }
    }

    //DELETE
    @DeleteMapping("/id/{myID}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId myID) {
        Entry entryById = entryService.findEntryById(myID).orElse(null);
        if (entryById != null) {
            entryService.removeEntryById(myID);
            return new ResponseEntity<>("Deleted ID " + myID, HttpStatus.OK);
            //For 204 HttpStatus.NO_CONTENT, we can't send a body with the ResponseEntity
            //It doesn't expect any content except headers
            //Therefore for Deletion, send a 200 HttpStatus.OK
        } else {
            return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
        }
    }

    //PUT (update)
    @PutMapping("/id/{myID}")
    public ResponseEntity<?> updateEntryById(@PathVariable ObjectId myID, @RequestBody Entry newEntry) {
        Entry oldEntry = entryService.findEntryById(myID).orElse(null);
        if (oldEntry != null) {
            //ID and Date remain the same
            //Only Title and Content need to be updated
            //isEmpty()+isNull()=isBlank() //isBlank() checks if String is empty or null or only whitespace
            oldEntry.setTitle(!newEntry.getTitle().isBlank() ? newEntry.getTitle() : oldEntry.getTitle());
            oldEntry.setContent(!newEntry.getContent().isBlank() ? newEntry.getContent() : oldEntry.getContent());

            //Since we have already updated the oldEntry above
            //We're using the oldEntry to update as it already has the referenced ID in the object.
            //Thereby, updating the values in the old ID and not creating a new ID.
            entryService.saveEntry(oldEntry);
            return new ResponseEntity<>("ID of Updated Entry: " + myID, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
        }
    }
}
