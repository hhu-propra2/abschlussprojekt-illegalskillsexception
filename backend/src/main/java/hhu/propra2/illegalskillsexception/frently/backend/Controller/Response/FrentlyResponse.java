package hhu.propra2.illegalskillsexception.frently.backend.Controller.Response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FrentlyResponse {
    private FrentlyError error;
    private Object data;
}
