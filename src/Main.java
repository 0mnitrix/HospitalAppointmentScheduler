import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AppointmentManager manager = new AppointmentManager();


        List<Appointment> loadedAppointments = FileHandler.loadAppointments();
        manager.setAppointments(loadedAppointments);

        boolean running = true;

        while (running) {
            System.out.println("\n=_=_= Hospital Appointment Scheduler =_=_=");
            System.out.println("1. Add Appointment");
            System.out.println("2. View Appointments");
            System.out.println("3. Update Appointment Status");
            System.out.println("4. Delete Appointment");
            System.out.println("5. Exit");
            System.out.println("6. View Report"); // добавлен пункт 6
            System.out.println("7. Export to CSV");
            System.out.println("8. Import from CSV");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    manager.addAppointment(scanner);
                    break;
                case "2":
                    manager.viewAppointments();
                    break;
                case "3":
                    manager.updateAppointment(scanner);
                    break;
                case "4":
                    manager.deleteAppointment(scanner);
                    break;
                case "5":
                    // Сохраняем данные при выходе
                    FileHandler.saveAppointments(manager.getAppointments());
                    System.out.println("Appointments saved. Goodbye!");
                    running = false;
                    break;
                case "6":
                    manager.generateReport(); // вызывает отчёт
                    break;
                case "7":
                    FileHandler.exportToCSV(manager.getAppointments(), "appointments_export.csv");
                    break;
                case "8":
                    List<Appointment> imported = FileHandler.importFromCSV("appointments_import.csv");
                    manager.setAppointments(imported);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }
}
