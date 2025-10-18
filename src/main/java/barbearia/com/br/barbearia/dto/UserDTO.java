package barbearia.com.br.barbearia.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nome;
    private String email;
    private String role;
    private String telefone;
    private Boolean ativo;
}