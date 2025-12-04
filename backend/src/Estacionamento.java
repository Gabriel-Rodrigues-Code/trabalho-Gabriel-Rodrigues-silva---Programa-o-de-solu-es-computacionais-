// Controla o estacionamento: vagas, lista de veículos e faturamento
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Estacionamento {

    // Capacidade máxima de veículos no estacionamento
    private int capacidadeMaxima;

    // Lista de veículos atualmente estacionados
    private ArrayList<Veiculo> veiculosEstacionados;

    // Total de dinheiro arrecadado com as saídas
    private double totalArrecadado;

    // Valores fixos do estacionamento
    private static final double PRIMEIRA_HORA = 12.0;
    private static final double HORA_ADICIONAL = 8.0;

    // Construtor: define a capacidade e inicializa a lista
    public Estacionamento(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
        this.veiculosEstacionados = new ArrayList<>();
        this.totalArrecadado = 0.0;
    }

    // Tenta registrar a entrada de um veículo
    // Retorna true se conseguiu, false se o estacionamento está cheio
    public boolean registrarEntrada(String placa, LocalDateTime horaEntrada) {
        if (veiculosEstacionados.size() >= capacidadeMaxima) {
            return false; // estacionamento cheio, não entra
        }

        // Cria um novo veículo e adiciona na lista
        Veiculo v = new Veiculo(placa, horaEntrada);
        veiculosEstacionados.add(v);
        return true;
    }

    // Registra a saída de um veículo pela placa
    // Retorna o valor a pagar, ou -1 se veículo não foi encontrado
    public double registrarSaida(String placa, LocalDateTime horaSaida) {
        Veiculo encontrado = buscarVeiculo(placa);

        if (encontrado == null) {
            return -1; // não achou o veículo no estacionamento
        }

        // Calcula tempo de permanência em horas, arredondando para cima
        long minutos = ChronoUnit.MINUTES.between(encontrado.getHoraEntrada(), horaSaida);

        // Converte minutos para horas, sempre arredondando para cima
        long horas = minutos / 60;
        if (minutos % 60 != 0) {
            horas++; // se sobrou minuto, conta como mais 1 hora
        }

        // Garante pelo menos 1 hora cobrada
        if (horas <= 0) {
            horas = 1;
        }

        // Calcula valor: primeira hora = 12, adicionais = 8
        double valor;
        if (horas == 1) {
            valor = PRIMEIRA_HORA;
        } else {
            long horasAdicionais = horas - 1;
            valor = PRIMEIRA_HORA + (horasAdicionais * HORA_ADICIONAL);
        }

        // Soma ao total arrecadado
        totalArrecadado += valor;

        // Remove o veículo da lista de estacionados
        veiculosEstacionados.remove(encontrado);

        return valor;
    }

    // Retorna quantas vagas ainda estão livres
    public int getVagasDisponiveis() {
        return capacidadeMaxima - veiculosEstacionados.size();
    }

    // Retorna quantas vagas estão ocupadas
    public int getVagasOcupadas() {
        return veiculosEstacionados.size();
    }

    // Mostra todos os veículos presentes (placa e hora de entrada)
    public void mostrarVeiculosPresentes() {
        if (veiculosEstacionados.isEmpty()) {
            System.out.println("Nenhum veículo estacionado no momento.");
        } else {
            System.out.println("Veículos atualmente no estacionamento:");
            for (Veiculo v : veiculosEstacionados) {
                System.out.println(v);
            }
        }
    }

    // Busca um veículo pela placa
    // Retorna o objeto Veiculo ou null se não encontrar
    public Veiculo buscarVeiculo(String placa) {
        for (Veiculo v : veiculosEstacionados) {
            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return v;
            }
        }
        return null;
    }

    // Retorna o total arrecadado até o momento
    public double getTotalArrecadado() {
        return totalArrecadado;
    }

    // Retorna a lista de veículos estacionados
    public ArrayList<Veiculo> getVeiculos() {
        return veiculosEstacionados;
    }
}
