package hhu.propra2.illegalskillsexception.frently.backend.Controller.Borrow.Article.IServices;

import hhu.propra2.illegalskillsexception.frently.backend.Data.Exceptions.NoSuchArticleException;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.BorrowArticle;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IBorrowArticleService {
    List<BorrowArticle> retrieveAllButOwn(Authentication auth);

    List<BorrowArticle> retrieveAllOfOwner(long ownerId);

    BorrowArticle getArticleById(long articleId) throws NoSuchArticleException;
}
