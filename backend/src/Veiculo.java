// Representa um veículo dentro do estacionamento
// Guarda placa e horário de entrada
import java.time.LocalDateTime;

public class Veiculo {

    // Placa do veículo (ex: ABC-1234)
    private String placa;

    // Momento em que o veículo entrou no estacionamento
    private LocalDateTime horaEntrada;

    // Construtor: obriga informar a placa ao criar o objeto
    public Veiculo(String placa, LocalDateTime horaEntrada) {
        this.placa = placa;
        this.horaEntrada = horaEntrada;
    }

    // Retorna a placa do veículo
    public String getPlaca() {
        return placa;
    }

    // Retorna a hora de entrada do veículo
    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    // Atualiza a hora de entrada (caso precise ajustar manualmente)
    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    // Método usado quando queremos imprimir o veículo como texto
    @Override
    public String toString() {
        return "Placa: " + placa + " | Entrada: " + horaEntrada;
    }
}


