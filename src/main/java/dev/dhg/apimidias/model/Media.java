package dev.dhg.apimidias.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Media {

	@Id
	@GeneratedValue
	private Integer id;

	@Length(max = 512)
	private String nome;

	private String nomeArquivo;

	@Length(max = 512)
	private String URL;

	private Integer duracao;

	@NotNull
	@Column(name = "dt_upload")
	private LocalDate dataUpload;

	@NotNull
	private Boolean deleted;

}
