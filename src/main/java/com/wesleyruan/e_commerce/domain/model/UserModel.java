package com.wesleyruan.e_commerce.domain.model;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.wesleyruan.e_commerce.domain.enums.RolesEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private boolean isDeleted; // depois que alterado para soft delete, esse campo vai ser usado para marcar o usuário como deletado, sem realmente removê-lo do banco de dados. Assim, quando um usuário for "deletado", em vez de ser removido, o campo isDeleted será definido como true. E quando for necessário recuperar ou listar os usuários, a aplicação pode filtrar os resultados para incluir apenas aqueles com isDeleted definido como false, garantindo que os usuários "deletados" não sejam exibidos ou acessíveis.

    private String name;
    
    private String email;

    private String password;

    private String phone;

    private RolesEnum role;

    private LocalDate dateOfBirth;


    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @PrePersist 
    public void prePersist() { // essa caralha aqui vai ser chamada antes de persistir o objeto no banco de dados, ou seja, antes de salvar um novo usuário. Ela é usada para definir os valores iniciais de createdAt, updatedAt e role. 
        if (this.role == null) {
            this.role = RolesEnum.USER; // Define o papel padrão como USER
        }
        
        this.isDeleted = false; // Define o status de exclusão para falso
    }

}