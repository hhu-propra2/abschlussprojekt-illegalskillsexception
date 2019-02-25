package hhu.propra2.illegalskillsexception.frently.backend.Controller.Borrow.Article.Services;

import hhu.propra2.illegalskillsexception.frently.backend.Controller.Borrow.Article.IServices.IBorrowArticleService;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Exceptions.NoSuchArticleException;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.Article;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Repositories.IArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BorrowArticleService implements IBorrowArticleService {

    private final IArticleRepository articles;

    @Override
    public List<Article> retrieveAll() {
        return articles.findAll();
    }

    @Override
    public List<Article> retrieveAllOfOwner(long ownerId) {
        return articles.findAllByOwner_Id(ownerId);
    }

    @Override
    public Article getArticleById(long articleId) throws NoSuchArticleException {
        Optional<Article> opt = articles.findById(articleId);
        return opt.orElseThrow(NoSuchArticleException::new);
    }
}