package ru.job4j.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.chat.repository.PersonRepository;
import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final PersonRepository users;

    public UserDetailsServiceImpl(final PersonRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = users.findPersonByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(user.getLogin(), user.getPassword(), emptyList());
    }
}
