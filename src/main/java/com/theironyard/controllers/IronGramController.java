package com.theironyard.controllers;

import com.sun.deploy.net.HttpResponse;
import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utils.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caroline on 3/15/16.
 */
@RestController
public class IronGramController {
    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;

    Server dbui = null;

    @PostConstruct
    public void init() throws SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findByName(username);
        if (user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        }
        else if (!PasswordStorage.verifyPassword(password, user.getPasswordHash())) {
            throw new Exception("Wrong password");
        }
        session.setAttribute("username", username);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return users.findByName(username);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(MultipartFile photo, Integer timeEntry, String recipient, HttpSession session, HttpServletResponse response) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }

        if (!photo.getContentType().startsWith("image")) {
              throw new Exception("You can only upload images.");
        }

        User user = users.findByName(username);

        File photoFile = File.createTempFile("image", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        User user1 = users.findByName(recipient);

        Photo p = new Photo(user, user1, photoFile.getName(), timeEntry);
        photos.save(p);

        response.sendRedirect("/");

        return p;
    }

    @RequestMapping(path = "/photos", method = RequestMethod.GET)
    public List<Photo> showPhotos(HttpSession session) {
        // Help by Alex Hughes and Paige Hetherington
        String username = (String) session.getAttribute("username");
        User user = users.findByName(username);
        List<Photo> photosList = (List<Photo>)photos.findAll();
        List<Photo> sentPhotos = (List<Photo>)photos.findByRecipient(user);

        for (Photo photo : photosList) {
            if (photo.getRecipient() == null) {
                sentPhotos.add(photo);
            }
        }

        for (Photo photo : sentPhotos) {
            if (photo.getDateTime() == null) {
                photo.setDateTime(LocalDateTime.now());
                photos.save(photo);
            }
            else if (LocalDateTime.now().isAfter(photo.getDateTime().plusSeconds(photo.getTimeEntry()))) {
                photos.delete(photo);
                File f = new File("public/" + photo.getFilename());
                f.delete();
            }
        }

        return sentPhotos;
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        response.sendRedirect("/");
    }
}
