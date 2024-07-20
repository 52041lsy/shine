package com.example.demo;

import com.example.demo.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping(path = "/user")
public class UserControler {
        @Autowired
        private UserService userService;

        @GetMapping(path = "/getAll")
        public @ResponseBody Iterable<User> getAllUsers()
        {
            return userService.getAllUsers();
        }

        @PostMapping(path = "/get")
        public @ResponseBody Optional<User> findById(String id)
        {
            return userService.findById(id);
        }

        @PostMapping(path = "/add")
        public @ResponseBody boolean add(@RequestParam String id,@RequestParam String paassword,@RequestParam String phonenumber)
        {
            return userService.add(id,paassword,phonenumber);
        }

        @PostMapping(path = "/modifynum")
        public @ResponseBody boolean modifynum(@RequestParam String id,@RequestParam String phonenumber)
        {
            return userService.modifynum(id,phonenumber);
        }

        @PostMapping(path = "/delete")
        public @ResponseBody boolean deleteById(@RequestParam String id)
        {
            return userService.deleteById(id);
        }
}

