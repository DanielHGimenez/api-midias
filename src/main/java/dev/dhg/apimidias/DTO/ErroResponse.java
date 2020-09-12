package dev.dhg.apimidias.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ErroResponse<T> {

    private T mensagem;

}
