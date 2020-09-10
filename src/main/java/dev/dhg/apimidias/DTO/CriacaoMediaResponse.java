package dev.dhg.apimidias.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriacaoMediaResponse {

	private Integer id;
	private String nome;
	private byte[] arquivo;
	private Integer duracao;

	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDate dataUpload;

}