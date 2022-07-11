package repository.user;

import java.util.Optional;

import model.User;

public interface UserRepository {

	Optional<User> getById(Long id);
	
	Optional<User> getByUsername(String username);
	
}
