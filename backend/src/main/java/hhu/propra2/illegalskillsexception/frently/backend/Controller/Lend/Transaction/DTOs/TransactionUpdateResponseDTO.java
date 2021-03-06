package hhu.propra2.illegalskillsexception.frently.backend.Controller.Lend.Transaction.DTOs;

import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.Transaction;
import lombok.Data;

@Data
public class TransactionUpdateResponseDTO {
    String status;

    public TransactionUpdateResponseDTO(Transaction.Status type) {
        this.status = type.toString();
    }
}
