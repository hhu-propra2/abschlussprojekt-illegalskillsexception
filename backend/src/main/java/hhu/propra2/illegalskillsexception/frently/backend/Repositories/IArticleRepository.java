package hhu.propra2.illegalskillsexception.frently.backend.Repositories;

import hhu.propra2.illegalskillsexception.frently.backend.Models.Article;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IArticleRepository extends CrudRepository<Article, Long> {
    List<Article> findAll();
    List<Article> findAllByOwner_Id(Long ownerId);

}