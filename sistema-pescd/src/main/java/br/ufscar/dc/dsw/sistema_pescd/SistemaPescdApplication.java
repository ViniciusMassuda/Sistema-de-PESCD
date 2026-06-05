// Arquivo: SistemaPescdApplication.java - Criado para o sistema PESCD
// Este codigo foi feito de forma simples para facilitar o entendimento
package br.ufscar.dc.dsw.sistema_pescd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })public class SistemaPescdApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaPescdApplication.class, args);
	}

}


