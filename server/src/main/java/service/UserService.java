package service;
import java.util.UUID;

import dataaccess.*;
import model.*;

public class UserService {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

}
