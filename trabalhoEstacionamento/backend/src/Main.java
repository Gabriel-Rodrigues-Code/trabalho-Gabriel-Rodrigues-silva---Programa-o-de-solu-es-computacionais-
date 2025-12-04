// Classe principal: contém o menu e a interação com o usuário
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Define a capacidade máxima de vagas (pode ler do usuário se quiser)
        System.out.print("Informe a capacidade máxima do estacionamento: ");
        int capacidade = scanner.nextInt();
        scanner.nextLine(); // limpa o \n

        // Cria o objeto Estacionamento com a capacidade informada
        Estacionamento estacionamento = new Estacionamento(capacidade);

        int opcao;

        // Laço principal do menu
        do {
            System.out.println("\n=== MENU ESTACIONAMENTO ===");
            System.out.println("1 - Registrar entrada de veículo");
            System.out.println("2 - Registrar saída de veículo");
            System.out.println("3 - Mostrar quantidade de vagas");
            System.out.println("4 - Mostrar veículos presentes");
            System.out.println("5 - Pesquisar veículo por placa");
            System.out.println("6 - Relatório de faturamento");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = scanner.nextInt();
            scanner.nextLine(); // consome quebra de linha

            switch (opcao) {
                case 1:
                    // Registrar entrada
                    System.out.print("Informe a placa do veículo: ");
                    String placaEntrada = scanner.nextLine();

                    System.out.println("Deseja usar a hora atual ou informar manualmente?");
                    System.out.println("1 - Usar hora atual");
                    System.out.println("2 - Informar hora manualmente (formato: yyyy-MM-ddTHH:mm)");
                    int escolhaHora = scanner.nextInt();
                    scanner.nextLine(); // limpa

                    LocalDateTime horaEntrada;

                    if (escolhaHora == 1) {
                        // Captura a hora atual do sistema
                        horaEntrada = LocalDateTime.now();
                    } else {
                        // Usuário informa a data/hora completa
                        System.out.print("Digite a data e hora de entrada (ex: 2025-12-04T14:30): ");
                        String textoDataHora = scanner.nextLine();
                        // Converte o texto para LocalDateTime (usa o padrão ISO)
                        horaEntrada = LocalDateTime.parse(textoDataHora);
                    }

                    boolean entrou = estacionamento.registrarEntrada(placaEntrada, horaEntrada);
                    if (entrou) {
                        System.out.println("Entrada registrada com sucesso!");
                    } else {
                        System.out.println("Estacionamento cheio! Não foi possível registrar a entrada.");
                    }
                    break;

                case 2:
                    // Registrar saída
                    System.out.print("Informe a placa do veículo: ");
                    String placaSaida = scanner.nextLine();

                    // Usa hora atual como hora de saída
                    LocalDateTime horaSaida = LocalDateTime.now();

                    double valor = estacionamento.registrarSaida(placaSaida, horaSaida);

                    if (valor == -1) {
                        System.out.println("Veículo não encontrado no estacionamento.");
                    } else {
                        System.out.printf("Saída registrada. Valor a pagar: R$ %.2f%n", valor);
                    }
                    break;

                case 3:
                    // Mostrar vagas
                    System.out.println("Vagas ocupadas: " + estacionamento.getVagasOcupadas());
                    System.out.println("Vagas disponíveis: " + estacionamento.getVagasDisponiveis());
                    break;

                case 4:
                    // Mostrar veículos presentes
                    estacionamento.mostrarVeiculosPresentes();
                    break;

                case 5:
                    // Pesquisar veículo por placa
                    System.out.print("Informe a placa para pesquisa: ");
                    String placaBusca = scanner.nextLine();

                    Veiculo v = estacionamento.buscarVeiculo(placaBusca);
                    if (v == null) {
                        System.out.println("Veículo não está no estacionamento.");
                    } else {
                        System.out.println("Veículo encontrado!");
                        System.out.println("Placa: " + v.getPlaca());
                        System.out.println("Hora de entrada: " + v.getHoraEntrada());
                    }
                    break;

                case 6:
                    // Relatório de faturamento
                    System.out.printf("Total arrecadado até o momento: R$ %.2f%n",
                            estacionamento.getTotalArrecadado());
                    break;

                case 0:
                    System.out.println("Encerrando o sistema...");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }

        } while (opcao != 0);

        scanner.close();
    }
}
