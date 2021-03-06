package hhu.propra2.illegalskillsexception.frently.backend.Controller.User.DTOs;


import lombok.Data;

import java.util.List;

@Data
public class UserDetailResponse {

    private String username;
    private String email;

    private String propayUsername;
    private double accountBalance;

    private List<MoneyTransferDTO> completedTransactions;
}
