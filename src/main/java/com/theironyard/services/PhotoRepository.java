package com.theironyard.services;

import com.theironyard.entities.Photo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Caroline on 3/15/16.
 */
public interface PhotoRepository extends CrudRepository<Photo, Integer> {
}
