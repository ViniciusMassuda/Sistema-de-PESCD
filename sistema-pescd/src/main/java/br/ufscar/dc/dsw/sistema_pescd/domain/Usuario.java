package br.ufscar.dc.dsw.sistema_pescd.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
@Entity
@Table(name = "Usuario")
public class Usuario {
    public enum Role { ADMIN, SECRETARIO, PROFESSOR, ALUNO }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;
    @NotBlank
    @Column(nullable = false)
    private String password;
    @NotBlank
    @Column(nullable = false)
    private String nome;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
