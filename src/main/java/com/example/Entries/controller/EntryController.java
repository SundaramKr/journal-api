package com.example.Entries.controller;

import com.example.Entries.entity.Entry;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/___entry")
public class EntryController {
    //Simulating Database Table
    public Map<ObjectId, Entry> Entries = new HashMap<>();

    //localhost:8080/entry GET
    @GetMapping
    public List<Entry> getAll() {
        return new ArrayList<>(Entries.values());
    }

    //localhost:8080/entry POST
    @PostMapping
    public String createEntry(@RequestBody Entry myEntry){
        Entries.put(myEntry.getId(), myEntry);
        return "Posted ID "+myEntry.getId();
    }

    //PathParam or PathVariable
    @GetMapping("/id/{myID}")
    public Entry getEntryById(@PathVariable ObjectId myID) {
        return Entries.get(myID);
    }

    //DELETE
    @DeleteMapping("/id/{myID}")
    public String deleteEntryById(@PathVariable ObjectId myID) {
        Entries.remove(myID);
        return "Deleted ID "+myID;
    }

    //PUT (update)
    @PutMapping("/id/{myID}")
    public String updateEntryById(@PathVariable ObjectId myID, @RequestBody Entry myEntry) {
        Entries.put(myID, myEntry);
        return "Updated ID "+myID;
    }
}
