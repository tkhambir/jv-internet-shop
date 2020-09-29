package com.internet.shop.security;

import com.internet.shop.lib.Inject;
import com.internet.shop.lib.Service;
import com.internet.shop.lib.exceptions.AuthenticationException;
import com.internet.shop.model.User;
import com.internet.shop.service.UserService;
import com.internet.shop.util.HashUtil;

import static com.internet.shop.util.HashUtil.hashPassword;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String login, String password) throws AuthenticationException {
        return userService.findByLogin(login)
                .filter(u -> isValid(password, u))
                .orElseThrow(() -> new AuthenticationException("Incorrect username or password."));
    }

    private static boolean isValid(String password, User user) {
        return hashPassword(password, user.getSalt()).equals(user.getPassword());
    }
}
