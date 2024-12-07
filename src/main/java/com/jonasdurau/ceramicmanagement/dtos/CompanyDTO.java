package com.jonasdurau.ceramicmanagement.dtos;

import com.jonasdurau.ceramicmanagement.entities.Company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CompanyDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "O CNPJ é obrigatório")
    private String cnpj;

    @NotBlank(message = "A senha é obrigatória")
    private String password;

    public CompanyDTO() {
    }

    public CompanyDTO(String name, String email, String cnpj, String password) {
        this.name = name;
        this.email = email;
        this.cnpj = cnpj;
        this.password = password;
    }

    public CompanyDTO(Company entity) {
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.cnpj = entity.getCnpj();
        this.password = entity.getPassword();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getPassword() {
        return password;
    }
}
