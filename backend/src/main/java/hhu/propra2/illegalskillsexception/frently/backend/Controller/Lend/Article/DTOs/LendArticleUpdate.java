package hhu.propra2.illegalskillsexception.frently.backend.Controller.Lend.Article.DTOs;

import lombok.Data;

@Data
public class LendArticleUpdate {
    private Long articleId;
    private String title;
    private Double deposit;
    private String description;
    private Double dailyRate;
    private String location;
}
