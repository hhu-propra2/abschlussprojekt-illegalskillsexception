package hhu.propra2.illegalskillsexception.frently.backend.Repositories;

import hhu.propra2.illegalskillsexception.frently.backend.Models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IPersonRepository extends CrudRepository<User, Long> {
    List<User> findAll();
}
