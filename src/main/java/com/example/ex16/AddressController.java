package com.example.ex16;

import java.util.List;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final SessionFactory sf;
    private Session ss;
    @PostConstruct
    void init() {
        ss = sf.openSession();
    }

    // return a list of all
    @GetMapping
    public ResponseEntity<List<Address>> getAll() {

        Query q = ss.createQuery("from Address");

        return new ResponseEntity<>(q.list(), HttpStatus.OK);
    }

    // return a single item
    @GetMapping({"/{id}"})
    public ResponseEntity<Address> getById(@PathVariable final long id) {

        Address a = ss.get(Address.class, id);

        if(a == null) {
            return new ResponseEntity<>(a, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(a, HttpStatus.OK);
        }
        
    }

    // return related building
    @GetMapping({"/{id}/building"})
    public ResponseEntity<Building> getBuilding(@PathVariable final long id) {

        Address a = ss.get(Address.class, id);

        if(a == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {

            return new ResponseEntity<>(a.getBuilding(), HttpStatus.OK);
        }
        
    }

    // creates a new item and saves it to the DB and returns the created item
    @PostMapping
    public ResponseEntity<Address> create(@RequestBody Address a) {

        Transaction t = ss.beginTransaction();
        ss.persist(a);
        t.commit();
        ss.refresh(a);

        return new ResponseEntity<>(a, HttpStatus.CREATED);
    }

    // deletes the specified Id and returns the deleted item
    @DeleteMapping({"/{id}"})
    public ResponseEntity<Address> delete(@PathVariable("id") Long id) {

        Address a = ss.get(Address.class, id);

        if(a == null) {
            return new ResponseEntity<>(a, HttpStatus.NOT_FOUND);
        } else {
            Transaction t = ss.beginTransaction();
            ss.delete(a);
            t.commit();
            return new ResponseEntity<>(a, HttpStatus.OK);
        }
    }

    // updates the specified Id and returns the updated item
    @PutMapping({"/{id}"})
    public ResponseEntity<Address> edit(@PathVariable("id") Long id, @RequestBody Address changed) {


        Address a = ss.get(Address.class, id);

        if(a == null) {
            return new ResponseEntity<>(a, HttpStatus.NOT_FOUND);
        } else {
            a.setAddressText(changed.getAddressText());
            a.setZipCode(changed.getZipCode());
            Transaction t = ss.beginTransaction();
            ss.flush(); // persistent object already changed
            t.commit();
            return new ResponseEntity<>(a, HttpStatus.OK);
        }
    }

}
