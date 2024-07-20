package com.example.demo;

import com.example.demo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Iterable<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public Optional<User> findById(String id)
    {
        return userRepository.findById(id);
    }

    public boolean add(String id,String password,String phonenumber)
    {
        User user = new User();
        user.setId(id);
        user.setPassword(password);
        user.setUsernumber(phonenumber);
        userRepository.save(user);
        return true;
    }

    public boolean modifynum(String id,String phonenumber)
    {
        //User user = new User();
        //user.setId(id);
        //user.setUsernumber(phonenumber);
        userRepository.modifynum(id,phonenumber);
        return true;
    }

    public boolean deleteById(String id)
    {
        return userRepository.deleteById(id) != 0;
    }
}
