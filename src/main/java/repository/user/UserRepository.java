package repository.user;

import java.util.List;
import java.util.Optional;

import model.User;

public interface UserRepository {
	
	Optional<User> getByUsername(String username);
	
	List<User> getAll();
}
